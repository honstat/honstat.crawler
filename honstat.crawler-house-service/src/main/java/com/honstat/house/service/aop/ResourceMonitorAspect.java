package com.honstat.house.service.aop;

import com.honstat.crawler.models.in.GetCommunityRecordIn;
import com.honstat.crawler.models.in.HtmlLoadDetailIn;
import com.honstat.crawler.service.utils.CountUtils;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.aop
 * @Description: TODO
 * @date 2018/12/26 14:33
 */
@Component
@Aspect
public class ResourceMonitorAspect {
    Logger logger = Logger.getLogger(ResourceMonitorAspect.class);
    final String keyFormat = "%s_current";

    @Around("@annotation(annotation)")
    public Object doTask(ProceedingJoinPoint joinPoint, NeedResourceMonitor annotation) throws Throwable {
        long start = System.currentTimeMillis();
        Object res = joinPoint.proceed(joinPoint.getArgs());
        logger.info(joinPoint.getSignature() + " exec times：" + (System.currentTimeMillis() - start) + " ms");
        Object[] args = joinPoint.getArgs();
        if (args != null && args[0] != null) {
            if (args[0] instanceof HtmlLoadDetailIn) {
                HtmlLoadDetailIn o = (HtmlLoadDetailIn) args[0];
                CountUtils.addCount(String.format(keyFormat, o.getTaskKey()));
            }else if(args[0] instanceof GetCommunityRecordIn){
                GetCommunityRecordIn o = (GetCommunityRecordIn) args[0];
                CountUtils.addCount(String.format(keyFormat, o.getTaskKey()));
            }
        } else {
            logger.warn("after() no input param!");
        }

        return res;
    }

}
