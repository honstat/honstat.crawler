package com.honstat.house.manager.lianjia;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import com.gargoylesoftware.htmlunit.javascript.host.intl.DateTimeFormat;
import com.honstat.crawler.models.in.AddTwoHouseTradeInfoIn;
import com.honstat.crawler.models.in.HtmlLoadDetailIn;
import com.honstat.crawler.service.IQuqueTaskService;
import com.honstat.crawler.service.utils.NumericMatchUtil;
import com.honstat.house.service.aop.NeedResourceMonitor;
import com.honstat.house.service.dao.service.TwoHouseTradeInfoDaoService;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.assertj.core.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.manager
 * @Description: TODO
 * @date 2019/1/11 11:35
 */
@Component
public class LianJiaDoHtmlTask implements IQuqueTaskService<HtmlLoadDetailIn> {
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    WebClient webClient;
    @Autowired
    TwoHouseTradeInfoDaoService tradeService;
    static final String redisHouseIdKey = "%s_%s_lianjia_house_trade_house_Ids";
    Logger logger=Logger.getLogger(LianJiaDoHtmlTask.class);
    @NeedResourceMonitor(key = "LianJiaDoHtmlTask")
    @Override
    public void doHtmlTask(HtmlLoadDetailIn in) {
        logger.debug(Thread.currentThread().getName() + "get " + in.getDistrict() + "request link size:" + in.getHrefs().size());
        try {
            Long cityId = in.getCityId();
            String district = in.getDistrict();

            List<String> links = in.getHrefs();
            List<AddTwoHouseTradeInfoIn> dblist = new ArrayList<>(links.size());

            for (String link : links) {
                // region   a
                try {
                    HtmlPage page = webClient.getPage(link); // 解析获取页面
                    List<HtmlElement> realDatespans = page.getByXPath("//div[@class='house-title LOGVIEWDATA LOGVIEW']//span");
                    if (realDatespans == null || realDatespans.size() == 0) {
                        continue;
                    }
                    AddTwoHouseTradeInfoIn addinfo = new AddTwoHouseTradeInfoIn();
                    addinfo.setDistrict(district);
                    addinfo.setCityId(cityId);
                    addinfo.setCreateTime(new Date());
                    String tranDate = "";
                    String houseStr = NumericMatchUtil.getNumeric(link);
                    if(houseStr==null||houseStr.isEmpty()){
                        continue;
                    }
                    if (!redisTemplate.opsForSet().isMember(String.format(redisHouseIdKey,cityId,district), houseStr)) {
                        redisTemplate.opsForSet().add(String.format(redisHouseIdKey,cityId,district), houseStr);
                    } else {
                        logger.debug("redis exist houseid:" + houseStr);
                        continue;
                    }

                    addinfo.setHouseId(Long.valueOf(houseStr));
                    tranDate = realDatespans.get(0).asText().split(" ")[0];
                    SimpleDateFormat sdf = new SimpleDateFormat("yyy.MM.dd");
                    addinfo.setTradeDate(sdf.parse(tranDate));
                    List<HtmlElement> titlespans = page.getByXPath("//div[@class='house-title LOGVIEWDATA LOGVIEW']/div");
                    String titleStr = titlespans.get(0).asText();
                    String[] titles = titleStr.split(" ");
                    if (titles != null && titles.length >= 2) {
                        addinfo.setCoummunity(titles[0]);
                        addinfo.setStyle(titles[1]);
                        String sizeStr=titles[2].split("平米")[0];
                        Double size =0.0;
                        if(NumericMatchUtil.hasNumeric(sizeStr)&&NumericMatchUtil.getDoubleNumeric(sizeStr)!=null){
                            size= Double.valueOf(NumericMatchUtil.getDoubleNumeric(sizeStr));
                        }
                        addinfo.setSize(size);
                    }
                    List<HtmlElement> titlediv = page.getByXPath("//div[@class='house-title LOGVIEWDATA LOGVIEW']");
                    String coummunitystr = titlediv.get(0).getAttribute("data-lj_action_housedel_id");
                    if(coummunitystr!=null&&!coummunitystr.isEmpty()){
                        addinfo.setCoummunityId(Long.valueOf(coummunitystr));
                    }

                    List<HtmlElement> pricespanhtmls = page.getByXPath("//div[@class='overview']//div[@class='price']");
                    String priceStr = pricespanhtmls.get(0).asText();
                    String[] priceitems = priceStr.split("万");

                    if (NumericMatchUtil.hasNumeric(priceitems[0])) {
                        Double totalPrice = Double.valueOf(priceitems[0] + "0000");
                        addinfo.setRealAmount(new BigDecimal(totalPrice));
                    }
                    if (NumericMatchUtil.hasNumeric(priceitems[1])) {
                        Double avgPrice = Double.valueOf(NumericMatchUtil.getDoubleNumeric(priceitems[1]));
                        addinfo.setAvgAmount(new BigDecimal(avgPrice));
                    }
                    List<HtmlElement> salepricespanhtmls = page.getByXPath("//div[@class='overview']//div[@class='msg']/span");
                    if (salepricespanhtmls != null && salepricespanhtmls.size() > 0) {
                        String salehtml = salepricespanhtmls.get(0).asText();
                        if (NumericMatchUtil.hasNumeric(salehtml)) {
                            Double avgPrice = Double.valueOf(NumericMatchUtil.getDoubleNumeric(salehtml )+ "0000");
                            addinfo.setSalePrice(new BigDecimal(avgPrice));
                        }
                        String dayhtml = salepricespanhtmls.get(1).asText();
                        if (NumericMatchUtil.hasNumeric(salehtml)) {
                            int days = Integer.valueOf(NumericMatchUtil.getDoubleNumeric(dayhtml));
                            addinfo.setSaleDays(days);
                        }
                    }
                    dblist.add(addinfo);


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    logger.error(e);
                    e.printStackTrace();
                } catch (Exception ex) {
                    logger.error(ex.getMessage());
                }
//endregion

            }

            if (dblist.size() > 0) {
                Boolean result = tradeService.BatchInsertData(dblist);
                logger.info("insert：" + dblist.size() + " result：" + result);
            }

        } catch (FailingHttpStatusCodeException e) {
            e.printStackTrace();
        } catch (Exception e) {
           logger.error(e);
        }
    }



}
