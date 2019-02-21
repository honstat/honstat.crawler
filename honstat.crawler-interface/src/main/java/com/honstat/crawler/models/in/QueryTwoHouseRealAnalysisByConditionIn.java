package com.honstat.crawler.models.in;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.interfaces.model.in
 * @Description: TODO
 * @date 2018/12/21 11:13
 */
@Data
public class QueryTwoHouseRealAnalysisByConditionIn implements Serializable {
    @NotNull
    private Long cityId;
    @NotNull
    private String district;
    @Size(min =0,max = 20)
    private String community;
    /**均价开始**/

    private Double startAmount;
    private Double endAmount;
    /**统计维度，1=按小区（取top20）2=按时间（月份）3=按区县 **/
    @Size(min=1,max =3 )
    private Integer analysisType;
    @Size(min=1,max =3 )
    private Integer sortField;
    private Boolean isAsc;
}
