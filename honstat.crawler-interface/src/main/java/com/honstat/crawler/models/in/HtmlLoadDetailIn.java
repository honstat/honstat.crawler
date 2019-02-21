package com.honstat.crawler.models.in;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.interfaces.model.in
 * @Description: TODO
 * @date 2019/1/11 11:32
 */
@Data
public class HtmlLoadDetailIn extends BaseQueueTaskIn implements Serializable{

    private String  district;
    private Long cityId;
    private String link;
    private List<String>hrefs;
   public HtmlLoadDetailIn(){
       super.setTaskKey(HtmlLoadDetailIn.class.getSimpleName());
   }
   public HtmlLoadDetailIn(Long _cityId, String  _district, List<String>_hrefs,String _link){
       super.setTaskKey(HtmlLoadDetailIn.class.getSimpleName());
       this.district=_district;
       this.cityId=_cityId;
       this.hrefs=_hrefs;
       this.link=_link;
   }
}
