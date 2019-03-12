package com.honstat.crawler.service.utils;

import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: house.crawler-parent
 * @Package com.honstat.crawler.service.utils
 * @Description: TODO
 * @date 2019/3/11 14:29
 */
public class ThreadPoolFactoryUtil {
   static Logger logger=Logger.getLogger(ThreadPoolFactoryUtil.class.getName());
   final static BlockingQueue<Runnable>runQueue=new LinkedBlockingQueue<>(1000);
    private   static ThreadPoolExecutor poolExecutor=new ThreadPoolExecutor(4,10,10000, TimeUnit.MILLISECONDS,runQueue,new ThreadPoolExecutor.CallerRunsPolicy());
    private static ScheduledThreadPoolExecutor delaypoolExecutor=new ScheduledThreadPoolExecutor(2,new ThreadPoolExecutor.CallerRunsPolicy());

    static {
        poolExecutor.prestartCoreThread();
        delaypoolExecutor.prestartCoreThread();
    }
    public static void execute(Runnable runnable){
        poolExecutor.execute(runnable);
    }
    public static void executeDelaye(Runnable runnable, Long time, TimeUnit unit){
        delaypoolExecutor.schedule(runnable,time,unit);
    }
    public static void executeDelaye(Runnable runnable){
        executeDelaye(runnable,3000L,TimeUnit.MILLISECONDS);
    }
    public static Integer getWaitSize(){
        return runQueue.size();
    }
}
