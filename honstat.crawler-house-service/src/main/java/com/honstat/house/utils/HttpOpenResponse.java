package com.honstat.house.utils;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.utils
 * @Description: TODO
 * @date 2018/10/15 13:47
 */
@Data
public class HttpOpenResponse implements Serializable {
    public HttpOpenResponse(){
        setCode(0,"success");
    }
    public HttpOpenResponse(ErrorModel code){
        this.code=code;
    }

    public ErrorModel  code;

    public void setCode(Integer code,String msg) {
        this.code = ErrorModel.define(code,msg);
    }
}
