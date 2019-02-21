package com.honstat.crawler.models.out;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.interfaces.model.out
 * @Description: TODO
 * @date 2018/10/22 16:08
 */
@Data
public class GetAnalysisByDistrictRes implements Serializable{
    private int up;
    private int down;
    private int eq;
    private int total;
    private Map<String,List<String>>listMap;
}
