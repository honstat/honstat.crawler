package com.honstat.crawler.service;



import com.honstat.crawler.models.in.CommonKeyValue;
import com.honstat.crawler.models.in.HistoryStepInfoIn;
import com.honstat.crawler.models.in.BaseHouseIn;

import java.util.List;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.IInterface
 * @Description: TODO
 * @date 2018/12/26 10:27
 */
public interface ILoadHtmlPage {
   /**获取域名**/
   String getDomain(BaseHouseIn in);
    /**加载待获取的区县（排除历史记录）**/
   List<CommonKeyValue> initWaitLoadDistricts(BaseHouseIn in);
   /**根据url获取页数**/
   Integer loadPageSizeByUrl(HistoryStepInfoIn in);
   /**加载每页数据**/
   Long loadDataByPage(HistoryStepInfoIn in);
   /**获取区县下面的乡镇链接**/
  List<CommonKeyValue>loadPostionDistrictMaps(HistoryStepInfoIn contextIn, HistoryStepInfoIn history);
   Long loadTown(HistoryStepInfoIn context, String baseUrl, HistoryStepInfoIn history);

}
