package com.honstat.crawler.models.in;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.interfaces.model.in
 * @Description: TODO
 * @date 2018/10/22 15:09
 */
@Data
public class GetTwoHouseAnalysisByDistrictIn  implements Serializable{
    @NotNull
private Long cityId;
private String district;
private Integer type;
}
