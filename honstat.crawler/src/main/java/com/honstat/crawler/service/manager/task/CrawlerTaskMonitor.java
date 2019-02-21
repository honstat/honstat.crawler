package com.honstat.crawler.service.manager.task;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.manager
 * @Description: 爬虫管理，整合爬网页功能和监控功能
 * @date 2019/1/2 11:27
 */
public class CrawlerTaskMonitor {
    Logger logger = Logger.getLogger(CrawlerTaskMonitor.class);
    private final LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>(50) ;
    ExecutorService executorService;
    public CrawlerTaskMonitor(Integer theadNums){
        executorService= Executors.newFixedThreadPool(theadNums);
    }
    public void initCustomProssorMethod(List<Runnable> list) {
        if (list != null && list.size() > 0) {
            this.queue.addAll(list);
        }
    }


    /**
     * 运行时 随时加任务
     **/
    public void addTask(Runnable task) {
        queue.add(task);
        execCount = 0;
       if(isComplete){
           isComplete=false;
           doTask();
       }
    }

    private volatile boolean isComplete = false;

    /**
     * 设定停止标记
     **/
    public void complete() {
        isComplete = true;
    }

    /**
     * 设定没有主动调用complete()时停止条件，一旦运行时临时添加，重置标记
     **/
    int execCount = 0;

    public boolean doTask() {
        //初始化环境before
        logger.info("start run doTask");

        //执行

        while (!isComplete) {
            while (queue.iterator().hasNext()) {
                Runnable task = queue.poll();
                //按排序执行
                if (task != null) {
                    try {
                        executorService.submit(task);
                    }catch (Exception e){
                        logger.error("task exec error:"+e.getMessage());
                    }
                }
            }
            execCount++;
            try {
                logger.debug("queue no meage sleeping...");
                Thread.sleep(1000);
            } catch (InterruptedException e) {

            }
//            if (execCount > 100) {
//                isComplete=true;
//                break;
//            }
        }
        logger.info("isComplete exit");
        //结束处理after
        return true;
    }
}
