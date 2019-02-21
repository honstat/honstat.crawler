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
 * @date 2018/10/22 14:40
 */
@Data
public class CommonEcharts  implements Serializable{
private TitleModelRes title;
private DateShowCommponent legend;
private List<XdataCommponent> series;
private String  yAxis="  [" + " {" + "type : 'value'"+ " }"+ " ]";
private List<DateShowCommponent>  Axis;
}
