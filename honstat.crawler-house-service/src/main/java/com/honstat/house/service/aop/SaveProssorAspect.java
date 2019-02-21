package com.honstat.house.service.aop;


import com.honstat.crawler.service.manager.ProssorSaveMonitor;
import com.honstat.crawler.models.in.HistoryStepInfoIn;
import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.aop
 * @Description: TODO
 * @date 2018/12/25 10:56
 */
@Component
@Aspect
@Order(-9000)
public class SaveProssorAspect {


    public SaveProssorAspect() {
    }

    final String keyFormat = "%s_%s_%s";
    Logger logger = Logger.getLogger(SaveProssorAspect.class);
    @Autowired
    ProssorSaveMonitor prossorSaveMonitor;

    @Before("@annotation(annotation)")
    public void before(JoinPoint joinPoint, NeedSaveProssor annotation) {
        Object[] args = joinPoint.getArgs();
        String key = annotation.key();
        if (args != null && args[0] != null) {
            if (args[0] instanceof HistoryStepInfoIn) {
                HistoryStepInfoIn o = (HistoryStepInfoIn) args[0];
                if(o!=null){
                     o.setKey(String.format(keyFormat,o.getDistrict(),o.getCityId(),key));
                    if(o.getPageSize()>0&&o.getPageSize()==o.getPageIndex()){
                        o.setIsComplete(true);
                    }
                    prossorSaveMonitor.addValue(o.getKey(), o);
                }

            }
        } else {
            logger.warn("before()没有取得入参");
        }
    }

    @Around("@annotation(annotation)")
    public Object doTask(ProceedingJoinPoint joinPoint, NeedSaveProssor annotation)throws Throwable {
          Object res=   joinPoint.proceed(joinPoint.getArgs());
        return res;
    }
}
