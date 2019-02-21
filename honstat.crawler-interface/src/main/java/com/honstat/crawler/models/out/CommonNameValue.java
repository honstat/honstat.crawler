package com.honstat.crawler.models.out;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.interfaces.model.out
 * @Description: TODO
 * @date 2018/11/2 17:45
 */
@Data
public class CommonNameValue implements Serializable{
    private String item;
    private Number value;
public CommonNameValue(){}
public CommonNameValue(String _item, Number _value){
    this.item=_item;
    this.value=_value;
}
}
