package com.honstat.house.service.dao.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.dao.model
 * @Description: TODO
 * @date 2018/12/12 11:12
 */
@Data
@Table(name = "two_house_real_analysis")
@EqualsAndHashCode(callSuper = false)

public class TwoHouseRealAnalysis implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "JDBC")
    private Long id=0L;
    @Column(name = "city_id")
    private Long cityId=0L;
    @Column(name = "district")
    private String district;
    @Column(name = "community")
    private String community;
    @Column(name = "wait_sale_count")
    private Integer waitSaleCount=0;
    @Column(name = "trade_count")
    private Integer tradeCount=0;
    @Column(name = "trade_avg_price")
    private Double tradeAvgPrice=0.0;
    @Column(name = "trade_total_amount")
    private Double tradeTotalAmount=0.0;
    @Column(name = "update_time")
    private Date updateTime;

}
