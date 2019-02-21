package com.honstat.crawler.models.out;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.interfaces.model.out
 * @Description: TODO
 * @date 2018/11/5 17:01
 */
@Data
public class TitleModelRes implements Serializable {
    private String title;
    private String subtext;
    public TitleModelRes(){}
    public TitleModelRes(String _title,String _sub){
        this.title=_title;
        this.subtext=_sub;
    }
}
