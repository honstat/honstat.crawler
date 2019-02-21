package com.honstat.crawler.models.in;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.interfaces.model.in
 * @Description: TODO
 * @date 2018/12/25 16:37
 */
@Data
public class HistoryStepInfoIn extends BaseHouseIn  implements Serializable {
    private String district;
    private Integer pageIndex=0;
    private String link;
    private Integer pageSize;
    private Boolean isGetHistory=false;
    /**乡镇**/
    private String town;
    private Boolean isComplete=false;
    private String key;
}
