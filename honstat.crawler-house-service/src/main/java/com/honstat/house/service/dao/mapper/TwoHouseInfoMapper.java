package com.honstat.house.service.dao.mapper;


import com.honstat.house.service.dao.model.TwoHouseInfo;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.BaseMapper;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.dao.mapper
 * @Description: TODO
 * @date 2018/10/15 15:22
 */
@org.apache.ibatis.annotations.Mapper
public interface TwoHouseInfoMapper extends Mapper<TwoHouseInfo>,MySqlMapper<TwoHouseInfo> {
}
