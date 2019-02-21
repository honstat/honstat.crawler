package com.honstat.crawler.service.manager.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import com.honstat.crawler.service.IHistoryLoad;
import com.honstat.crawler.service.IQueueTaskManager;
import com.honstat.crawler.service.IQuqueTaskService;
import com.honstat.crawler.models.in.BaseQueueTaskIn;
import com.honstat.crawler.service.utils.CountUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.manager.task
 * @Description: 内存队列任务管理器(主要功能：加载历史记录，分配任务，存储队列到介质)
 * @date 2019/1/11 13:48
 */
public class QueueTaskManager implements IQueueTaskManager {
    private final String countKeyFormat="%s_total";
    private final Integer MAXTHEADS=8;
    private final Integer MINTHEADS=1;
    private  Logger logger = Logger.getLogger(QueueTaskManager.class);
    private IHistoryLoad historyLoad = null;
    private Integer queueSaveLimit = 10000;
    private Class entityClass = null;
    private  Queue<BaseQueueTaskIn> queues = new LinkedBlockingQueue<>(1000);

    public Boolean addTasks(Collection<BaseQueueTaskIn> list) {
        list.forEach(x->{
            CountUtils.addCount(String.format(countKeyFormat, x.getTaskKey()));
        });
        return queues.addAll(list);
    }

    private   ExecutorService executorService = null;
    private volatile Boolean runState = false;

   private IQuqueTaskService ququeTaskService;
    private Integer sleepBasic = 2000;//基数
    private  Integer step = 0;
    private int[] arr = new int[10];

    private Integer getNextSleepTime() {
        if (step >= arr.length) {
            step = -1;
            runState = false;
        }
        return arr[step++] * sleepBasic;
    }

    public QueueTaskManager(IQuqueTaskService _taskService, Class entityClass,Integer theads) {
        if(theads<MINTHEADS){
            theads=MINTHEADS;
        }else if(theads>MAXTHEADS){
            theads=MAXTHEADS;
        }
        executorService = Executors.newFixedThreadPool(theads);
        ququeTaskService = _taskService;
        this.entityClass = entityClass;
        initArr();
    }
    private void initArr() {
        int a = 1, b = 1, c = 0;
        for (int i = 0; i < 10; i++) {
            c = a + b;
            a = b;
            b = c;
            arr[i] = c;
        }
    }

    public void addTask(BaseQueueTaskIn in) {
        CountUtils.addCount(String.format(countKeyFormat, in.getTaskKey()));
        queues.add(in);
    }

    @Override
    public Long size() {
        return  Long.valueOf(queues.size());
    }

    @Override
    public void init() {
        logger.info(entityClass.getName()+" taskManager init() run.....");
    }

    @Override
    public void LoadHistory(IHistoryLoad loader) {
        historyLoad = loader;
        List<BaseQueueTaskIn> historys = loader.init(entityClass.getName());
        if (historys != null && historys.size() > 0) {
            addTasks(historys);
        }
    }

    @Override
    public void run() {
        logger.info(entityClass.getName() + ": QueueTaskManager runing....");
        runState = true;
        while (runState) {
            try {
                while (!queues.isEmpty()) {
                    try {
                        if (queues.size() > queueSaveLimit) {
                            saveQueueToRedis();
                            continue;
                        }
                        BaseQueueTaskIn param = queues.poll();
                        if (param != null) {
                            executorService.submit(new Runnable() {
                                @Override
                                public void run() {
                                    ququeTaskService.doHtmlTask(param);
                                }
                            });
                        }
                    } catch (Exception e) {
                        logger.error(e);
                    }
                }
                Thread.sleep(sleepBasic);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }catch (Exception e){
                logger.error(e);
            }
        }
    }

    @Override
    public void save() {
        saveQueueToRedis();
    }


    public boolean saveQueueToRedis() {
        Boolean isSaveSucssess = false;
        if (historyLoad != null && queues.size() > 10) {
            List<BaseQueueTaskIn> list = new ArrayList<>(queues.size());
            synchronized (QueueTaskManager.class) {
                if (queues.size() > 10) {
                    list = queues.stream().collect(Collectors.toList());
                    queues.clear();
                }
            }
            if (list.size() > 0) {
                try {
                    isSaveSucssess = historyLoad.save(entityClass.getName(), list);
                    logger.info(entityClass.getName()+ "queue manager exec  historyLoad.save()  saved size:" + list.size() + " model 1=" + JSON.toJSONString(list.get(0)));
                } catch (Exception e) {
                    logger.error(" historyLoad.save() error :" + e.getMessage());
                    logger.warn(JSONArray.toJSONString(list));

                }
            }
        }else {
            if(historyLoad==null){
                logger.warn(entityClass.getName()+ " queue manager not specified  historyLoad ." );
            }
            logger.info(entityClass.getName()+ " queue manager exec  historyLoad.save() but no message need save:" );
        }

        return isSaveSucssess;
    }
}
