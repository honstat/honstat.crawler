package com.honstat.house.controller;

import com.alibaba.fastjson.JSON;
import com.honstat.crawler.models.in.GetTwoHouseAnalysisByCoummunityIn;
import com.honstat.crawler.service.manager.ProssorSaveMonitor;
import com.honstat.house.utils.HttpResponse;
import com.honstat.house.utils.HttpResponseBuild;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.controller
 * @Description: TODO
 * @date 2019/1/23 16:28
 */
@RestController
@RequestMapping("/home")
public class HomeController {
    @Autowired
    ProssorSaveMonitor prossorSaveMonitor;
    @RequestMapping(value = "/stopSystem", method = RequestMethod.GET)
    public HttpResponse stopSystem() {
        System.exit(0);
        return new HttpResponseBuild().setData(true).build();
    }
    @RequestMapping(value = "/clearHistory", method = RequestMethod.GET)
    public HttpResponse clearHistory() {
        prossorSaveMonitor.clear();
        return new HttpResponseBuild().setData(true).build();
    }
    @RequestMapping(value = "/testPost", method = RequestMethod.POST)
    public HttpResponse testPost(@RequestBody GetTwoHouseAnalysisByCoummunityIn infoIn) {

        return new HttpResponseBuild().setData(JSON.toJSONString(infoIn)).build();
    }
    @RequestMapping(value = "/testPost2", method = RequestMethod.POST)
    public HttpResponse testPost(@RequestBody Long cityId  ) {
        return new HttpResponseBuild().setData(cityId).build();
    }
}

