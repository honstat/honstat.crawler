package com.honstat.crawler.models.in;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.interfaces.model.in
 * @Description: TODO
 * @date 2018/10/25 17:57
 */
@Data
public class AddTwoHouseTradeInfoIn implements Serializable {

        private Long cityId;

        private Long houseId;

        private String district;

        private Long coummunityId;

        private String coummunity;

        private String style;

        private Double size;

        private BigDecimal realAmount;

        private BigDecimal avgAmount;

        private BigDecimal salePrice;

        private Integer saleDays;

        private Date tradeDate;

        private Date createTime;
}
