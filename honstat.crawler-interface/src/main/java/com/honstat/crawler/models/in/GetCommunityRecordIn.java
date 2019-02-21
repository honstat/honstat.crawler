package com.honstat.crawler.models.in;

import com.honstat.crawler.models.in.BaseQueueTaskIn;
import lombok.Data;

import java.io.Serializable;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.interfaces.model.in
 * @Description: 拉取小区成交走势
 * @date 2019/1/16 18:56
 */
@Data
public class GetCommunityRecordIn extends BaseQueueTaskIn implements Serializable {
    private Long coummunityId;
    private String district;
    private String coummunity;
    private String address;
    private Long cityId;
    public GetCommunityRecordIn(Long _cityId,String _district,Long _coummunityId,String _coummunity,String _address){
        super.setTaskKey(GetCommunityRecordIn.class.getSimpleName());
        this.cityId=_cityId;
        this.district=_district;
        this.coummunityId=_coummunityId;
        this.coummunity=_coummunity;
        this.address=_address;
    }
}
