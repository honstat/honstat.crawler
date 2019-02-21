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
 * @date 2018/10/22 14:14
 */
@Data
public class GetTwoHouseAnalysisByCoummunityIn  implements Serializable {
    @NotNull
    private Long cityId;
    private String coummunityName;
}
