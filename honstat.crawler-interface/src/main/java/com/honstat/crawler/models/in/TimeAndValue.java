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
 * @date 2018/10/19 14:39
 */
@Data
public class TimeAndValue implements Serializable{
    private Date time;
    private BigDecimal value;
}
