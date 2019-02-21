package com.honstat.crawler.models.out;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.interfaces.model.out
 * @Description: TODO
 * @date 2018/11/4 13:06
 */

public class EchatsBuildManager {
    public static CommonEcharts bulid(TitleModelRes titlemodel,String type, Map<String,List<String>>dataMap, List<String>title,List<String>tab)throws Exception{
        CommonEcharts echart=new CommonEcharts();
        echart.setTitle(titlemodel);
        DateShowCommponent tabData=new DateShowCommponent();
        tabData.setData(tab);
        echart.setLegend(tabData);
        if(dataMap!=null&&dataMap.size()>0&&title!=null){
            List<XdataCommponent>xdataCommponents=new ArrayList<>();
            for (String key: dataMap.keySet()) {
              XdataCommponent xdata=new XdataCommponent(key,type,dataMap.get(key));
                xdataCommponents.add(xdata);
                if((xdata.getData()==null|xdata.getData().size()==0||title==null)|| title.size()!=xdata.getData().size()){
                    throw new Exception("下标与参数值长度不匹配",null);
                }
            }
            echart.setSeries(xdataCommponents);
        }
        DateShowCommponent dateCommponent=new DateShowCommponent();
        dateCommponent.setType("category");
        dateCommponent.setData(title);
        echart.setAxis( Arrays.asList(dateCommponent));
        return echart;
    };
}
