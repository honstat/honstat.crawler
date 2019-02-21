package com.honstat.crawler.models.out;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.interfaces.model.out
 * @Description: TODO
 * @date 2018/11/4 13:00
 */
@Data
public class XdataCommponent implements Serializable {
    private String name;
    private String type="bar";
    private List<String>data;
    public  XdataCommponent(String _name,String _type,List<String>_data){
        this.data=_data;
        this.name=_name;
        this.type=_type;
    }
}
