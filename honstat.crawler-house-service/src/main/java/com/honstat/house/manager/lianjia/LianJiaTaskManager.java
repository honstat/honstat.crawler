package com.honstat.house.manager.lianjia;

import com.alibaba.fastjson.JSON;

import com.honstat.crawler.models.in.CommonKeyValue;
import com.honstat.crawler.models.in.HistoryStepInfoIn;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.manager
 * @Description: lianjia区县任务流程管理器
 * @date 2019/1/9 18:38
 */
public class LianJiaTaskManager implements Runnable {
    Logger logger = Logger.getLogger(LianJiaTaskManager.class);

    public LianJiaTaskManager(LianJiaLoadHtmlPageManager _manager, HistoryStepInfoIn in, HistoryStepInfoIn _history) {
        lianJiaLoadHtmlPageManager = _manager;
        contextIn = in;
        historyStepInfoIn = _history;
    }

    private LianJiaLoadHtmlPageManager lianJiaLoadHtmlPageManager;
    HistoryStepInfoIn contextIn;
    HistoryStepInfoIn historyStepInfoIn;

    public void doTask(HistoryStepInfoIn contextIn, HistoryStepInfoIn history) {
        logger.info("run doTask inputparam: contextIn:" + JSON.toJSONString(contextIn) + "history" + JSON.toJSONString(history));
        lianJiaLoadHtmlPageManager.init(contextIn);
        List<CommonKeyValue> subDistrictLinks = lianJiaLoadHtmlPageManager.loadPostionDistrictMaps(contextIn, history);

        boolean isneedload = false;
        boolean isfind = false;
        if (historyStepInfoIn != null) {
            if (historyStepInfoIn.getTown() != null) {
                isneedload = true;
            }
        }
        for (CommonKeyValue tmr : subDistrictLinks) {
            if (isneedload) {
                if (!isfind) {
                    if (historyStepInfoIn.getTown().contains(tmr.getValue())) {
                        isfind = true;
                    }
                    continue;
                }
            }
            contextIn.setLink(tmr.getKey());
            contextIn.setTown(tmr.getValue());
            int pageSize = lianJiaLoadHtmlPageManager.loadPageSizeByUrl(contextIn);
            contextIn.setPageSize(pageSize);
            lianJiaLoadHtmlPageManager.loadTown(contextIn, tmr.getKey(), history);

        }
        lianJiaLoadHtmlPageManager.writeComplete(contextIn);
    }


    @Override
    public void run() {
        doTask(contextIn, historyStepInfoIn);
    }
}
