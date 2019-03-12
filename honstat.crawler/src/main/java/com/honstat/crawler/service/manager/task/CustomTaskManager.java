package com.honstat.crawler.service.manager.task;


import com.honstat.crawler.service.IHistoryLoad;
import com.honstat.crawler.service.IQueueTaskManager;
import com.honstat.crawler.service.IQuqueTaskService;
import com.honstat.crawler.service.utils.ThreadPoolFactoryUtil;
import org.apache.log4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.manager.task
 * @Description: TODO
 * @date 2019/1/17 12:39
 */
public class CustomTaskManager {
    static Logger logger = Logger.getLogger(CustomTaskManager.class);
    /***
     * 采用ConcurrentHashMap集合进行存储
     * Class 作为key ---对象的类型
     * Object 作为value---对象的实例化
     * 实现对象的类型和对象的实例化 一一对应
     */
  static IHistoryLoad historyLoad = null;

    private static final ConcurrentMap<String, IQueueTaskManager> map = new ConcurrentHashMap<>(5);
     /**
      * 指定队列数据加载器，可以自己实现，决定存储方式（redis,kafka,other）
      * **/
    public static void setHistoryLoad(IHistoryLoad _historyLoader) {
        if(historyLoad==null){
            historyLoad = _historyLoader;
        }

    }
    /**
     * 获取单例：队列管理实例，重载方法指定线程数量
     * */
    public static IQueueTaskManager getSingleton(Class entityCls, IQuqueTaskService constructorService){
       return getSingleton(entityCls,constructorService,1);
    }
    public static IQueueTaskManager getSingleton(Class entityCls, IQuqueTaskService constructorService,int theads) {
        /***
         * 从map中取出对象的相对应的实例
         *
         * 为减少对map的操作，在此处使用局部变量ob  --- 符合优化性能要求
         */
        if(entityCls==null||constructorService==null){
            logger.error("IQueueTaskManager  constructor method param is null!");
          return null;
        }
        IQueueTaskManager ob = map.get(entityCls.getName());
        /***
         * 对该对象的实例进行null判断
         */
        if (ob == null) {
            logger.info(String.format("not find [ %s ] instance getSingleton ing...", entityCls.getName()));
            /***
             * 为使用安全的map及其操作  设置同步锁
             */
            synchronized (map) {
                ob = new QueueTaskManager(constructorService, entityCls,theads);
                map.put(entityCls.getName(), ob);
            }
            ThreadPoolFactoryUtil.execute(new Runnable() {
                /***
                 * 做初始化工作
                 */
                @Override
                public void run() {
                    map.get(entityCls.getName()).init();
                    map.get(entityCls.getName()).LoadHistory(historyLoad);
                    map.get(entityCls.getName()).run();
                }
            });
        }
        return ob;
    }

    private static void init() {
        if (historyLoad != null) {
            for (String taskKey : map.keySet()) {
                map.get(taskKey).LoadHistory(historyLoad);
            }
        } else {
            logger.error("not specified historyLoad instance !");
        }
    }
    /**
     * 在需要的时候保存队列
     * **/
public static  void saveQueue(){
         map.keySet().forEach(x->{
             map.get(x).save();
         });
}
}
