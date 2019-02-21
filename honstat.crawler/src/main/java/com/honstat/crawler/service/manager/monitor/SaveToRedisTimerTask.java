package com.honstat.crawler.service.manager.monitor;

import com.honstat.crawler.service.manager.ProssorSaveMonitor;
import org.apache.log4j.Logger;
import java.util.TimerTask;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.manager.monitor
 * @Description: 定时执行任务：保存进度到redis
 * @date 2019/1/7 18:09
 */
public class SaveToRedisTimerTask extends TimerTask {
    Logger logger = Logger.getLogger(SaveToRedisTimerTask.class);
    ProssorSaveMonitor prossorSaveMonitor;

    public SaveToRedisTimerTask(ProssorSaveMonitor _psm) {
        this.prossorSaveMonitor = _psm;
    }

    /**
     * The action to be performed by this timer task.
     */
    @Override
    public void run() {
        logger.debug("prossorSaveMonitor.saveToRedis()");
        prossorSaveMonitor.saveToRedis();
    }

}
