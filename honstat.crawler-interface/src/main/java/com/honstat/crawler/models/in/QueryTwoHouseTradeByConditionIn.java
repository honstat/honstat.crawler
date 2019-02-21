package com.honstat.crawler.models.in;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.interfaces.model.in
 * @Description: TODO
 * @date 2018/11/2 17:39
 */
@Data
public class QueryTwoHouseTradeByConditionIn implements Serializable{
    private Long cityld;
    private Date start;
    private Date end;
    private Boolean isAsc;
    private String orderFiled;
    private String groupbyFiled;
    private String style;
    private Boolean isTop;
    private Double minPrice;
    private Double maxPrice;
    private String district;
    private String coummunity;
    private String showType;
    //1=按区县分组，2=按小区分组 5=按月份分组
    private Integer queryMethod;
    private Integer timeType;
}
