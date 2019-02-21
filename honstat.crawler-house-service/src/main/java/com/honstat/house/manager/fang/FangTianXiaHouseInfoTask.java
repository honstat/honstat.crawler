package com.honstat.house.manager.fang;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.honstat.crawler.service.IQueueTaskManager;
import com.honstat.crawler.service.IQuqueTaskService;
import com.honstat.crawler.models.enums.WriteProssorStepType;
import com.honstat.house.service.aop.NeedResourceMonitor;
import com.honstat.crawler.service.manager.task.CustomTaskManager;
import com.honstat.crawler.service.utils.NumericMatchUtil;
import com.honstat.crawler.models.in.AddTwoHouseInfoIn;
import com.honstat.crawler.models.in.GetCommunityRecordIn;
import com.honstat.crawler.models.in.HtmlLoadDetailIn;
import com.honstat.house.service.aop.NeedWriteProssorDetail;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.manager
 * @Description: TODO
 * @date 2019/1/17 18:01
 */
@Component
public class FangTianXiaHouseInfoTask implements IQuqueTaskService<HtmlLoadDetailIn> {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    WebClient webClient;
    @Autowired
    TwoHouseInfoServiceManager twoHouseInfoServiceManager;
    @Autowired
    FangTianXiaDoHtmlTask tianXiaZouShiDoHtmlTask;
    Logger logger=Logger.getLogger(FangTianXiaHouseInfoTask.class);
   volatile IQueueTaskManager queueTaskManager;
    final static String redisHouseIdkey = "%s_fang_house_Ids";
    final static String redisCommunityKey="%s_fang_community_Ids";
    @NeedResourceMonitor(key = "FangTianXiaLoadHtmlPageManager")
    @NeedWriteProssorDetail(key = "FangTianXiaLoadHtmlPageManager", step = WriteProssorStepType.Update, sort = 100)
    @Override
    public void doHtmlTask(HtmlLoadDetailIn in) {

        List<AddTwoHouseInfoIn>houselist=null;
        if(in==null||in.getLink()==null||in.getLink().length()==0){
            return;
        }
            try {
                HtmlPage page = webClient.getPage(in.getLink()); // 解析获取
                List<HtmlElement> liList = page.getByXPath("//div[@class='shop_list shop_list_4']/dl");
                if (liList == null || liList.size() == 0) {
                    return;
                }
                    houselist=new ArrayList<>(liList.size());
                for (HtmlElement htmlElement :liList) {
                    try {
                        AddTwoHouseInfoIn addTwoHouseInfoIn = new AddTwoHouseInfoIn();
                        addTwoHouseInfoIn.setCityId(in.getCityId());
                        addTwoHouseInfoIn.setDistrict(in.getDistrict());
                        addTwoHouseInfoIn.setGetTime(new Date());
                        //解析logo
                        List<HtmlElement> imghtml = htmlElement.getByXPath("dt/a/img");
                        if (imghtml != null && imghtml.size() > 0) {
                            addTwoHouseInfoIn.setLogo(imghtml.get(0).getAttribute("src2"));
                        }
                        //解析houseid
                        String databg = htmlElement.getAttribute("data-bg");
                        if (databg!=null&& !databg.isEmpty() && databg.length() > 20) {
                            JSONObject jsonObject = JSON.parseObject(databg);
                            String houseid = jsonObject.getString("houseid");
                            addTwoHouseInfoIn.setHouseId(Long.valueOf(houseid));
                        } else {
                         continue;
                        }
                        //取hash值分片，减小key内元素数量
                         Long shareIndex=  addTwoHouseInfoIn.getHouseId()%64;
                        if (!redisTemplate.opsForSet().isMember(String.format(redisHouseIdkey,shareIndex), addTwoHouseInfoIn.getHouseId())) {
                            redisTemplate.opsForSet().add(String.format(redisHouseIdkey,shareIndex), addTwoHouseInfoIn.getHouseId());
                        } else {
                            logger.debug("redis已存在houseid 跳过:" + addTwoHouseInfoIn.getHouseId());
                          continue;
                        }
                        List<HtmlElement> titles = htmlElement.getByXPath("dd/h4/a");

                        if (titles != null && titles.size() > 0) {
                            //houseid,title,href
                            HtmlElement titleHtml = titles.get(0);
                            String title = titleHtml.getAttribute("title");
                            String houseUrl = titleHtml.getAttribute("href");
                            addTwoHouseInfoIn.setSaleTitle(title);
                            addTwoHouseInfoIn.setHouseUrl(houseUrl);
                        }

                        //style,size,foor,direction,year
                        List<HtmlElement> houseinfos = htmlElement.getByXPath("dd/p[@class='tel_shop']");
                        if (houseinfos != null && houseinfos.size() > 0) {
                            HtmlElement houseinfoHtml = houseinfos.get(0);
                            String infohtml = houseinfoHtml.asText();

                            infohtml = infohtml.replace('|', ',');
                            String[] items = infohtml.split(",");
                            if (items.length >= 5) {
                                addTwoHouseInfoIn.setStyle(items[0]);
                                String sizeStr = items[1].split("�O")[0];
                                addTwoHouseInfoIn.setSize(Double.valueOf(sizeStr.trim()));
                                addTwoHouseInfoIn.setFoor(items[2].trim());
                                addTwoHouseInfoIn.setDirection(items[3]);
                                if (NumericMatchUtil.hasNumeric(items[4])) {
                                    addTwoHouseInfoIn.setHouseYear(Integer.valueOf(items[4].split("年建")[0].trim()));
                                }
                                if (NumericMatchUtil.hasNumeric(items[3])) {
                                    addTwoHouseInfoIn.setHouseYear(Integer.valueOf(items[3].split("年建")[0].trim()));
                                    addTwoHouseInfoIn.setDirection("");
                                }
                            }

                        }
                        //coummity
                        List<HtmlElement> addressHtmls = htmlElement.getByXPath("dd/p[@class='add_shop']/a");
                        if (addressHtmls != null && addressHtmls.size() > 0) {
                            HtmlElement addressHtml = addressHtmls.get(0);
                            addTwoHouseInfoIn.setCommunity(addressHtml.asText());
                            //coummityId
                            String addressHref = addressHtml.getAttribute("href");
                            if (addressHref!=null&&!addressHref.isEmpty()) {
                                String numberstr = NumericMatchUtil.getNumeric(addressHref);
                                if (numberstr!=null&&!numberstr.isEmpty()) {
                                    Long coummityId = Long.valueOf(numberstr);
                                    Long shardIndex=coummityId%8;
                                    String redisKey=String.format(redisCommunityKey,shardIndex);
                                    if (!redisTemplate.opsForSet().isMember(redisKey, String.valueOf(coummityId))) {
                                        redisTemplate.opsForSet().add(redisKey, String.valueOf(coummityId));
                                        //address
                                        String address = "";
                                        List<HtmlElement> addressHtmlss = htmlElement.getByXPath("dd/p[@class='add_shop']/span");
                                        if (addressHtmlss != null && addressHtmlss.size() > 0) {
                                            address = addressHtmlss.get(0).asText();
                                        }
                                       //发布小区拉取走势事件
                                        GetCommunityRecordIn item = new GetCommunityRecordIn(addTwoHouseInfoIn.getCityId(),addTwoHouseInfoIn.getDistrict(),coummityId,addTwoHouseInfoIn.getCommunity(),address);
                                        if(queueTaskManager==null){
                                            synchronized (FangTianXiaHouseInfoTask.class){
                                                queueTaskManager= CustomTaskManager.getSingleton(GetCommunityRecordIn.class,tianXiaZouShiDoHtmlTask);
                                            }
                                        }
                                        queueTaskManager.addTask(item);
                                    }
                                }

                            }

                        }


                        //tag
                        List<HtmlElement> tagHtml = htmlElement.getByXPath("dd/p[@class='clearfix label']/span");
                        if (tagHtml != null) {
                            String tagStr = "";
                            for (int j = 0; j < tagHtml.size(); j++) {
                                tagStr += tagHtml.get(j).asText() + ",";
                            }
                            addTwoHouseInfoIn.setTag(tagStr);
                        }
                        //saleprice totalamount

                        List<HtmlElement> priceHtml = htmlElement.getByXPath("dd[@class='price_right']/span");
                        if (priceHtml != null && priceHtml.size() >= 2) {
                            String priceStr = priceHtml.get(0).getFirstChild().asText();
                            addTwoHouseInfoIn.setTotalPrice(new BigDecimal(priceStr + "0000"));
                            String salePriceText = priceHtml.get(1).getFirstChild().asText();
                            if (salePriceText.indexOf("元") > 0) {
                                String[] saleItems = salePriceText.split("元/�O");
                                addTwoHouseInfoIn.setUnit("元/㎡");
                                addTwoHouseInfoIn.setSaleAmount(new BigDecimal(saleItems[0]));
                            }
                            houselist.add(addTwoHouseInfoIn);
                        }


                    } catch (FailingHttpStatusCodeException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }catch (Exception e){

            }

        if(houselist!=null&&houselist.size()>0){
            twoHouseInfoServiceManager.batchAdd(houselist);
        }
    }
}
