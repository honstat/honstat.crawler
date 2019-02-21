package com.honstat.house.service.dao.mapper;
import com.honstat.house.service.dao.model.TwoHouseRealAnalysis;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.BaseMapper;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;


/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.dao.mapper
 * @Description: 实时统计
 * @date 2018/12/12 13:47
 */
@org.apache.ibatis.annotations.Mapper
public interface TwoHouseRealAnalysisMapper  extends Mapper<TwoHouseRealAnalysis>,MySqlMapper<TwoHouseRealAnalysis> {
}
