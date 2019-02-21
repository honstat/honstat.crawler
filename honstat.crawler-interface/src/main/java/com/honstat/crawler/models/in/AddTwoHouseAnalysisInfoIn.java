package com.honstat.crawler.models.in;


import lombok.Data;
import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.interfaces.model.in
 * @Description: TODO
 * @date 2018/10/19 14:41
 */
@Data
public class AddTwoHouseAnalysisInfoIn implements Serializable{
    private List<TimeAndValue> list;
    private Long cityId;
    private String district;
    private Long coummunityId;
    private String coummunity;
    private String address;
    private Date saleDate;

}
