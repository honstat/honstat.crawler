package com.honstat.house.service.dao.service;

import com.honstat.crawler.models.in.AddTwoHouseRealAnalysisIn;
import com.honstat.crawler.models.in.QueryTwoHouseRealAnalysisByConditionIn;
import com.honstat.crawler.models.out.CommonEcharts;
import com.honstat.crawler.models.out.CommonNameValue;
import com.honstat.crawler.models.out.EchatsBuildManager;
import com.honstat.crawler.models.out.TitleModelRes;
import com.honstat.house.service.dao.mapper.TwoHouseRealAnalysisMapper;
import com.honstat.house.service.dao.model.TwoHouseAnalysisInfo;
import com.honstat.house.service.dao.model.TwoHouseRealAnalysis;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.dao.service
 * @Description: TODO
 * @date 2018/12/12 13:49
 */
@Service
public class TwoHouseRealAnalysisDaoService  {
    @Autowired
    TwoHouseRealAnalysisMapper mapper;

    Logger logger=Logger.getLogger(TwoHouseRealAnalysisDaoService.class);
    public List<TwoHouseRealAnalysis> selectByExample(Example example){

        return mapper.selectByExample(example);
    }
    public Integer insert(TwoHouseRealAnalysis  model){

        return mapper.insert(model);
    }
    public Integer insertList(List<TwoHouseRealAnalysis>list){
        List<TwoHouseAnalysisInfo> newlist = new ArrayList<>(list.size());
        for (TwoHouseRealAnalysis x : list) {
            TwoHouseAnalysisInfo dbtwoinfo = new TwoHouseAnalysisInfo();
            BeanUtils.copyProperties(x, dbtwoinfo);
            dbtwoinfo.setId(0L);
            newlist.add(dbtwoinfo);
        }
        return mapper.insertList(list);
    }

    private int getIndex(String str) {
        return str.hashCode() % 4;
    }

    public boolean addRealAnalysis(AddTwoHouseRealAnalysisIn in)  {
        if (in.getDistrict() != null && in.getCommunity() != null) {
            int exeCount = 0;
            do {
                exeCount++;
                TwoHouseRealAnalysis dbentity = get(in.getDistrict(), in.getCommunity());
                if (dbentity != null) {
                    //update

                    dbentity.setUpdateTime(new Date());
                    if (in.getType().equals(2)) {
                        //统计挂单
                        dbentity.setWaitSaleCount(dbentity.getWaitSaleCount() + 1);
                    } else {
                        //统计成交
                        Double totalAmount = (dbentity.getTradeTotalAmount() == null ? 0.0 : dbentity.getTradeTotalAmount()) + in.getTradeAmount();
                        dbentity.setTradeTotalAmount(totalAmount);
                        dbentity.setTradeCount(dbentity.getTradeCount() + 1);
                        //计算平均值
                        Double avg = totalAmount / (dbentity.getTradeCount() == 0 ? 1 : dbentity.getTradeCount());
                        dbentity.setTradeAvgPrice(avg);
                    }
                    return mapper.updateByPrimaryKey(dbentity) > 0;

                } else {
                    //add
                    dbentity = new TwoHouseRealAnalysis();
                    BeanUtils.copyProperties(in, dbentity);
                    if (in.getType().equals(2)) {
                        //挂单，统计挂单数
                        dbentity.setWaitSaleCount(1);
                    } else {
                        //成交，统计成交金额，均价
                        dbentity.setTradeTotalAmount(in.getTradeAmount());
                        dbentity.setTradeCount(1);
                        dbentity.setTradeAvgPrice(in.getTradeAmount());
                    }
                    dbentity.setUpdateTime(new Date());
                    try {
                        return mapper.insert(dbentity) > 0;
                    } catch (Exception e) {
                        logger.error("统计挂单成交时异常：" + e.getLocalizedMessage());
                    }
                }
            } while (get(in.getDistrict(), in.getCommunity()) == null && exeCount < 3);

        }


        return true;
    }

    private TwoHouseRealAnalysis get(String distrct, String community) {
        Example example = new Example(TwoHouseRealAnalysis.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("district", distrct);
        criteria.andEqualTo("community", community);
        return mapper.selectOneByExample(example);
    }

    private List<TwoHouseRealAnalysis> queryByCondition(QueryTwoHouseRealAnalysisByConditionIn in) {
        Example example = new Example(TwoHouseRealAnalysis.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("cityId", in.getCityId());
        criteria.andEqualTo("district", in.getDistrict());
        if (in.getCommunity() != null && in.getCommunity().length() > 0) {
            criteria.andEqualTo("community", in.getCommunity());
        }
        if (in.getStartAmount() != null && in.getStartAmount().doubleValue() > 0) {
            criteria.andGreaterThanOrEqualTo("tradeAvgPrice", in.getStartAmount());
        }
        if (in.getEndAmount() != null && in.getEndAmount().doubleValue() > 0) {
            criteria.andLessThanOrEqualTo("tradeAvgPrice", in.getEndAmount());
        }
        return mapper.selectByExample(example);
    }

    public CommonEcharts getRealAnalysis(QueryTwoHouseRealAnalysisByConditionIn in) throws Exception {
        List<TwoHouseRealAnalysis> list = queryByCondition(in);
        return converTo(list, in);
    }

    private CommonEcharts converTo(List<TwoHouseRealAnalysis> list, QueryTwoHouseRealAnalysisByConditionIn in) throws Exception {
        CommonEcharts echarts = new CommonEcharts();
        //底部文案日期

        List<String> title = new ArrayList<>();
        //具体数值指标
        Map<String, List<String>> maps = new HashMap<>();
        //第一项
        List<String> dataOne = new ArrayList<>(20);
        //第二项
        List<String> dataTwo = new ArrayList<>(20);
        //第三项
        List<String> dataThree = new ArrayList<>(20);
        //标题选项
        List<String> tab = new ArrayList<>();
        //挂单量、成交量、均价,
        tab.add("挂单量");
        tab.add("成交量");
        tab.add("均价");
        TitleModelRes titleModelRes = new TitleModelRes("二手房实时统计", "链接的二手交易，房天下的挂单");
        echarts.setTitle(titleModelRes);
        if (list == null || list.size() == 0) {
            maps.put(tab.get(0), null);
            maps.put(tab.get(1), null);
            maps.put(tab.get(2), null);
            return echarts;
        } else {
            Map<String, List<TwoHouseRealAnalysis>> tempMap = null;
            Map<String, List<CommonNameValue>> tagDatas = new HashMap<>();

            /**统计维度，1=按小区（取top20）2=按时间（月份）3=按区县 **/
            /**
             * 排序字段：0，1=挂单数 2=成交量 3=成交均价
             * **/
            switch (in.getAnalysisType()) {
                case 1:

                    list.sort(new Comparator<TwoHouseRealAnalysis>() {
                        @Override
                        public int compare(TwoHouseRealAnalysis o1, TwoHouseRealAnalysis o2) {
                            if (in.getIsAsc()) {
                                if (in.getSortField() == 1) {
                                    return o1.getWaitSaleCount().compareTo(o2.getWaitSaleCount());
                                } else if (in.getSortField() == 2) {
                                    return o1.getTradeCount().compareTo(o2.getTradeCount());
                                } else if (in.getSortField() == 3) {
                                    return o1.getTradeAvgPrice().compareTo(o2.getTradeAvgPrice());
                                }

                            } else {

                                if (in.getSortField() == 1) {
                                    return o2.getWaitSaleCount().compareTo(o1.getWaitSaleCount());
                                } else if (in.getSortField() == 2) {
                                    return o2.getTradeCount().compareTo(o1.getTradeCount());
                                } else if (in.getSortField() == 3) {
                                    return o2.getTradeAvgPrice().compareTo(o1.getTradeAvgPrice());
                                }
                            }
                            return o2.getWaitSaleCount().compareTo(o1.getWaitSaleCount());
                        }

                    });
                    List<TwoHouseRealAnalysis> temps = null;
                    if (list.size() > 20) {
                        temps = list.subList(0, 20);
                    } else {
                        temps = list;
                    }
                    temps.forEach(x -> {
                        title.add(x.getCommunity());
                        dataOne.add(String.valueOf(x.getWaitSaleCount()));
                        dataTwo.add(String.valueOf(x.getTradeCount()));
                        dataThree.add(String.valueOf(x.getTradeAvgPrice()));

                    });

                    break;
                case 2:
                    //todo 暂时无法实现
                    break;
                case 3:
                    List<CommonNameValue> commonNameValues = new ArrayList<>();

                    Map<String, List<TwoHouseRealAnalysis>> districtmaps = list.stream().collect(Collectors.groupingBy(TwoHouseRealAnalysis::getDistrict));
                    for (String district :
                            districtmaps.keySet()) {
                        List<TwoHouseRealAnalysis> dist_temp = districtmaps.get(district);
                        if (in.getSortField() == 1) {
                            commonNameValues.add(new CommonNameValue(district, dist_temp.stream().mapToInt(TwoHouseRealAnalysis::getWaitSaleCount).sum()));
                        } else if (in.getSortField() == 2) {
                            commonNameValues.add(new CommonNameValue(district, dist_temp.stream().mapToInt(TwoHouseRealAnalysis::getTradeCount).sum()));
                        } else if (in.getSortField() == 3) {
                            commonNameValues.add(new CommonNameValue(district, dist_temp.stream().mapToDouble(TwoHouseRealAnalysis::getTradeAvgPrice).sum()));
                        }
                    }
                    //排序

                    commonNameValues.sort(new Comparator<CommonNameValue>() {
                        @Override
                        public int compare(CommonNameValue o1, CommonNameValue o2) {
                            if (in.getIsAsc()) {
                                return o2.getValue().doubleValue() < o1.getValue().doubleValue() ? -1 : o2.getValue().doubleValue() == o1.getValue().doubleValue() ? 0 : 1;
                            } else {
                                return o1.getValue().doubleValue() < o2.getValue().doubleValue() ? -1 : o1.getValue().doubleValue() == o2.getValue().doubleValue() ? 0 : 1;
                            }

                        }
                    });
                    if (list.size() > 20) {
                        commonNameValues = commonNameValues.subList(0, 20);
                    } else {
                        commonNameValues = commonNameValues;
                    }
                    commonNameValues.forEach(y -> {
                        title.add(y.getItem());
                        List<TwoHouseRealAnalysis> temp = districtmaps.get(y.getItem());
                        if (in.getSortField() == 1) {
                            dataOne.add(String.valueOf(y.getValue()));
                            dataTwo.add(String.valueOf(temp.stream().mapToDouble(TwoHouseRealAnalysis::getTradeCount).sum()));
                            dataThree.add(String.valueOf(temp.stream().mapToDouble(TwoHouseRealAnalysis::getTradeAvgPrice).sum()));
                        } else if (in.getSortField() == 2) {
                            dataOne.add(String.valueOf(temp.stream().mapToLong(TwoHouseRealAnalysis::getTradeCount).sum()));
                            dataTwo.add(String.valueOf(y.getValue()));
                            dataThree.add(String.valueOf(temp.stream().mapToDouble(TwoHouseRealAnalysis::getTradeAvgPrice).sum()));
                        } else if (in.getSortField() == 3) {
                            dataOne.add(String.valueOf(temp.stream().mapToLong(TwoHouseRealAnalysis::getTradeCount).sum()));
                            dataTwo.add(String.valueOf(temp.stream().mapToDouble(TwoHouseRealAnalysis::getTradeCount).sum()));
                            dataThree.add(String.valueOf(y.getValue()));
                        }
                    });
                    break;
                default:
                    break;
            }
            maps.put(tab.get(0), dataOne);
            maps.put(tab.get(1), dataTwo);
            maps.put(tab.get(2), dataThree);
        }
        return EchatsBuildManager.bulid(titleModelRes, "bar", maps, title, tab);
    }

}
