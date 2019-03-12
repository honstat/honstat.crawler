package com.honstat.house.controller;

import com.alibaba.fastjson.JSON;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.honstat.crawler.models.in.*;
import com.honstat.crawler.service.manager.ProssorSaveMonitor;
import com.honstat.crawler.service.manager.task.CustomTaskManager;
import com.honstat.crawler.service.manager.task.TaskRedisLoaderManager;
import com.honstat.crawler.service.utils.CountUtils;
import com.honstat.crawler.service.utils.ThreadPoolFactoryUtil;
import com.honstat.house.manager.fang.FangTianXiaDoHtmlTask;
import com.honstat.house.manager.fang.FangTianXiaHouseInfoTask;
import com.honstat.house.manager.fang.FangTianXiaSaleInfoLoadHtmlPage;
import com.honstat.house.manager.fang.FangTianXiaTaskManager;
import com.honstat.house.service.aop.NeedIdempotentFilter;
import com.honstat.house.service.dao.model.TwoHouseInfo;
import com.honstat.house.service.dao.service.TwoHouseAnalysisInfoDaoService;
import com.honstat.house.service.dao.service.TwoHouseInfoDaoService;
import com.honstat.house.service.dao.service.TwoHouseRealAnalysisDaoService;
import com.honstat.house.utils.HttpResponse;
import com.honstat.house.utils.HttpResponseBuild;
import com.honstat.house.utils.ThreedPoolUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.controller
 * @Description: TODO
 * @date 2018/10/16 15:11
 */
@RequestMapping("/fang")
@RestController
public class FangTianXiaController extends BaseController {
    @Autowired
    TwoHouseInfoDaoService twoHouseService;
    @Autowired
    TwoHouseAnalysisInfoDaoService twoHouseAnalysisService;
    @Autowired
    TwoHouseRealAnalysisDaoService realAnalysisService;

    @Autowired
    ProssorSaveMonitor prossorSaveMonitor;
    @Autowired
    FangTianXiaSaleInfoLoadHtmlPage saleInfoLoadHtmlPage;
    @Autowired
    WebClient webClient;
    @Autowired
    TaskRedisLoaderManager taskRedisLoaderManager;
    @Autowired
    FangTianXiaDoHtmlTask doTradeAnalysisTask;
    @Autowired
    FangTianXiaHouseInfoTask houseInfoTask;
    Logger logger = Logger.getLogger(FangTianXiaController.class);
    static ThreedPoolUtil threedPoolUtil = new ThreedPoolUtil(1, 1);
    final String keyformat = "%s_%s_FangTianXiaLoadHtmlPageManager";

    @NeedIdempotentFilter(key = "fangtianxia_register", seconds = 10)
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public HttpResponse register(@RequestParam final Long cityId) {
        Integer result = 1;
        String registerKey = cityId + "_FangTianXiaLoadHtmlPageManager";
        Integer isExists = CountUtils.getCount(registerKey);
        if (isExists != null && isExists > 0) {
            //有任务在进行中，不能重复注册
            result = 3;
            return new HttpResponseBuild().setData(result).build();
        } else {
            CountUtils.addCount(registerKey);
            Map history_map = prossorSaveMonitor.getByContainsKey(registerKey);
            //有历史记录，将从历史记录开始加载
            if (history_map != null && history_map.size() > 0) {
                result = 2;
            }
        }
        runTask(cityId);
        settingShutdownHook();
        return new HttpResponseBuild().setData(result).build();
    }

    private void settingShutdownHook() {
        Thread rollbackThread = new Thread(new Runnable() {
            @Override
            public void run() {
                logger.info("钩子方法运行了,临时性抢救一下数据");
                prossorSaveMonitor.saveToRedis();
                logger.info("进度已保存至redis");
                CustomTaskManager.saveQueue();
            }
        });
        Runtime.getRuntime().addShutdownHook(rollbackThread);
    }

    private void runTask(Long cityId) {
        Runnable r1 = new Runnable() {

            @Override
            public void run() {
                if (cityId > 0) {
                    if (cityId == 1) {
                        doTaskNew(1L, "https://sh.esf.fang.com/");

                    } else if (cityId == 2) {
                        doTaskNew(2L, "https://suzhou.esf.fang.com/");
                    } else if (cityId == 3) {
                        doTaskNew(3L, "https://wuhan.esf.fang.com/");
                    }

                }
            }
        };
        threedPoolUtil.execute(r1);
    }

    private void doTaskNew(Long cityId, String baseUrl) {
        System.out.println("============================================");
        System.out.println("\r\n");
        System.out.println("爬虫主程序开始执行，go！");
        System.out.println("============================================");
        /**init tasks**/
        initTaskQueue();

        List<CommonKeyValue> districtList = getDistrict(cityId, baseUrl);
        for (CommonKeyValue districtInfo : districtList) {
            String key = String.format(keyformat, districtInfo.getValue(), cityId);
            Object history = getHistory(key);
            HistoryStepInfoIn historyStepInfoIn = null;
            if (history != null) {
                historyStepInfoIn = (HistoryStepInfoIn) history;
            }
            HistoryStepInfoIn context = new HistoryStepInfoIn();
            context.setCityId(cityId);
            context.setDistrict(districtInfo.getValue());
            context.setLink(getLink(cityId, districtInfo.getKey()));
            context.setKey(key);
            FangTianXiaTaskManager manager = new FangTianXiaTaskManager(saleInfoLoadHtmlPage, context, historyStepInfoIn);
            ThreadPoolFactoryUtil.execute(manager);
        }
        System.out.println("============================================");
        System.out.println("\r\n");
        System.out.println("爬虫主程序执行完成，退出！");
        System.out.println("============================================");
    }

    private void initTaskQueue() {
        if (!isInited) {
            try {
                CustomTaskManager.setHistoryLoad(taskRedisLoaderManager);
                CustomTaskManager.getSingleton(GetCommunityRecordIn.class, doTradeAnalysisTask);
                CustomTaskManager.getSingleton(HtmlLoadDetailIn.class, houseInfoTask);
                prossorSaveMonitor.registerTimerSave(3);
                isInited = true;
            } catch (Exception e) {
                isInited = false;
                logger.error("initTaskQueue error:" + e.getMessage());
            }
        }
    }

    private Object getHistory(String key) {
        return prossorSaveMonitor.getValue(key);
    }

    private String getLink(Long cityId, String district) {
        String baseDomain = "";
        if (cityId == 1) {
            baseDomain = "https://sh.esf.fang.com/" + district;
        } else if (cityId == 2) {
            baseDomain = "https://suzhou.esf.fang.com" + district;
        } else if (cityId == 3) {
            baseDomain = "https://wuhan.esf.fang.com/" + district;
        }
        return baseDomain;
    }

    private List<CommonKeyValue> getDistrict(Long cityId, String baseUrl) {
        List<CommonKeyValue> res = new ArrayList<>(10);
        try {
            HtmlPage page = webClient.getPage(baseUrl); // 解析获取页面
            List<HtmlElement> spanList = page.getByXPath("//li[@class='clearfix screen_list'][1]/ul/li[@class='']/a");
            if (spanList != null && spanList.size() > 0) {

                for (int i = 0; i < spanList.size(); i++) {
                    HtmlElement htmlElement = spanList.get(i);
                    CommonKeyValue temp = new CommonKeyValue(htmlElement.getAttribute("href"), htmlElement.asText());
                    res.add(temp);
                }
            }

        } catch (Exception e) {
            logger.error("get distict error:" + e);
        }
        return res;
    }

    @RequestMapping(value = "/sayHi", method = {RequestMethod.GET})
    public HttpResponse sayHi() {
        List<TwoHouseInfo> s = twoHouseService.selectByExample(null);

        return new HttpResponseBuild().setData("hello~").build();
    }

    @RequestMapping(value = "/analysisByCoummunity", method = {RequestMethod.POST})
    public HttpResponse analysisByCoummunity(@RequestBody GetTwoHouseAnalysisByCoummunityIn in) {
        return new HttpResponseBuild().setData(twoHouseAnalysisService.queryHouseAnalysis(in)).build();
    }

    @RequestMapping(value = "/analysisTimeByCoummunity", method = {RequestMethod.POST})
    public HttpResponse analysisTimeByCoummunity(@RequestBody GetTwoHouseAnalysisByCoummunityIn in) {
        try {
            return new HttpResponseBuild().setData(twoHouseAnalysisService.getCoummunityEchart(in)).build();
        } catch (Exception e) {

        }
        return new HttpResponseBuild().setData(false).build();
    }

    @RequestMapping(value = "/analysisByDistrict", method = RequestMethod.POST)
    public HttpResponse analysisByDistrict(@RequestBody GetTwoHouseAnalysisByDistrictIn in) {
        return new HttpResponseBuild().setData(twoHouseAnalysisService.doAnalysisByBasicForType(in)).build();
    }

    @RequestMapping(value = "/testPost", method = RequestMethod.POST)
    public HttpResponse testPost(@RequestBody GetTwoHouseAnalysisByCoummunityIn infoIn) {

        return new HttpResponseBuild().setData(JSON.toJSONString(infoIn)).build();
    }

    @RequestMapping(value = "/testgetRealAnalysis", method = RequestMethod.POST)
    public HttpResponse testgetRealAnalysis(@RequestBody QueryTwoHouseRealAnalysisByConditionIn infoIn) {

        return new HttpResponseBuild().setData(JSON.toJSONString(infoIn)).build();
    }

    @RequestMapping(value = "/getRealAnalysis", method = RequestMethod.POST)
    public HttpResponse getRealAnalysis(@RequestBody QueryTwoHouseRealAnalysisByConditionIn in) {
        try {
            return new HttpResponseBuild().setData(realAnalysisService.getRealAnalysis(in)).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HttpResponseBuild().setData(null).build();
    }
}
