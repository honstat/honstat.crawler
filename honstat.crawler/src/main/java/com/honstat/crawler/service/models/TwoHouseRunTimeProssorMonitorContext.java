package com.honstat.crawler.service.models;

import org.apache.log4j.Logger;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.model.out.monitor
 * @Description: 执行过程监控上下文
 * @date 2018/12/28 17:55
 */

public class TwoHouseRunTimeProssorMonitorContext<T> implements Serializable {
    Logger logger = Logger.getLogger(TwoHouseRunTimeProssorMonitorContext.class);
    private Map<String, T> map = new TreeMap<>();

    public T getCurrent(Class<T> cls,String key) {
        if (map.containsKey(key)) {
            return map.get(key);
        }
        synchronized (TwoHouseRunTimeProssorMonitorContext.class) {
            if (map.containsKey(key)) {
                return map.get(key);
            } else {
                try {
                    T t = cls.newInstance();
                    map.put(key, t);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }


            }
        }
        return map.get(key);
    }

    /**
     * 归档
     * 将current数据写入map,重置current
     **/
    public void archive() {

    }
}
