package com.honstat.house.manager.fang;


import com.honstat.crawler.models.in.AddTwoHouseInfoIn;
import com.honstat.house.service.dao.service.TwoHouseInfoDaoService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.manager
 * @Description: TODO
 * @date 2018/12/19 13:55
 */
@Service
public class TwoHouseInfoServiceManager {
    Logger logger = Logger.getLogger(TwoHouseInfoServiceManager.class);
    @Autowired
    TwoHouseInfoDaoService twoHouseService;
    final List<AddTwoHouseInfoIn> list = new ArrayList<>();

    public synchronized boolean add(AddTwoHouseInfoIn in) {
        list.add(in);
        if (list.size() > 60) {

            boolean result = twoHouseService.insert(in)>0;
            if (result) {
                logger.info("批量插入TwoHouseInfo完成！数量：" + list.size());
                list.clear();
            }
        }
        return true;
    }

    public boolean batchAdd(List<AddTwoHouseInfoIn> in) {
        if (in.size() == 0) {
            return false;
        }
        boolean result =twoHouseService.insertList(in)>0;
        if (result) {
            logger.info("批量插入TwoHouseInfo完成！数量：" + in.size());
        }
        return result;
    }
}
