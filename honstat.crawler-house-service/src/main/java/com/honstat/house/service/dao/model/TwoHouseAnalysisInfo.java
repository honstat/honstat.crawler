package com.honstat.house.service.dao.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.dao.model
 * @Description: TODO
 * @date 2018/10/19 14:25
 */
@Data
@Table(name = "two_house_analysis_info")
@EqualsAndHashCode(callSuper = false)

public class TwoHouseAnalysisInfo  implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "JDBC")
    private Long id;
    @Column(name = "city_id")
    private Long cityId;
    @Column(name = "district")
    private String district;
    @Column(name = "sale_amount")
    private BigDecimal saleAmount;
    @Column(name = "coummunity")
    private String coummunity;
    @Column(name = "coummunity_id")
    private Long coummunityId;
    @Column(name = "address")
    private String address;
    @Column(name = "sale_date")
    private Date saleDate;
    @Column(name = "analysis_date")
    private Date analysisTime;
    @Column(name = "create_time")
    private Date createTime;

}
