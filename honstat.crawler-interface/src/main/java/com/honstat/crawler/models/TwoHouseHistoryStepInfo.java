package com.honstat.crawler.models;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.models.monitor
 * @Description: TODO
 * @date 2019/1/21 10:46
 */
@Data
public class TwoHouseHistoryStepInfo implements Serializable{
    private Long cityId;
    private String district;
    private String town;
    private String link;
    private Long pageSize;
    private Integer pageIndex;
}
