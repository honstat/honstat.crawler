package com.honstat.house.manager.lianjia;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import com.honstat.crawler.models.in.HistoryStepInfoIn;
import com.honstat.crawler.models.in.HtmlLoadDetailIn;
import com.honstat.crawler.service.IQueueTaskManager;
import com.honstat.crawler.service.manager.task.CustomTaskManager;
import com.honstat.crawler.service.utils.NumericMatchUtil;
import com.honstat.house.service.aop.NeedSaveProssor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.manager
 * @Description: TODO
 * @date 2019/1/14 11:14
 */
@Component
public class LianJiaDoWorkPageTask {
    @Autowired
   private RedisTemplate redisTemplate;
    @Autowired
    WebClient webClient;
    @Autowired
    LianJiaDoHtmlTask doHtmlTask;
    final static String redisHouseIdkey = "%s_%s_lianjia_house_trade_house_Ids";
    IQueueTaskManager queueTaskManager;
    @NeedSaveProssor(key = "LianJiaLoadHtmlPageManager")
    public Long doWork(HistoryStepInfoIn in){
        if(queueTaskManager==null){
            queueTaskManager= CustomTaskManager.getSingleton(HtmlLoadDetailIn.class,doHtmlTask);
        }
        List<String> hrefs = new ArrayList<>();
        try {
            HtmlPage page = webClient.getPage(in.getLink()); // 解析获取页面
            List<HtmlElement> liList = page.getByXPath("//ul[@class='listContent']/li");
            if (liList == null || liList.size() == 0) {
                return 0L;
            }
            for (int j = 0; j < liList.size(); j++) {
                HtmlElement html = liList.get(j);
                List<HtmlElement> atags = html.getByXPath("a");
                String href = "";
                if (atags != null && atags.size() > 0) {
                    href = atags.get(0).getAttribute("href");
                    if (!href.isEmpty()) {
                        String houseStr = NumericMatchUtil.getNumeric(href);

                        if (!redisTemplate.opsForSet().isMember(String.format(redisHouseIdkey, in.getCityId(), in.getDistrict()), houseStr)) {
                            hrefs.add(href);
                        } else {
                            continue;
                        }
                    }
                }

            }
            if (hrefs.size() > 0) {
                HtmlLoadDetailIn param = new HtmlLoadDetailIn(in.getCityId(), in.getDistrict(), hrefs,null);
                queueTaskManager.addTask(param);
            } else {
                return 0L;
            }
        } catch (IOException E) {
            E.printStackTrace();
        }

        return Long.valueOf(hrefs.size());
    }
}
