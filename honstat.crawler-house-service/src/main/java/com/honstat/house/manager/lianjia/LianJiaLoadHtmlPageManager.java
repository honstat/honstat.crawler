package com.honstat.house.manager.lianjia;

import com.alibaba.fastjson.JSON;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import com.honstat.crawler.models.enums.WriteProssorStepType;
import com.honstat.crawler.models.in.BaseHouseIn;
import com.honstat.crawler.models.in.CommonKeyValue;
import com.honstat.crawler.models.in.HistoryStepInfoIn;
import com.honstat.crawler.models.in.HtmlLoadDetailIn;
import com.honstat.crawler.service.ILoadHtmlPage;
import com.honstat.crawler.service.manager.task.CustomTaskManager;
import com.honstat.crawler.service.manager.task.QueueTaskManager;
import com.honstat.crawler.service.models.ProssorMonitorContextManager;
import com.honstat.crawler.service.models.TwoHouseRunTimeProssorMonitorContext;
import com.honstat.crawler.service.utils.NumericMatchUtil;
import com.honstat.house.service.aop.NeedSaveProssor;
import com.honstat.house.service.aop.NeedWriteProssorDetail;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.manager
 * @Description: 链家爬虫处理类
 * @date 2018/12/26 10:29
 */
@Service
public class LianJiaLoadHtmlPageManager implements ILoadHtmlPage {

    private TwoHouseRunTimeProssorMonitorContext context;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private WebClient webClient;
    @Autowired
    private LianJiaDoWorkPageTask doWorkPageTask;
    @Autowired
    LianJiaDoHtmlTask doHtmlTask;
    Logger logger = Logger.getLogger(LianJiaLoadHtmlPageManager.class);
    String keyformat = "%s_LianJiaLoadHtmlPageManager";
    String redisHouseIdkey = "%s_%s_lianjia_house_trade_house_Ids";

    @NeedWriteProssorDetail(key = "LianJiaLoadHtmlPageManager", step = WriteProssorStepType.Complete)
    public void writeComplete(HistoryStepInfoIn in) {
        logger.info("run writeComplete() :" + JSON.toJSONString(in));
    }

    public void init(HistoryStepInfoIn in) {
        context = ProssorMonitorContextManager.getContext(String.format(keyformat, in.getCityId()));
    }

    @NeedSaveProssor(key = "LianJiaLoadHtmlPageManager")
    public Long loadDataByPage(HistoryStepInfoIn in) {
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
                CustomTaskManager.getSingleton(QueueTaskManager.class, doHtmlTask).addTask(param);
            } else {
                return 0L;
            }
        } catch (IOException E) {
            E.printStackTrace();
        }

        return Long.valueOf(hrefs.size());
    }


    /**
     * 获取区县下面的乡镇链接
     *
     * @param
     * @param
     **/
    @NeedWriteProssorDetail(key = "LianJiaLoadHtmlPageManager", step = WriteProssorStepType.Create)
    @Override
    public List<CommonKeyValue> loadPostionDistrictMaps(HistoryStepInfoIn contextIn, HistoryStepInfoIn history) {
        List<CommonKeyValue> arr = new ArrayList<>(20);
        try {
            long start = System.currentTimeMillis();
            HtmlPage firstpage = webClient.getPage(contextIn.getLink());
            List<HtmlElement> pagehtmls = firstpage.getByXPath("/html/body/div[3]/div[1]/dl[2]/dd/div/div[2]/a");
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
        if (arr != null && history != null && history.getTown() != null) {
            List<String>values= arr.stream().map(CommonKeyValue::getValue).collect(Collectors.toList());
            int index=values.indexOf(history.getTown());
            if (index != -1) {
                logger.info("find history town index ,remove it before index item");
                int size = arr.size();
                for (int i = 0; i < size; i++) {
                    if (i < index) {
                        arr.remove(0);
                    }
                }
            }
        }
        return arr;
    }

    @NeedWriteProssorDetail(key = "LianJiaLoadHtmlPageManager", step = WriteProssorStepType.Update, sort = 99)
    @Override
    public Long loadTown(HistoryStepInfoIn context, String baseUrl, HistoryStepInfoIn history) {
        Long linkcount = 0L;
        logger.info("load town:" + context.getTown());
        int startIndex = 0;
        if (history != null && history.getLink() != null && history.getLink().contains(baseUrl)) {
            if (history.getPageIndex() != null && history.getPageIndex() > 0) {
                startIndex = history.getPageIndex();
            }

        }

        for (int i = 1; i <= context.getPageSize(); i++) {
            if (i > startIndex) {
                try {
                    String reqUrl = baseUrl + "/pg" + i;
                    HistoryStepInfoIn infoIn = new HistoryStepInfoIn();
                    infoIn.setPageSize(context.getPageSize());
                    infoIn.setLink(reqUrl);
                    infoIn.setPageIndex(i);
                    infoIn.setCityId(context.getCityId());
                    infoIn.setDistrict(context.getDistrict());
                    infoIn.setTown(context.getTown());
                    linkcount += doWorkPageTask.doWork(infoIn);
                } catch (Exception e) {
                    logger.error(e);
                }
            }

        }
        return linkcount;
    }

    /**
     * 获取域名
     *
     * @param in
     **/
    @Override
    public String getDomain(BaseHouseIn in) {
        String baseDomain = "";
        if (in.getCityId() == 1) {

            baseDomain = "https://sh.lianjia.com/";
        } else if (in.getCityId() == 2) {

            baseDomain = "https://su.lianjia.com/";
        } else if (in.getCityId() == 3) {
            baseDomain = "https://wh.lianjia.com/";
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

    @Override
    public Integer loadPageSizeByUrl(HistoryStepInfoIn in) {
        int pageSize = 0;
        String requestUrl = in.getLink() + "/pg1";
        try {
            int index = 0;
            while (pageSize == 0) {
                HtmlPage firstpage = webClient.getPage(requestUrl);
                List<HtmlElement> pagehtmls = firstpage.getByXPath("//div[@class='page-box house-lst-page-box']");

                if (pagehtmls != null && pagehtmls.size() > 0) {
                    String dataJson = pagehtmls.get(0).getAttribute("page-data");
                    Map jsonmaps = (Map) JSON.parse(dataJson);
                    if (jsonmaps != null && jsonmaps.containsKey("totalPage")) {
                        pageSize = (Integer) jsonmaps.get("totalPage");
                    }
                }
                index++;
                if (index > 5) {

                    return 0;
                }
            }
        } catch (IOException e) {
            logger.error(e);
        }
        return pageSize;
    }


}
