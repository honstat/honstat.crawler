package com.honstat.house.manager.fang;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.honstat.crawler.service.IQueueTaskManager;
import com.honstat.crawler.models.in.HistoryStepInfoIn;
import com.honstat.crawler.models.in.HtmlLoadDetailIn;
import com.honstat.crawler.service.manager.task.CustomTaskManager;

import com.honstat.house.service.aop.NeedSaveProssor;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.manager
 * @Description: TODO
 * @date 2019/1/14 11:14
 */
@Component
public class FangTianXiaDoWorkPageTask {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    WebClient webClient;
    @Autowired
    TwoHouseInfoServiceManager twoHouseInfoServiceManager;
    @Autowired
    FangTianXiaHouseInfoTask houseInfoTask;
    IQueueTaskManager queueTaskManager;
   private static Pattern pattern=Pattern.compile("[^\\d]+(\\d+)[^\\d]+");
    final static String redisHouseIdkey = "%s_%s_fang_house_trade_house_Ids";
    Logger logger = Logger.getLogger(FangTianXiaDoWorkPageTask.class);

    @NeedSaveProssor(key = "FangTianXiaLoadHtmlPageManager")
    public Long doWork(HistoryStepInfoIn in) {
        if(queueTaskManager==null){
            queueTaskManager= CustomTaskManager.getSingleton(HtmlLoadDetailIn.class,houseInfoTask);
        }
        HtmlLoadDetailIn detailIn=new HtmlLoadDetailIn(in.getCityId(),in.getDistrict(), null,in.getLink());
        queueTaskManager.addTask(detailIn);
        try {
            HtmlPage page = webClient.getPage(in.getLink()); // 解析获取页面
            List<HtmlElement> liList = page.getByXPath("//div[@class='shop_list shop_list_4']/dl");
            if (liList == null || liList.size() == 0) {
                return 0L;
            }
           Long count= liList.stream().filter(x->x.getAttribute("data-bg")!=null).count();
            return count;
        } catch (IOException E) {
            E.printStackTrace();
        }
        return 0L;
    }


}
