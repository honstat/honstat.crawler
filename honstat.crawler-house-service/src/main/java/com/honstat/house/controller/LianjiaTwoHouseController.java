package com.honstat.house.controller;

import com.alibaba.fastjson.JSON;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.honstat.crawler.models.in.*;
import com.honstat.crawler.models.out.CommonEcharts;
import com.honstat.crawler.service.IQueueTaskManager;
import com.honstat.crawler.service.IQuqueTaskService;
import com.honstat.crawler.service.manager.ProssorSaveMonitor;
import com.honstat.crawler.service.manager.task.CrawlerTaskMonitor;

import com.honstat.crawler.service.manager.task.CustomTaskManager;
import com.honstat.crawler.service.manager.task.TaskRedisLoaderManager;
import com.honstat.house.manager.lianjia.LianJiaDoHtmlTask;
import com.honstat.house.manager.lianjia.LianJiaLoadHtmlPageManager;
import com.honstat.house.manager.lianjia.LianJiaTaskManager;
import com.honstat.house.service.aop.NeedIdempotentFilter;
import com.honstat.house.service.dao.service.TwoHouseTradeInfoDaoService;

import com.honstat.house.utils.HttpResponse;
import com.honstat.house.utils.HttpResponseBuild;
import com.honstat.house.utils.ThreedPoolUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.controller
 * @Description: TODO
 * @date 2018/10/25 16:55
 */
@RestController
@RequestMapping("/lian")
public class LianjiaTwoHouseController {
    Logger logger = Logger.getLogger(LianjiaTwoHouseController.class);
    @Autowired
    private TwoHouseTradeInfoDaoService twoHouseTradeService;

    @Autowired
    private ProssorSaveMonitor prossorSaveMonitor;

    @Autowired
    private LianJiaDoHtmlTask lianJiaDoHtmlTask;

    @Autowired
    private LianJiaLoadHtmlPageManager lianJiaLoadHtmlPageManager;
    @Autowired
    TaskRedisLoaderManager taskRedisLoaderManager;
    @Autowired
    WebClient webClient;
    static ThreedPoolUtil threedPoolUtil = new ThreedPoolUtil(1, 1);

    final String keyformat = "%s_%s_LianJiaLoadHtmlPageManager";
    @RequestMapping(value = "/testPost", method = RequestMethod.POST)
    public HttpResponse testPost(@RequestBody GetTwoHouseAnalysisByCoummunityIn infoIn) {

        return new HttpResponseBuild().setData(JSON.toJSONString(infoIn)).build();
    }
    @NeedIdempotentFilter(key = "lianjiatwoHouse_register", seconds = 60)
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public HttpResponse register(@RequestParam Long cityId) {
        BaseHouseIn basein = new BaseHouseIn();
        basein.setCityId(cityId);
        Runnable r1 = new Runnable() {

            @Override
            public void run() {
                getTask(basein);
            }
        };
        Runnable rollback = new Runnable() {
            @Override
            public void run() {
                logger.info("钩子方法运行了");
                prossorSaveMonitor.saveToRedis();
                logger.info("已保存至redis");
            }
        };
        Thread rollbackThread = new Thread(rollback);
        threedPoolUtil.execute(r1);
        Runtime.getRuntime().addShutdownHook(rollbackThread);
        return new HttpResponseBuild().setData(true).build();
    }

    private String getLink(Long cityId, String district) {
        String baseDomain = "";
        if (cityId == 1) {
            baseDomain = "https://sh.lianjia.com" + district;
        } else if (cityId == 2) {
            baseDomain = "https://su.lianjia.com" + district;
        } else if (cityId == 3) {
            baseDomain = "https://wh.lianjia.com" + district;
        }
        return baseDomain;
    }

    private void initTaskQueue(int Theads, IQuqueTaskService service) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                IQueueTaskManager queueTaskManager=  CustomTaskManager.getSingleton(HtmlLoadDetailIn.class,service);
                CustomTaskManager.setHistoryLoad(taskRedisLoaderManager);
            }
        };
        Thread ra = new Thread(r);
        ra.start();
    }

    private void getTask(BaseHouseIn baseIn) {


        System.out.println("============================================");
        System.out.println("\r\n");
        System.out.println("爬虫主程序开始执行，go！");
        System.out.println("============================================");
        /**init tasks**/
        CrawlerTaskMonitor crawlerTaskMonitor = new CrawlerTaskMonitor(4);
        initTaskQueue(4, lianJiaDoHtmlTask);
        prossorSaveMonitor.registerTimerSave(3);

        List<CommonKeyValue> districtList = getDistrictsByCity(baseIn.getCityId());
        for (CommonKeyValue districtInfo : districtList) {
            Object history = getHistory(String.format(keyformat, districtInfo.getValue(), baseIn.getCityId()));
            HistoryStepInfoIn historyStepInfoIn = null;
            if (history != null) {
                historyStepInfoIn = (HistoryStepInfoIn) history;
                if (historyStepInfoIn!=null&&historyStepInfoIn.getIsComplete()!=null&&historyStepInfoIn.getIsComplete()) {
                    continue;
                }
            }
            HistoryStepInfoIn context = new HistoryStepInfoIn();
            context.setCityId(baseIn.getCityId());
            context.setDistrict(districtInfo.getValue());
            context.setLink(getLink(baseIn.getCityId(), districtInfo.getKey()));
            LianJiaTaskManager manager = new LianJiaTaskManager(lianJiaLoadHtmlPageManager, context, historyStepInfoIn);
            crawlerTaskMonitor.addTask(manager);
            break;
        }
        crawlerTaskMonitor.doTask();
        System.out.println("============================================");
        System.out.println("\r\n");
        System.out.println("爬虫主程序执行完成，退出！");
        System.out.println("============================================");
    }

    private Object getHistory(String key) {
        return prossorSaveMonitor.getValue(key);
    }


    @RequestMapping(value = "/getTradeAnalysis", method = RequestMethod.POST)
    public HttpResponse getTradeAnalysis(@RequestBody QueryTwoHouseTradeByConditionIn in) {
        CommonEcharts res = null;
        if (in.getTimeType() != null && in.getTimeType() > 0) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.MONTH, -in.getTimeType());
            in.setStart(cal.getTime());
            in.setEnd(new Date());
        }


        switch (in.getQueryMethod()) {
            case 1:
                res = twoHouseTradeService.getAnalysisByDistrict(in);
                break;
            case 2:
                res = twoHouseTradeService.getAnalysisByCoummunity(in);
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                res = twoHouseTradeService.getAnalysisByMonth(in);
                break;
        }
        return new HttpResponseBuild().setData(res).build();
    }

    private List<CommonKeyValue>getDistrict(Long cityId, String baseUrl){
        List<CommonKeyValue>res=new ArrayList<>(10);
        try {
            HtmlPage page = webClient.getPage(baseUrl); // 解析获取页面
            List<HtmlElement> spanList = page.getByXPath("/html/body/div[3]/div[1]/dl[2]/dd/div/div/a");
            if(spanList!=null&&spanList.size()>0){
            }

            for (int i = 0; i < spanList.size(); i++) {
                HtmlElement htmlElement=spanList.get(i);
                CommonKeyValue temp=   new CommonKeyValue(htmlElement.getAttribute("href"),htmlElement.asText());
                res.add(temp);
            }
        }catch (Exception e){

        }
        return res;
    }

    private List<CommonKeyValue> getDistrictsByCity(Long cityId) {
        String baseUrl="";
        if (cityId.equals(1L)) {
            baseUrl="https://sh.lianjia.com/chengjiao/";
        } else if (cityId.equals(2L)) {
            baseUrl="https://su.lianjia.com/chengjiao/";
        } else if (cityId.equals(3L)) {
            baseUrl="https://wh.lianjia.com/chengjiao/";
        }
        if(!baseUrl.isEmpty()){
          return   getDistrict(cityId,baseUrl);
        }
        return null;
    }
}
