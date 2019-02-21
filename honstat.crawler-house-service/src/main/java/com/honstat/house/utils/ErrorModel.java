package com.honstat.house.utils;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.utils
 * @Description: TODO
 * @date 2018/11/22 15:22
 */
@Data
public class ErrorModel implements Serializable {
    public ErrorModel(Integer _code,String _msg){
        this.errcode=_code;
        this.errmsg=_msg;
    }
    private Integer errcode;
    private String errmsg;
    public static final ErrorModel define(Integer code, String message)
    {
        return new ErrorModel(code, message);
    }
}
