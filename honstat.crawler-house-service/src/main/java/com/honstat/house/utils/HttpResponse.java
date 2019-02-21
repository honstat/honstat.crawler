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
public class HttpResponse<T> implements Serializable {
    public HttpResponse(){
        setCode("0","成功");
    }
    public HttpResponse(ErrorEntity code,T data){
        this.code=code;
        this.data=data;
    }

    private transient T data;
    public ErrorEntity  code;

    public T getData() {
        return data;
    }

    public void setCode(String code,String msg) {
        this.code = ErrorEntity.define(code,msg);
    }
}
