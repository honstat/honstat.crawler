package com.honstat.crawler.service;

import com.honstat.crawler.models.in.BaseQueueTaskIn;
import java.util.Collection;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.IInterface
 * @Description: 队列规范
 * @date 2019/1/17 14:54
 */
public interface IQueueTaskManager {
    /**队列管理器初始化**/
    void init();
    /**注入加载器，加载历史记录**/
    void LoadHistory(IHistoryLoad loader);
    /**运行管理器**/
    void run();
    /**存储队列数据**/
    void save();
    /**添加单个任务到队列**/
    void addTask(BaseQueueTaskIn in);
    /**返回队列个数**/
    Long size();
    /**批量添加任务**/
    Boolean addTasks(Collection<BaseQueueTaskIn> list);
}
