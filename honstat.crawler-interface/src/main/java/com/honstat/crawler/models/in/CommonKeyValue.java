package com.honstat.crawler.models.in;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.interfaces.model.out.common
 * @Description: TODO
 * @date 2019/1/4 15:39
 */
@Data
public class CommonKeyValue implements Serializable {
    private String key;
    private String value;
    public CommonKeyValue(String _key, String _value){
        this.key=_key;
        this.value=_value;
    }
}
