package com.honstat.house.manager.fang;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.honstat.crawler.service.IQuqueTaskService;
import com.honstat.crawler.models.in.TimeAndValue;
import com.honstat.house.service.aop.NeedResourceMonitor;

import com.honstat.crawler.models.in.AddTwoHouseAnalysisInfoIn;
import com.honstat.crawler.models.in.GetCommunityRecordIn;
import com.honstat.house.service.dao.service.TwoHouseAnalysisInfoDaoService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.manager
 * @Description: 小区走势拉取
 * @date 2019/1/11 11:35
 */
@Component
public class FangTianXiaDoHtmlTask implements IQuqueTaskService<GetCommunityRecordIn> {
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    WebClient webClient;

    @Autowired
    TwoHouseAnalysisInfoDaoService twoHouseAnalysisService;
    Logger logger=Logger.getLogger(FangTianXiaDoHtmlTask.class);

    @NeedResourceMonitor(key = "FangTianXiaLoadHtmlPageManager")
    @Override
    public void doHtmlTask(GetCommunityRecordIn in) {
        logger.debug(Thread.currentThread().getName() + "get " + in.getDistrict() + "request link size:" + in.getCoummunity());

        String baseUrl = "http://pinggus.fang.com/RunChartNew/MakeChartData?newcode=codestr&city=citystr&district=districtstr&commerce=&titleshow=&year=";
        String city = "";
        switch (in.getCityId().intValue()) {
            case 1:
                city = "上海";
                break;
            case 2:
                city = "苏州";
                break;
            case 3:
                city = "武汉";
                break;
        }
        String requestUrl = baseUrl.replaceAll("codestr", in.getCoummunityId().toString()).replaceAll("citystr", city).replaceAll("districtstr", in.getDistrict());
        logger.info("当前访问url:" + requestUrl);
        try {
            HtmlPage page = webClient.getPage(requestUrl);

            String response = page.asText();
            String[] items = response.split("&");
            if (items != null && items.length > 1) {
                AddTwoHouseAnalysisInfoIn info = new AddTwoHouseAnalysisInfoIn();
                info.setAddress(in.getAddress());
                info.setCityId(in.getCityId());
                info.setCoummunity(in.getCoummunity());
                info.setCoummunityId(in.getCoummunityId());
                info.setDistrict(in.getDistrict());

                List<TimeAndValue> list = new ArrayList<>();
                String ss = items[0];
                if (ss.isEmpty() || ss.length() == 0) {
                    return;
                }
                JSONArray jsonArray = JSON.parseArray(ss);
                for (Object o : jsonArray) {
                    JSONArray item = (JSONArray) o;
                    Date date = new Date((Long) item.get(0));
                    TimeAndValue timeAndValue = new TimeAndValue();
                    timeAndValue.setTime(date);
                    timeAndValue.setValue(new BigDecimal(item.get(1).toString()));
                    list.add(timeAndValue);
                }
                info.setList(list);
                boolean isok = twoHouseAnalysisService.insert(info);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
