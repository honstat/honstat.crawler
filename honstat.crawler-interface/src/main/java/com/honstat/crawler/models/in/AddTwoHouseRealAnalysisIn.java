package com.honstat.crawler.models.in;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.interfaces.model.in
 * @Description: TODO
 * @date 2018/12/12 14:07
 */
@Data
public class AddTwoHouseRealAnalysisIn implements Serializable {
    private Long cityId=0L;
    private String district;
    private String community;
    /**1.挂单 2.成交**/
    private Integer type=0;
    /**成交价，挂单则不填写**/
    private Double tradeAmount=0.0;

}
