package com.honstat.house.manager.fang;

import com.alibaba.fastjson.JSON;

import com.honstat.crawler.models.in.CommonKeyValue;
import com.honstat.crawler.models.in.HistoryStepInfoIn;
import com.honstat.crawler.service.utils.ThreadPoolFactoryUtil;
import org.apache.log4j.Logger;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.manager
 * @Description: TODO
 * @date 2019/1/15 19:24
 */
public class FangTianXiaTaskManager implements Runnable {

    Logger logger=Logger.getLogger(FangTianXiaTaskManager.class);
    public  FangTianXiaTaskManager (FangTianXiaSaleInfoLoadHtmlPage _manager, HistoryStepInfoIn in, HistoryStepInfoIn _history){
        LoadHtmlPageManager=_manager;
        contextIn=in;
        historyStepInfoIn=_history;
    }
    private FangTianXiaSaleInfoLoadHtmlPage LoadHtmlPageManager;
    HistoryStepInfoIn contextIn;
    HistoryStepInfoIn historyStepInfoIn;
    public void doTask(HistoryStepInfoIn contextIn,HistoryStepInfoIn history){
        logger.info("run doTask inputparam: contextIn:"+ JSON.toJSONString(contextIn)+"history: "+ JSON.toJSONString(history));
        try {
            List<CommonKeyValue> subDistrictLinks = LoadHtmlPageManager.loadPostionDistrictMaps(contextIn,historyStepInfoIn);
            if(subDistrictLinks.size()==0){
                logger.error("被反爬虫手段屏蔽了！参数："+JSON.toJSONString(contextIn));
                /**此处如果频繁错误，将会导致死循环**/
                ThreadPoolFactoryUtil.executeDelaye(this,1L, TimeUnit.MINUTES);
                return;
            }
            boolean isneedload=false;
            boolean isfind=false;
            if(historyStepInfoIn!=null){
                if(historyStepInfoIn.getTown()!=null){
                    isneedload=true;
                }
            }
            for (CommonKeyValue tmr : subDistrictLinks) {
                try {
                    if(isneedload){
                        if(!isfind){
                            if(historyStepInfoIn.getTown().contains(tmr.getValue())){
                                isfind=true;
                            }
                            continue;
                        }
                    }
                    contextIn.setLink(tmr.getKey());
                    contextIn.setTown(tmr.getValue());
                    int pageSize = LoadHtmlPageManager.loadPageSizeByUrl(contextIn);
                    contextIn.setPageSize(pageSize);
                    LoadHtmlPageManager.loadTown(contextIn,tmr.getKey(),history);
                }catch (Exception e){
                    logger.error("exec town error:"+e);
                }

            }
            LoadHtmlPageManager.writeComplete(contextIn);
        }catch (Exception e){
         logger.error(e);
        }

    }

    @Override
    public void run() {
        doTask(contextIn,historyStepInfoIn);
    }
}
