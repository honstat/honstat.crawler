package com.honstat.house.service.aop;


import com.honstat.crawler.models.in.CommonKeyValue;
import com.honstat.crawler.models.enums.WriteProssorStepType;
import com.honstat.crawler.service.models.ProssorMonitorContextManager;
import com.honstat.crawler.service.models.TwoHouseRunTimeProssorMonitorContext;
import com.honstat.crawler.models.in.HistoryStepInfoIn;
import com.honstat.crawler.models.in.HtmlLoadDetailIn;
import com.honstat.crawler.models.DistrictRunTimeInfo;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.aop
 * @Description: TODO
 * @date 2018/12/29 15:42
 */
@Component
@Aspect
public class WriteProssorDetailAspect {
    String keyformat = "%s_%s";
    Logger logger = Logger.getLogger(WriteProssorDetailAspect.class);

    @Around("@annotation(annotation)")
    public Object doTask(ProceedingJoinPoint joinPoint, NeedWriteProssorDetail annotation) throws Throwable {
        Long start = System.currentTimeMillis();
        Object returnRes = joinPoint.proceed(joinPoint.getArgs());
        if (joinPoint.getArgs()[0] instanceof HistoryStepInfoIn) {
            HistoryStepInfoIn param = ((HistoryStepInfoIn) joinPoint.getArgs()[0]);
            try {
                TwoHouseRunTimeProssorMonitorContext<DistrictRunTimeInfo> context = ProssorMonitorContextManager.getContext(String.format(keyformat, param.getCityId(), annotation.key()));
                DistrictRunTimeInfo current = context.getCurrent(DistrictRunTimeInfo.class,param.getDistrict());
                //记录
                if (annotation.step().equals(WriteProssorStepType.Create)) {
                    current.setExecTime(new Date());
                    CopyOnWriteArrayList<CommonKeyValue> waitList = null;
                    if(returnRes instanceof List){
                        waitList=new CopyOnWriteArrayList<>((ArrayList)returnRes);
                    }
                    if (waitList != null) {
                        current.setWaitTowns(waitList);
                    }
                    current.setDistrict(param.getDistrict());
                    current.setExecTime(new Date());
                } else if (annotation.step().equals(WriteProssorStepType.Update)) {

                     if (annotation.step().equals(WriteProssorStepType.Update) && annotation.sort() == 99) {
                        //移除当前执行 的乡镇
                        if (param.getTown() != null && current.getWaitTowns() != null && current.getWaitTowns().size() > 0) {
                            current.getWaitTowns().removeIf(x -> x.getValue().contains(param.getTown()));
                        }
                        //累计页数
                         current.addPageSize(param.getPageSize());
                        //累计链接数
                         current.addLinkCount((Long)returnRes);
                    } else {
                        current.getTimes().add(System.currentTimeMillis() - start);
                    }
                } else if (annotation.step().equals(WriteProssorStepType.Complete)) {
                    context.archive();
                }
                return returnRes;
            } catch (Exception e) {
                logger.error(e.getMessage());
                return returnRes;
            }

        }else if(joinPoint.getArgs()[0] instanceof HtmlLoadDetailIn){
            HtmlLoadDetailIn param=(HtmlLoadDetailIn)joinPoint.getArgs()[0];
            TwoHouseRunTimeProssorMonitorContext<DistrictRunTimeInfo> context = ProssorMonitorContextManager.getContext(String.format(keyformat, param.getCityId(), annotation.key()));
            DistrictRunTimeInfo current = context.getCurrent(DistrictRunTimeInfo.class,param.getDistrict());
            if(current!=null&&annotation.sort()==100){
                current.getTimes().add(System.currentTimeMillis() - start);
            }
        }
            return returnRes;
    }
}
