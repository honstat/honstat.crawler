package com.honstat.crawler.models;

import com.honstat.crawler.models.in.CommonKeyValue;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.interfaces.model.in
 * @Description: TODO
 * @date 2019/1/11 11:13
 */
@Data
public class DistrictRunTimeInfo implements Serializable {
    private Integer pageSize=0;
    private Date execTime;
    //每页执行耗时ms
    private List<Long> times=new ArrayList<>();
    /**等待执行的乡镇**/
    private CopyOnWriteArrayList<CommonKeyValue> waitTowns=new CopyOnWriteArrayList<>();
    private String district;
    private Long linkCount=0L;
    /**执行耗时**/
    private Long eclispe=0L;
    public void addLinkCount(Long count){
        linkCount=linkCount+count;
    }
    public void addPageSize(Integer count){
        pageSize=pageSize+count;
    }
}
