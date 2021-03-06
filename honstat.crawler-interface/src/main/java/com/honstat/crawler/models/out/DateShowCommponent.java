package com.honstat.crawler.models.out;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.interfaces.model.out
 * @Description: TODO
 * @date 2018/11/4 12:58
 */
@Data
public class DateShowCommponent implements Serializable{
    private String type;
    private List<String>data;
}
