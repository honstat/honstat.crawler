package com.honstat.house.manager.fang;

import com.alibaba.fastjson.JSON;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import com.honstat.crawler.models.in.CommonKeyValue;

import com.honstat.crawler.models.enums.WriteProssorStepType;
import com.honstat.crawler.models.in.HistoryStepInfoIn;

import com.honstat.crawler.service.utils.NumericMatchUtil;
import com.honstat.crawler.models.in.AddTwoHouseInfoIn;
import com.honstat.crawler.models.in.BaseHouseIn;

import com.honstat.house.service.aop.NeedSaveProssor;
import com.honstat.house.service.aop.NeedWriteProssorDetail;
import com.honstat.crawler.service.ILoadHtmlPage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.manager
 * @Description: TODO
 * @date 2018/12/28 10:38
 */
@Component
public class FangTianXiaSaleInfoLoadHtmlPage implements ILoadHtmlPage {
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    WebClient webClient;
    @Autowired
    TwoHouseInfoServiceManager twoHouseInfoServiceManager;
    static Logger logger = Logger.getLogger(FangTianXiaSaleInfoLoadHtmlPage.class);
    @Autowired
    FangTianXiaDoWorkPageTask doWorkPageTask;
    /**
     * 获取域名
     *
     * @param in
     **/
    @Override
    public String getDomain(BaseHouseIn in) {
        String baseDomain = "";
        if (in.getCityId() == 1) {

            baseDomain = "https://sh.esf.fang.com/";
        } else if (in.getCityId() == 2) {

            baseDomain = "https://suzhou.esf.fang.com/";
        } else if (in.getCityId() == 3) {
            baseDomain = "https://wuhan.esf.fang.com/";
        }
        return baseDomain;
    }

    /**
     * 加载待获取的区县（排除历史记录）
     *
     * @param in
     **/
    @Override
    public List<CommonKeyValue> initWaitLoadDistricts(BaseHouseIn in) {
        return null;
    }
    @NeedWriteProssorDetail(key = "FangTianXiaLoadHtmlPageManager", step = WriteProssorStepType.Complete)
    public void writeComplete(HistoryStepInfoIn in) {
        logger.info("run writeComplete() :" + JSON.toJSONString(in));
    }

    /**
     * 根据url获取页数
     *
     * @param in
     **/
    @Override
    public Integer loadPageSizeByUrl(HistoryStepInfoIn in) {
        int pageSize = 0;
        String requestUrl = in.getLink() + "/i3";
        try {
            int index = 0;
            while (pageSize == 0) {
                HtmlPage firstpage = webClient.getPage(requestUrl);
                List<HtmlElement> pagehtmls = firstpage.getByXPath("//div[@id='list_D10_15']");

                if (pagehtmls != null && pagehtmls.size() > 0) {
                    HtmlElement nodehtml = pagehtmls.get(0);
                    String pagehtml = nodehtml.getLastChild().getPreviousSibling().asText();
                    pageSize = Integer.valueOf(NumericMatchUtil.getNumeric(pagehtml));
                }
                index++;
                if (pageSize==0&&index > 5) {
                    return 0;
                }
            }
        } catch (IOException e) {
            logger.error(e);
        }
        return pageSize;
    }

    @NeedSaveProssor(key = "FangTianXiaSaleInfoLoadHtmlPage")
    @Override
    public Long loadDataByPage(HistoryStepInfoIn in) {
        String url = in.getLink();
        List<AddTwoHouseInfoIn> models = new ArrayList<>();
        try {
            HtmlPage page = webClient.getPage(url);
            List<HtmlElement> spanList = page.getByXPath("//div[@class='shop_list shop_list_4']/dl");
            if (spanList == null || spanList.size() == 0) {
                return 0L;
            }
        } catch (IOException ioe) {
            logger.error("发生io异常:" + ioe);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("发生异常:" + e);
        }


        return Long.valueOf(models.size());
    }

    /**
     * 获取区县下面的乡镇链接
     *
     * @param contextIn
     **/
    @NeedWriteProssorDetail(key = "FangTianXiaLoadHtmlPageManager", step = WriteProssorStepType.Create)
    @Override
    public List<CommonKeyValue> loadPostionDistrictMaps(HistoryStepInfoIn contextIn,HistoryStepInfoIn history) {
        List<CommonKeyValue> arr = new ArrayList<>(20);
        try {
            long start = System.currentTimeMillis();
            HtmlPage firstpage = webClient.getPage(contextIn.getLink());
            List<HtmlElement> pagehtmls = firstpage.getByXPath("//li[@class='area_sq']/ul/li/a");
            if (pagehtmls != null && pagehtmls.size() > 0) {
                String baseDomain = getDomain(contextIn);
                for (HtmlElement ahtml : pagehtmls) {
                    String a = ahtml.getAttribute("href");
                    arr.add(new CommonKeyValue(baseDomain + a, ahtml.asText()));
                }
            }
        } catch (IOException e) {
            logger.error(e);
        }
        if(arr!=null&&history!=null&&history.getTown()!=null){
          List<String>values= arr.stream().map(CommonKeyValue::getValue).collect(Collectors.toList());
            int index=values.indexOf(history.getTown());
            if(index!=-1){
                logger.info("find history town index ,remove it before index item");
               int size= arr.size();
                for (int i = 0; i <size ; i++) {
                    if(i<index){
                        arr.remove(0);
                    }
                }
            }
        }
        return arr;
    }

    @NeedWriteProssorDetail(key = "FangTianXiaLoadHtmlPageManager", step = WriteProssorStepType.Update, sort = 99)
    @Override
    public Long loadTown(HistoryStepInfoIn context, String baseUrl, HistoryStepInfoIn history) {
        Long linkcount=0L;
        logger.info("load town:"+context.getTown());
        int startIndex = 0;
        if (history != null && history.getLink() != null && history.getLink().contains(baseUrl)) {
            if (history.getPageIndex() != null && history.getPageIndex() > 0) {
                startIndex = history.getPageIndex();
            }

        }
        baseUrl+="/i3";
        for (int i = 1; i <= context.getPageSize(); i++) {
            if (i > startIndex) {
                HistoryStepInfoIn infoIn = new HistoryStepInfoIn();
                infoIn.setPageSize(context.getPageSize());
                infoIn.setLink(baseUrl+i);
                infoIn.setPageIndex(i);
                infoIn.setCityId(context.getCityId());
                infoIn.setDistrict(context.getDistrict());
                infoIn.setTown(context.getTown());
                infoIn.setKey(context.getKey());
                linkcount+=doWorkPageTask.doWork(infoIn);
            }

        }
        return linkcount;
    }

}
