package com.honstat.crawler.service.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.utils
 * @Description: TODO
 * @date 2018/12/13 11:14
 */
public  class CountUtils {

   static volatile Map<String,AtomicInteger> map=new HashMap<>();
    static {
        System.out.println("计数器执行实例化");
    }
    private static AtomicInteger instance(String key){
        if(map.containsKey(key)){
            return  map.get(key);
        }else {
            AtomicInteger temp= new AtomicInteger(0);
            map.put(key,temp);
            return temp;
        }

    }
    private  static  AtomicInteger getOne(String key){
       return instance(key);
    };
    //自增
    public static void addCount(String key){
        getOne(key).incrementAndGet();
    }
    public static Integer getCount(String key){
       return getOne(key).get();
    }
    //自减
    public static void decrement(String key){
        if(getOne(key).get()>0){
            getOne(key).decrementAndGet();
        }
    }
}
