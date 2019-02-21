package com.honstat.crawler.service.manager;
import com.honstat.crawler.service.manager.monitor.SaveToRedisTimerTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.manager
 * @Description: TODO
 * @date 2018/12/24 18:22
 */
@Lazy
@Component
public class ProssorSaveMonitor {
    @Autowired
    private RedisTemplate redisTemplate;
    private final String rediskey = "ProssorSaveMonitor_current";
    private ProssorMonitorManager prossorMonitorManager;
    Timer sysInfoGetTimerCustom = null;
    public ProssorSaveMonitor() {
        prossorMonitorManager = ProssorMonitorManager.instance();
    }
    @PostConstruct
    public void init() {
        prossorMonitorManager.setLoadDataMap(loadForRedis());
    }
     /***定时存储任务*/
    public void registerTimerSave(Integer minitue) {
        if(sysInfoGetTimerCustom==null){
            sysInfoGetTimerCustom = new Timer("SaveToRedisTimerTask");
            sysInfoGetTimerCustom.schedule(new SaveToRedisTimerTask(this), 10 * 1000, 1000 * 60 * minitue);
        }
    }

    public Map<String, Object> getByContainsKey(String key) {
        Map<String, Object> res = null;
        Map<String, Object> map = prossorMonitorManager.getAll();
        if (map != null && map.size() > 0) {
            res = new HashMap<>(10);
            for (String mapkey : map.keySet()) {
                if (mapkey!=null&&mapkey.contains(key)) {
                    res.put(mapkey, map.get(mapkey));
                }
            }

        }
        return res;
    }

    public boolean saveToRedis() {

        redisTemplate.opsForValue().set(rediskey, prossorMonitorManager.getAll());
        return true;
    }

    public Map getMaps() {
        return prossorMonitorManager.getAll();
    }

    public boolean clear() {
       Map<String,Object> map=prossorMonitorManager.getAll();
        if(map!=null&&map.size()>0){
            map.keySet().forEach(x->{
                prossorMonitorManager.removeKey(x);
            });
        }
    redisTemplate.opsForValue().set(rediskey, null);
        return true;
    }

    private Map<String, Object> loadForRedis() {
        try {
            Object o = redisTemplate.opsForValue().get(rediskey);
            if (o != null) {
                return (Map<String, Object>) o;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Map<String, Object> getCollectionByKey(String key) {
        Map<String, Object> map = prossorMonitorManager.getAll();
        Map<String, Object> temp = new HashMap<>();
        if (map != null) {
            for (String strkey :
                    map.keySet()) {
                if (strkey.indexOf(key) > -1) {
                    temp.put(strkey, map.get(strkey));
                }
            }
        }
        return map;
    }

    public Object getValue(String key) {

        return prossorMonitorManager.getValue(key);
    }

    public boolean addValue(String key, Object o) {
        return prossorMonitorManager.addToProssor(key, o);
    }

    public boolean saveToFile() {
        return true;
    }

    public boolean clearKeys(List<String> keys) {
        return prossorMonitorManager.clearKeys(keys);
    }

    public boolean removeKey(String key) {
        return prossorMonitorManager.removeKey(key);
    }

    public Integer getSize() {
        return prossorMonitorManager.getSize();
    }

}
