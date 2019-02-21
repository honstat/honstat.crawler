package com.honstat.house.service.aop;

import com.honstat.crawler.models.enums.WriteProssorStepType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.aop
 * @Description: 记录进度使用
 * @date 2018/12/29 15:41
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NeedWriteProssorDetail {
    String key();
    WriteProssorStepType step() default WriteProssorStepType.Update;
    int sort() default 0;
}
