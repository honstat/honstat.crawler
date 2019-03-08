package com.honstat.crawler.service.manager.task;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.*;

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

    private  final  LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>(1000) ;
    ThreadPoolExecutor poolExecutor;

    public CrawlerTaskMonitor(Integer theadNums){
        poolExecutor=new ThreadPoolExecutor(theadNums,10,10000, TimeUnit.MILLISECONDS,queue);
        poolExecutor.prestartCoreThread();

    }
    public void initCustomProssorMethod(List<Runnable> list) {
        if (list != null && list.size() > 0) {
            this.queue.addAll(list);
        }
    }
   volatile boolean state;

    /**
     * 运行时 随时加任务
     **/
    public  void addTask(Runnable task) {
        queue.add(task);
        if(!state){
            notifyAll();
        }
       if(isComplete){
           /**重新激活**/
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
                        poolExecutor.execute(task);
                    }catch (Exception e){
                        logger.error("task exec error:"+e.getMessage());
                    }
                }
            }
            try {
                logger.debug("queue no meage will wait()...");
             //   Thread.sleep(1000);
                state=false;
                wait();
            } catch (InterruptedException e) {

            }
        }
        logger.info("isComplete exit");
        //结束处理after
        return true;
    }
}
