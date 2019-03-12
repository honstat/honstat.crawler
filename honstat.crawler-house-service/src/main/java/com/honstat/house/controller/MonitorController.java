package com.honstat.house.controller;

import com.honstat.crawler.service.IQueueTaskManager;
import com.honstat.crawler.models.in.CommonKeyValue;
import com.honstat.crawler.service.manager.ProssorSaveMonitor;
import com.honstat.crawler.service.manager.SystemInfoMonitorManager;
import com.honstat.crawler.service.manager.task.CustomTaskManager;
import com.honstat.crawler.service.models.ProssorMonitorContextManager;
import com.honstat.crawler.service.models.TwoHouseRunTimeProssorMonitorContext;
import com.honstat.crawler.service.utils.CountUtils;

import com.honstat.crawler.service.utils.ThreadPoolFactoryUtil;
import com.honstat.house.manager.fang.FangTianXiaDoHtmlTask;
import com.honstat.house.manager.fang.FangTianXiaHouseInfoTask;

import com.honstat.crawler.models.in.GetCommunityRecordIn;
import com.honstat.crawler.models.in.HtmlLoadDetailIn;
import com.honstat.crawler.models.DistrictRunTimeInfo;
import com.honstat.house.utils.HttpResponse;
import com.honstat.house.utils.HttpResponseBuild;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.controller
 * @Description: 监控类
 * @date 2018/12/25 15:02
 */
@RequestMapping("/monitor")
@RestController
public class MonitorController {
    @Autowired
    private ProssorSaveMonitor prossorSaveMonitor;
    @Autowired
    private SystemInfoMonitorManager systemInfoMonitorManager;
    @Autowired
    private RedisTemplate redisTemplate;



    @Autowired
    private FangTianXiaHouseInfoTask fangHouseInfoTask;
    @Autowired
    private FangTianXiaDoHtmlTask fangCommuityTask;

    @ApiOperation(value = "获取保存的map")
    @RequestMapping(value = "/getDataMap", method = RequestMethod.GET)
    public HttpResponse getDataMap() {
        return new HttpResponseBuild().setData(prossorSaveMonitor.getMaps()).build();
    }

    @ApiOperation(value = "获取保存map的元素数量")
    @RequestMapping(value = "/getDataMapSize", method = RequestMethod.GET)
    public HttpResponse getDataMapSize() {
        return new HttpResponseBuild().setData(prossorSaveMonitor.getSize()).build();
    }

    @ApiOperation(value = "根据指定key取值")
    @RequestMapping(value = "/getDataMapValueByKey", method = RequestMethod.GET)
    public HttpResponse getDataMapValueByKey(@RequestParam String key) {
        return new HttpResponseBuild().setData(prossorSaveMonitor.getValue(key)).build();
    }

    @ApiOperation(value = "匹配指定key的map元素")
    @RequestMapping(value = "/getDataMapValueByContainsKey", method = RequestMethod.GET)
    public HttpResponse getDataMapByContainsKey(@RequestParam String key) {
        return new HttpResponseBuild().setData(prossorSaveMonitor.getByContainsKey(key)).build();
    }

    @RequestMapping(value = "/getKeyByCount", method = {RequestMethod.GET})
    public HttpResponse getKeyByCount(@RequestParam String key) {
        Integer mq_current = CountUtils.getCount(key);
        return new HttpResponseBuild().setData(mq_current).build();
    }

    @RequestMapping(value = "/getRedisTaskCount", method = {RequestMethod.GET})
    public HttpResponse getRedisTaskCount(@RequestParam String key) {
        Long current =redisTemplate.opsForList().size(key);
        return new HttpResponseBuild().setData(current).build();
    }

    @RequestMapping(value = "/getCountByKeys", method = {RequestMethod.GET})
    public HttpResponse getCountByKeys(@RequestParam String keys) {
        if (keys != null && keys.length() > 1) {
            String[] keyArray = keys.split(",");
            List<CommonKeyValue> result = new ArrayList<>(keyArray.length);
            for (String key : keyArray) {
                Integer count = CountUtils.getCount(key);
                if(count>0){
                    result.add(new CommonKeyValue(key, String.valueOf(count)));
                }else {
                    if(key.equals("getDistrctTaskQueueCount")){
                        result.add(new CommonKeyValue(key, String.valueOf(getDistrctTaskQueueCount())));
                    }
                }
            }

            return new HttpResponseBuild().setData(result).build();
        } else {
            return new HttpResponseBuild().setData(null).build();
        }


    }

    public Integer getDistrctTaskQueueCount() {
            return ThreadPoolFactoryUtil.getWaitSize();
    }

    @ApiOperation(value = "设置服务器资源采集频率")
    @RequestMapping(value = "/settingCpuMonitorSpeed", method = {RequestMethod.GET})
    public HttpResponse settingCpuMonitorSpeed(@RequestParam Integer second) {
        if (second > 1) {
            systemInfoMonitorManager.resetTimerSpeed(second * 1000);
        }
        return new HttpResponseBuild().setData(true).build();
    }

    @ApiOperation(value = "获取实时进度")
    @RequestMapping(value = "/getProgressInfo", method = {RequestMethod.GET})
    public HttpResponse getProgressInfo(@RequestParam String key, @RequestParam String district) {
        TwoHouseRunTimeProssorMonitorContext<DistrictRunTimeInfo> context = ProssorMonitorContextManager.getContext(key);
        DistrictRunTimeInfo runTimeInfo = null;
        if (context != null) {
            runTimeInfo = context.getCurrent(DistrictRunTimeInfo.class,district);
        }
        return new HttpResponseBuild().setData(runTimeInfo).build();
    }

    @ApiOperation(value = "获取lianjia抓取任务队列排队数量")
    @RequestMapping(value = "/getQueueTaskSize", method = {RequestMethod.GET})
    public HttpResponse getLianJiaQueueTaskSize(@RequestParam  Integer type) {
        Long size=0L;
        if(type==1){
            //链家
//          IQueueTaskManager taskManager=  CustomTaskManager.getSingleton(HtmlLoadDetailIn.class,lianJiaDoHtmlTask);
//            size=taskManager.size();
        }else if(type==2){
            //房天下 挂单信息
            IQueueTaskManager taskManager=  CustomTaskManager.getSingleton(HtmlLoadDetailIn.class,fangHouseInfoTask);
            size=taskManager.size();
        }else if(type==3){
            //房天下走势信息
            IQueueTaskManager taskManager=  CustomTaskManager.getSingleton(GetCommunityRecordIn.class,fangCommuityTask);
            size=taskManager.size();
        }
        return new HttpResponseBuild().setData(size).build();
    }

}
