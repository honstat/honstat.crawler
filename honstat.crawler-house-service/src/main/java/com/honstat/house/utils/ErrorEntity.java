package com.honstat.house.utils;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.utils
 * @Description: TODO
 * @date 2018/10/15 13:49
 */
public class ErrorEntity {
    /** 错误代码 */
    private String errcode;
    /** 错误消息 */
    private String errmsg;

    private ErrorEntity(String code, String message)
    {
        this.errcode = code;
        this.errmsg = message;
    }

    public String getErrcode()
    {
        return errcode;
    }
    public String getErrmsg() {
        return errmsg;
    }


    public static final ErrorEntity define(String code, String message)
    {
        return new ErrorEntity(code, message);
    }

    @Override
    public String toString()
    {
        return String.valueOf(errcode);
    }
}