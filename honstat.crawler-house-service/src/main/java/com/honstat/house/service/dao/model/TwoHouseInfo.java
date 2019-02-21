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
 * @date 2018/10/15 14:14
 */
@Data
@Table(name = "two_house_sale_info")
@EqualsAndHashCode(callSuper = false)

public class TwoHouseInfo  implements Serializable {
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
    @Column(name = "logo")
    private String logo;
    @Column(name = "sale_amount")
    private BigDecimal saleAmount;
    @Column(name = "unit")
    private String unit;
    @Column(name = "community")
    private String community;
    @Column(name = "house_year")
    private Integer houseYear;
    @Column(name = "size")
    private Double size;
    @Column(name = "total_price")
    private BigDecimal totalPrice;
    @Column(name = "style")
    private String style;
    @Column(name = "tag")
    private String tag;
    @Column(name = "up_sale_time")
    private Date upSaleTime;
    @Column(name = "sale_title")
    private String saleTitle;
    @Column(name = "direction")
    private String direction;
    @Column(name = "house_url")
    private String houseUrl;
    @Column(name = "get_time")
    private Date getTime;
    //层级
    @Column(name = "foor")
    private String foor;

}
