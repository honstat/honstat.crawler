package com.honstat.crawler.service.manager.task;


import com.honstat.crawler.service.IHistoryLoad;
import com.honstat.crawler.service.IQuqueTaskService;
import com.honstat.crawler.models.in.BaseQueueTaskIn;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.manager.task
 * @Description: TODO
 * @date 2019/1/13 18:17
 */
public class TaskHistoryLoaderManger implements Runnable {
    Logger logger = Logger.getLogger(TaskHistoryLoaderManger.class);

    public TaskHistoryLoaderManger(IHistoryLoad loader, IQuqueTaskService service) {
        this.historyLoad = loader;
        this.service = service;
    }

    IQuqueTaskService service;
    IHistoryLoad historyLoad;

    @Override
    public void run() {
        if (historyLoad == null) {
            logger.error("historyLoad is null  can not run historyLoad.init()");
        }
        List<BaseQueueTaskIn> list = historyLoad.init(service.getClass().getSimpleName());
        if (list != null) {
            logger.info("TaskHistoryLoaderManger load  history list size:" + list.size());
            CustomTaskManager.getSingleton(QueueTaskManager.class, service).addTasks(list);

        } else {
            logger.info("TaskHistoryLoaderManger load  history list size 0");
        }
    }

}
