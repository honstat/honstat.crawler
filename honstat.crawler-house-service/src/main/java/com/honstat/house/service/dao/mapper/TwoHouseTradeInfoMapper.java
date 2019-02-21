package com.honstat.house.service.dao.mapper;

import com.honstat.house.service.dao.model.TwoHouseTradeInfo;
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
 * @date 2018/10/25 17:53
 */
@org.apache.ibatis.annotations.Mapper
public interface TwoHouseTradeInfoMapper  extends Mapper<TwoHouseTradeInfo> ,MySqlMapper<TwoHouseTradeInfo> {
}
