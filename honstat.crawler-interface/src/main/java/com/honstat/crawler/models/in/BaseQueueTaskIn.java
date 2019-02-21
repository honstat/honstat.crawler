package com.honstat.crawler.models.in;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.interfaces.model.in
 * @Description: 任务管理中心指定实体的父类，继承它能接受管理分配
 * @date 2019/1/16 18:54
 */
@Data
public class BaseQueueTaskIn implements Serializable {
   private String taskKey;
}
