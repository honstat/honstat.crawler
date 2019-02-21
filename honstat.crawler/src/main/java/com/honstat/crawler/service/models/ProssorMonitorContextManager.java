package com.honstat.crawler.service.models;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.model.out.monitor
 * @Description: TODO
 * @date 2018/12/28 18:26
 */
public final class ProssorMonitorContextManager implements Serializable{
   private static Map<String,TwoHouseRunTimeProssorMonitorContext>map=new HashMap<>();
   public static boolean addKey(String key,TwoHouseRunTimeProssorMonitorContext context)throws  Exception{
       if(map.containsKey(key)){
           throw new Exception("已存在相同key");
       }else {
           synchronized (ProssorMonitorContextManager.class){
               if(map.containsKey(key)){
                   throw new Exception("已存在相同key");
               }else {
                   map.put(key,context);
               }
           }
       }
       return true;
   }
   public static TwoHouseRunTimeProssorMonitorContext getContext(String key){
       TwoHouseRunTimeProssorMonitorContext context=  map.get(key);
       if(context!=null){
           return context;
       }else {
           context=new TwoHouseRunTimeProssorMonitorContext();
           try {
               addKey(key,context);
           }catch (Exception e){
               e.printStackTrace();
           }

       }
       return context;
   }


}
