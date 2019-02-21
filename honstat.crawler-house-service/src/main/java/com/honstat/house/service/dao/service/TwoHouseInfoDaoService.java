package com.honstat.house.service.dao.service;

import com.honstat.crawler.models.in.AddTwoHouseInfoIn;
import com.honstat.house.service.dao.mapper.TwoHouseInfoMapper;

import com.honstat.house.service.dao.model.TwoHouseInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.dao.service
 * @Description: TODO
 * @date 2018/10/16 15:16
 */
@Service
public class TwoHouseInfoDaoService {
    @Autowired
    TwoHouseInfoMapper mapper;
    public List<TwoHouseInfo> selectByExample(Example example){

        return mapper.selectByExample(example);
    }
    public Integer insert(AddTwoHouseInfoIn  model){
        TwoHouseInfo dbtwoinfo = new TwoHouseInfo();
        BeanUtils.copyProperties(model, dbtwoinfo);
        return mapper.insert(dbtwoinfo);
    }
    public Integer insertList(List<AddTwoHouseInfoIn>  list){
        List<TwoHouseInfo> newlist = new ArrayList<>(list.size());
        for (AddTwoHouseInfoIn x : list) {
            TwoHouseInfo dbtwoinfo = new TwoHouseInfo();
            BeanUtils.copyProperties(x, dbtwoinfo);
            dbtwoinfo.setId(0L);
            newlist.add(dbtwoinfo);
        }
        return mapper.insertList(newlist);
    }
}
