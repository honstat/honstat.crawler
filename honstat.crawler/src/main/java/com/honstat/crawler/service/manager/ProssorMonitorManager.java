package com.honstat.crawler.service.manager;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.manager
 * @Description: TODO
 * @date 2018/12/24 17:22
 */
public class ProssorMonitorManager {

    private volatile Map<String, Object> dataMap = null;
    private volatile static ProssorMonitorManager _instance = null;

    private ProssorMonitorManager() {
        System.out.println("执行了构造方法");
    }

    public static ProssorMonitorManager instance() {
        if (_instance != null) {
        } else {
            synchronized (ProssorMonitorManager.class) {
                if (_instance == null) {
                    _instance = new ProssorMonitorManager();
                }
            }
        }
        return _instance;
    }

    public boolean setLoadDataMap(Map<String, Object> data) {
        synchronized (ProssorMonitorManager.class) {
            if (dataMap == null) {
                dataMap = new HashMap<>(20);
            }
            dataMap = data;
        }
        return true;
    }

    public Object getValue(String key) {
        if (key != null && dataMap.containsKey(key)) {
            return dataMap.get(key);
        } else {
            return null;
        }
    }

    public boolean clearKeys(List<String> keys) {
        if (keys == null || keys.size() == 0) {
            return true;
        }
        for (String key :
                keys) {
            if (dataMap.containsKey(key)) {
                dataMap.remove(key);
            }
        }
        return true;
    }

    public boolean removeKey(String key) {

        if (key != null && dataMap.containsKey(key)) {
            dataMap.remove(key);
        }

        return true;
    }

    public boolean addToProssor(String classname, Object value) {
        dataMap.put(classname, value);
        return true;
    }

    public Map<String, Object> getAll() {
        return dataMap;
    }

    public Integer getSize() {
        return dataMap.size();
    }

}
