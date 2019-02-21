package com.honstat.crawler.service;
import com.honstat.crawler.models.in.BaseQueueTaskIn;
import java.util.List;
/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.IInterface
 * @Description: 历史记录加载与保存
 * @date 2019/1/13 17:33
 */
public interface IHistoryLoad {
    List<BaseQueueTaskIn> init(String className);
    Boolean save(String className, List<BaseQueueTaskIn> list);
}
