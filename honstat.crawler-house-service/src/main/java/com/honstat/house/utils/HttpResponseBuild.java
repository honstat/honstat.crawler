package com.honstat.house.utils;

import java.io.Serializable;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.utils
 * @Description: TODO
 * @date 2018/10/15 13:53
 */
public class HttpResponseBuild<T> implements Serializable {
    private  transient T data;
    public  ErrorEntity  code;
    public  HttpResponse<T> build(){
        if(code==null){
            this.code=ErrorEntity.define("0","成功");
        }
        return new HttpResponse<T>(this.code,this.data);
    }
    public HttpResponseBuild setData(T data){
        this.data=data;
        return  this;
    }
    public HttpResponseBuild setCode(ErrorEntity code){
        this.code=code;
        return  this;
    }
}