package com.honstat.house.utils;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.utils
 * @Description: TODO
 * @date 2018/12/12 14:30
 */
public class ServiceException  extends Exception{
    private String errmsg;
    private Integer code;
    public ServiceException(String errmsg){
        this.code=500;
        this.errmsg=errmsg;
    }
    public ServiceException(Integer errcode,String errmsg){
        this.code=errcode;
        this.errmsg=errmsg;
    }
}
