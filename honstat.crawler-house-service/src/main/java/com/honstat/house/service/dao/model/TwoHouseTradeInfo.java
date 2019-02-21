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
 * @date 2018/10/25 17:48
 */
@Data
@Table(name = "two_house_trade_info")
@EqualsAndHashCode(callSuper = false)

public class TwoHouseTradeInfo  implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "JDBC")
    private Long id;
    @Column(name = "city_id")
    private Long cityId;
    @Column(name = "house_id")
    private Long houseId;
    @Column(name = "district")
    private String district;
    @Column(name = "coummunity_id")
    private Long coummunityId;
    @Column(name = "coummunity")
    private String coummunity;
    @Column(name = "style")
    private String style;
    @Column(name = "size")
    private Double size;
    @Column(name = "real_amount")
    private BigDecimal realAmount;
    @Column(name = "avg_amount")
    private BigDecimal avgAmount;
    @Column(name = "sale_price")
    private BigDecimal salePrice;
    @Column(name = "sale_days")
    private Integer saleDays;
    @Column(name = "trade_date")
    private Date tradeDate;
    @Column(name = "create_time")
    private Date createTime;


}
