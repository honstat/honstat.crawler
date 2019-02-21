package com.honstat.house.service.dao.service;

import com.honstat.crawler.models.enums.LookTradeAnaylsisType;
import com.honstat.crawler.models.in.AddTwoHouseTradeInfoIn;
import com.honstat.crawler.models.in.QueryTwoHouseTradeByConditionIn;
import com.honstat.crawler.models.out.CommonEcharts;
import com.honstat.crawler.models.out.EchatsBuildManager;
import com.honstat.crawler.models.out.TitleModelRes;
import com.honstat.house.service.dao.mapper.TwoHouseTradeInfoMapper;
import com.honstat.house.service.dao.model.TwoHouseAnalysisInfo;
import com.honstat.house.service.dao.model.TwoHouseRealAnalysis;
import com.honstat.house.service.dao.model.TwoHouseTradeInfo;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.dao.service
 * @Description: TODO
 * @date 2018/10/25 17:54
 */
@Service
public class TwoHouseTradeInfoDaoService    {
    @Autowired
    TwoHouseTradeInfoMapper mapper;

    public List<TwoHouseTradeInfo> selectByExample(Example example){

        return mapper.selectByExample(example);
    }
    public Integer insert(TwoHouseTradeInfo  model){

        return mapper.insert(model);
    }
    public Integer insertList(List<TwoHouseTradeInfo>  list){
        List<TwoHouseTradeInfo> newlist = new ArrayList<>(list.size());
        for (TwoHouseTradeInfo x : list) {
            TwoHouseTradeInfo dbtwoinfo = new TwoHouseTradeInfo();
            BeanUtils.copyProperties(x, dbtwoinfo);
            dbtwoinfo.setId(0L);
            newlist.add(dbtwoinfo);
        }
        return mapper.insertList(list);
    }

    public boolean BatchInsertData(List<AddTwoHouseTradeInfoIn> items) {
        List<TwoHouseTradeInfo> list = new ArrayList<>();
        for (AddTwoHouseTradeInfoIn model : items) {
            TwoHouseTradeInfo info = new TwoHouseTradeInfo();
            BeanUtils.copyProperties(model, info);
            info.setId(0L);
            list.add(info);
        }
        return mapper.insertList(list) > 0;
    }

    private List<TwoHouseTradeInfo> queryByCondtion(QueryTwoHouseTradeByConditionIn in) {
        Example example = new Example(TwoHouseTradeInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("cityId", in.getCityld());
        if (!StringUtil.isEmpty(in.getDistrict())) {
            criteria.andEqualTo("district", in.getDistrict());
        }
        if (!StringUtil.isEmpty(in.getCoummunity())) {
            criteria.andEqualTo("coummunity", in.getCoummunity());
        }
        if (in.getStart() != null) {
            criteria.andGreaterThanOrEqualTo("tradeDate", in.getStart());
        }
        if (in.getEnd() != null && in.getEnd().compareTo(in.getStart()) > 0) {
            criteria.andLessThanOrEqualTo("tradeDate", in.getEnd());
        }
        if (in.getMinPrice() != null && in.getMinPrice() > 0) {
            criteria.andGreaterThanOrEqualTo("realAmount", in.getMinPrice());
        }
        if (in.getMaxPrice() != null && in.getMaxPrice() > 0) {
            criteria.andLessThanOrEqualTo("realAmount", in.getMaxPrice());
        }
        if (!StringUtil.isEmpty(in.getStyle())) {
            criteria.andEqualTo("style", in.getStyle());
        }

        int total = mapper.selectCountByExample(example);
        RowBounds rowBounds = new RowBounds(0, total);
        return mapper.selectByExampleAndRowBounds(example, rowBounds);

    }

    //按区县分组排序（城市，时间）
    public CommonEcharts getAnalysisByDistrict(QueryTwoHouseTradeByConditionIn in) {
        if (!StringUtil.isEmpty(in.getDistrict())) {
            in.setDistrict(null);
        }
        List<TwoHouseTradeInfo> tradeInfos = queryByCondtion(in);
        CommonEcharts res = null;
        if (tradeInfos != null || tradeInfos.size() > 0) {
            try {

                res = converTo(tradeInfos, in, LookTradeAnaylsisType.District);
            } catch (Exception e) {
            }
            return res;
        }

        return res;
    }

    //按月份分组排序(搜索条件：城市，小区)
    public CommonEcharts getAnalysisByMonth(QueryTwoHouseTradeByConditionIn in) {
        List<TwoHouseTradeInfo> tradeInfos = queryByCondtion(in);
        CommonEcharts res = null;
        if (tradeInfos != null || tradeInfos.size() > 0) {
            try {
                res = converTo(tradeInfos, in, LookTradeAnaylsisType.Month);
            } catch (Exception e) {
            }
            return res;
        }

        return res;
    }

    //按小区分组排序（城市，时间，区县）
    public CommonEcharts getAnalysisByCoummunity(QueryTwoHouseTradeByConditionIn in) {
        if (!StringUtil.isEmpty(in.getCoummunity())) {
            in.setCoummunity(null);
        }
        List<TwoHouseTradeInfo> tradeInfos = queryByCondtion(in);
        CommonEcharts res = null;
        if (tradeInfos != null || tradeInfos.size() > 0) {
            try {

                res = converTo(tradeInfos, in, LookTradeAnaylsisType.Community);
            } catch (Exception e) {
            }
            return res;
        }

        return res;
    }

    private CommonEcharts converTo(List<TwoHouseTradeInfo> tradeInfos, QueryTwoHouseTradeByConditionIn queryIn, LookTradeAnaylsisType useType) throws Exception {
        Map<String, List<TwoHouseTradeInfo>> dataMaps = null;
        //底部文案日期
        List<String> title = new ArrayList<>();
        //具体数值指标
        Map<String, List<String>> maps = new HashMap<>();
        //第一项
        List<String> dataOne = new ArrayList<>();
        //第二项
        List<String> dataTwo = new ArrayList<>();
        //标题选项
        List<String> tab = new ArrayList<>();
        switch (useType.getValue()) {
            case 1:
                dataMaps = tradeInfos.stream().collect(Collectors.groupingBy(TwoHouseTradeInfo::getDistrict));
                tab.add("销量");
                tab.add("均价");
                for (String key : dataMaps.keySet()) {
                    title.add(key);
                    List<TwoHouseTradeInfo> list = dataMaps.get(key);
                    if (list != null && list.size() > 0) {
                        BigDecimal sum = list.stream().map(TwoHouseTradeInfo::getAvgAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                        dataOne.add(String.valueOf(list.size()));
                        dataTwo.add(String.valueOf((Math.floor(sum.doubleValue() / list.size()))));
                    }
                }
                TitleModelRes titleModelRes = new TitleModelRes("按区县统计", "成交量/均价");
                maps.put(tab.get(0), dataOne);
                maps.put(tab.get(1), dataTwo);
                return EchatsBuildManager.bulid(titleModelRes, queryIn.getShowType(), maps, title, tab);
            case 2:
                //按小区
                Map<String, Double> sortmap = new HashMap<>();
                dataMaps = tradeInfos.stream().collect(Collectors.groupingBy(TwoHouseTradeInfo::getCoummunity));
                tab.add("销量");
                tab.add("均价");
                for (String key : dataMaps.keySet()) {
                    List<TwoHouseTradeInfo> list = dataMaps.get(key);
                    if (list != null && list.size() > 0) {
                        // BigDecimal avg=list.stream().map( TwoHouseTradeInfo::getRealAmount).reduce(BigDecimal.ZERO,BigDecimal::add);
                        sortmap.put(key, Double.valueOf(list.size()));
                    }
                }
                List<Map.Entry<String, Double>> templist = new ArrayList<Map.Entry<String, Double>>(sortmap.entrySet());
                Collections.sort(templist, new Comparator<Map.Entry<String, Double>>() {
                    @Override
                    public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                        return o2.getValue().compareTo(o1.getValue());
                    }
                });

                if (templist.size() > 10) {
                    List<Map.Entry<String, Double>> newlist = templist.subList(0, 10);
                    for (Map.Entry<String, Double> map : newlist) {
                        title.add(map.getKey());
                    }
                } else {
                    List<Map.Entry<String, Double>> newlist = templist.stream().collect(Collectors.toList());
                    for (Map.Entry<String, Double> map : newlist) {
                        title.add(map.getKey());
                    }
                }
                for (String key : title) {
                    List<TwoHouseTradeInfo> list = dataMaps.get(key);
                    if (list != null && list.size() > 0) {
                        BigDecimal sum = list.stream().map(TwoHouseTradeInfo::getAvgAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                        dataOne.add(String.valueOf(list.size()));
                        dataTwo.add(String.valueOf(Math.floor(sum.doubleValue() / list.size())));
                    }
                }
                titleModelRes = new TitleModelRes("按小区统计", "top 10");
                maps.put(tab.get(0), dataOne);
                maps.put(tab.get(1), dataTwo);
                return EchatsBuildManager.bulid(titleModelRes, queryIn.getShowType(), maps, title, tab);
            case 3:
                break;
            case 4:
                break;
            case 5:
                dataMaps = new HashMap<>();
                for (TwoHouseTradeInfo info : tradeInfos) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
                    String month = dateFormat.format(info.getTradeDate());

                    if (!dataMaps.containsKey(month)) {
                        title.add(month);
                        dataMaps.put(month, new ArrayList<TwoHouseTradeInfo>());
                    }
                    List<TwoHouseTradeInfo> arr = dataMaps.get(month);
                    arr.add(info);
                    dataMaps.put(month, arr);
                }
                tab.add("销量");
                tab.add("均价");

                title.sort(new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return o1.compareTo(o2);
                    }
                });
                for (String key : title) {
                    List<TwoHouseTradeInfo> list = dataMaps.get(key);
                    if (list != null && list.size() > 0) {
                        BigDecimal sum = list.stream().map(TwoHouseTradeInfo::getAvgAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                        dataOne.add(String.valueOf(list.size()));
                        dataTwo.add(String.valueOf(Math.floor(sum.doubleValue() / list.size())));
                    }
                }
                titleModelRes = new TitleModelRes("按月份统计", "走势图");
                maps.put(tab.get(0), dataOne);
                maps.put(tab.get(1), dataTwo);
                return EchatsBuildManager.bulid(titleModelRes, queryIn.getShowType(), maps, title, tab);

            default:

        }
        return null;
    }
}
