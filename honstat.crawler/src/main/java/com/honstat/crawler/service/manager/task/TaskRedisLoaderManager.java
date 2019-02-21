package com.honstat.crawler.service.manager.task;


import com.honstat.crawler.service.IHistoryLoad;
import com.honstat.crawler.models.in.BaseQueueTaskIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.manager.task
 * @Description: 从redis加载历史任务
 * @date 2019/1/13 18:31
 */
@Component
public class TaskRedisLoaderManager implements IHistoryLoad {
    @Autowired
    RedisTemplate redisTemplate;
    private final String rediskey = "TaskRedisLoaderManager_";

    @Override
    public List<BaseQueueTaskIn> init(String className) {
        Long count = redisTemplate.opsForList().size(rediskey+className);
        if (count > 0) {
            List<BaseQueueTaskIn> resultList = new ArrayList<>(count.intValue());
            for (int i = 0; i <count.intValue() ; i++) {
                try {
                    Object o=redisTemplate.opsForList().leftPop(rediskey+className,1, TimeUnit.SECONDS);
                    resultList.add((BaseQueueTaskIn)o);
                }catch (Exception e){
                 e.printStackTrace();
                }

            }
            return resultList;
        } else {
            return null;
        }
    }

    @Override
    public Boolean save(String className, List<BaseQueueTaskIn> list) {
        if (list == null || list.size() == 0) {
            return true;
        }
        Long res = redisTemplate.opsForList().leftPushAll(rediskey+className, list);
        return res > 0;
    }
}
