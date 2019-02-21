package com.honstat.crawler.models.enums;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.model.enums
 * @Description: 流程进度操作类型
 * @date 2018/12/29 15:48
 */
public enum WriteProssorStepType {
    Create(1),Update(2),Complete(3);

    private int value;

    private WriteProssorStepType(int value) {
        this.value = value;
    }
    public int getValue(){
        return  value;
    }
}
