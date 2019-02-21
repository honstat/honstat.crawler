package com.honstat.crawler.models.in;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.interfaces.model.in
 * @Description: TODO
 * @date 2018/10/15 15:39
 */
@Data
public class AddTwoHouseInfoIn implements Serializable {

    private Long cityId;
   @Min(1)
    private Long houseId;
   @NotNull
    private String district;
   private String logo;
   @Min(1)
    private BigDecimal saleAmount;
   @NotNull
    private String unit;
    private String community;
    @Min(1900)
    private Integer houseYear;
    @Min(5)
    private Double size;
    @Min(1000)
    private BigDecimal totalPrice;
    private String style;
    private String tag;
    private Date upSaleTime;
    @NotNull
    private String saleTitle;
    private String direction;
    //层次高度
    private String foor;
    private String houseUrl;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss" )
    private Date getTime;

}
