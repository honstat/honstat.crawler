package com.honstat.crawler.service.manager.monitor;

import com.alibaba.fastjson.JSON;
import com.honstat.crawler.service.models.ProssorMonitorContextManager;
import com.honstat.crawler.service.models.TwoHouseRunTimeProssorMonitorContext;
import org.apache.log4j.Logger;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.manager.monitor
 * @Description: TODO
 * @date 2019/1/3 14:58
 */
public abstract class AbstractProgressProcessor {
    Logger logger=Logger.getLogger(AbstractProgressProcessor.class);
   protected TwoHouseRunTimeProssorMonitorContext instance;
 private String contextKey="";

    public void init(String key) {
        instance = ProssorMonitorContextManager.getContext(key);
        contextKey=key;
    }

    public abstract void execute();

    public void archive() {
        logger.debug("run archive()"+JSON.toJSONString(instance.getCurrent(AbstractProgressProcessor.class,contextKey)));
        instance.archive();
    }

    public void complete() {
        logger.debug("run complete() current:"+JSON.toJSONString(instance.getCurrent(AbstractProgressProcessor.class,contextKey)));
    }
}
