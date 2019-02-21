package com.honstat.house.service.aop;

import com.honstat.crawler.service.models.ProssorMonitorContextManager;
import com.honstat.crawler.service.models.TwoHouseRunTimeProssorMonitorContext;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.aop
 * @Description: 专注于幂等校验
 * @date 2018/12/27 19:02
 */
@Component
@Aspect
public class RequestIdempotentAspect {
    @Autowired
    RedisTemplate redisTemplate;
    Logger logger = Logger.getLogger(RequestIdempotentAspect.class);

    @Around("@annotation(annotation)")
    public Object doTask(ProceedingJoinPoint joinPoint, NeedIdempotentFilter annotation) throws Throwable {
        long start = System.currentTimeMillis();
        Object[] args = joinPoint.getArgs();

        try {
            if (args != null && args[0] != null) {
                String inputParam = String.valueOf(args[0]);
                String reqkey = String.format("%s_%s", inputParam, annotation.key());
                Object redisValue = redisTemplate.opsForValue().get(reqkey);
                if (redisValue == null) {
                    //参数第一次调用，设x秒钟的有效期
                    redisTemplate.opsForValue().set(reqkey, new Date(), annotation.seconds(), TimeUnit.SECONDS);
                    TwoHouseRunTimeProssorMonitorContext context = ProssorMonitorContextManager.getContext(reqkey);
                    if (context == null) {
                        //初始化
                        context = new TwoHouseRunTimeProssorMonitorContext();
                        try {
                            ProssorMonitorContextManager.addKey(reqkey, context);
                        } catch (Exception e) {
                            e.printStackTrace();
                            logger.error("初始化TwoHouseRunTimeProssorMonitorContext失败", e);
                        }

                    }
                    return joinPoint.proceed(joinPoint.getArgs());
                } else {
                    //多次调用什么都不做
                    logger.info(joinPoint.getTarget() + "未通过幂等校验，不调用方法");
                }

            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        logger.info(joinPoint.getSignature() + "执行耗时：" + (System.currentTimeMillis() - start) + " ms");
        return null;
    }

}
