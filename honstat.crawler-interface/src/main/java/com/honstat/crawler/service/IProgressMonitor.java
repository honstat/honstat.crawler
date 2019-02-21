package com.honstat.crawler.service;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.IInterface
 * @Description: 进程管理接口
 * @date 2018/12/24 17:27
 */
public interface IProgressMonitor  {
    /**获取进度**/
    IProgressMonitor getProssorMonitor();
    /**添加进度**/
    boolean addToProgress(String className, Object value);
}
