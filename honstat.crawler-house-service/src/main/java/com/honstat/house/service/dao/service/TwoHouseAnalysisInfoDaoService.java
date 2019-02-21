package com.honstat.house.service.dao.service;

import com.honstat.crawler.models.in.GetTwoHouseAnalysisByCoummunityIn;
import com.honstat.crawler.models.in.GetTwoHouseAnalysisByDistrictIn;
import com.honstat.crawler.models.in.TimeAndValue;
import com.honstat.crawler.models.out.CommonEcharts;
import com.honstat.crawler.models.out.EchatsBuildManager;
import com.honstat.crawler.models.out.GetAnalysisByDistrictRes;
import com.honstat.crawler.models.out.TitleModelRes;
import com.honstat.crawler.models.in.AddTwoHouseAnalysisInfoIn;
import com.honstat.house.service.dao.mapper.TwoHouseAnalysisInfoMapper;
import com.honstat.house.service.dao.model.TwoHouseAnalysisInfo;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.dao.service
 * @Description: TODO
 * @date 2018/10/19 14:27
 */
@Service
public class TwoHouseAnalysisInfoDaoService  {

    @Autowired
    TwoHouseAnalysisInfoMapper twoHouseAnalysisInfoMapper;

    public List<TwoHouseAnalysisInfo> selectByExample(Example example){

       return twoHouseAnalysisInfoMapper.selectByExample(example);
    }
    public Boolean insert(AddTwoHouseAnalysisInfoIn  in){


        List<TwoHouseAnalysisInfo> list = new ArrayList<>();
        if (in.getList() == null || in.getList().size() == 0) {
            return false;
        }
        //先删除数据库数据，再覆盖
        deleteHouseAnalysisByCoummunityId(in.getCityId(), in.getCoummunityId());
        for (TimeAndValue item : in.getList()) {
            TwoHouseAnalysisInfo ainfo = new TwoHouseAnalysisInfo();
            ainfo.setId(0L);
            ainfo.setCityId(in.getCityId());
            ainfo.setAddress(in.getAddress());
            ainfo.setCreateTime(new Date());
            ainfo.setCoummunity(in.getCoummunity());
            ainfo.setCoummunityId(in.getCoummunityId());
            ainfo.setDistrict(in.getDistrict());
            ainfo.setSaleDate(in.getSaleDate());
            ainfo.setAnalysisTime(item.getTime());
            ainfo.setSaleAmount(item.getValue());
            ainfo.setSaleDate(new Date());
            list.add(ainfo);
        }
        return twoHouseAnalysisInfoMapper.insertList(list) > 0;
    }
    public boolean deleteHouseAnalysisByCoummunityId(Long cityId, Long coummunityId) {
        Example example = new Example(TwoHouseAnalysisInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("cityId", cityId);
        if (coummunityId > 0) {
            criteria.andEqualTo("coummunityId", coummunityId);
        } else {
            return false;
        }
        int total = twoHouseAnalysisInfoMapper.deleteByExample(example);
        return total > 0;
    }

    public List<TwoHouseAnalysisInfo> queryHouseAnalysis(GetTwoHouseAnalysisByCoummunityIn in) {
        Example example = new Example(TwoHouseAnalysisInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("cityId", in.getCityId());
        if (in.getCoummunityName() != null && in.getCoummunityName().length() > 1) {
            criteria.andLike("coummunity", in.getCoummunityName());
        }
        return twoHouseAnalysisInfoMapper.selectByExample(example);
    }

    public CommonEcharts getCoummunityEchart(GetTwoHouseAnalysisByCoummunityIn in) throws Exception {
        List<TwoHouseAnalysisInfo> list = queryHouseAnalysis(in);
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
        TitleModelRes titleModelRes = new TitleModelRes("小区走势", "均价");

        if (list != null && list.size() > 0) {
            Collections.sort(list, new Comparator<TwoHouseAnalysisInfo>() {
                @Override
                public int compare(TwoHouseAnalysisInfo o1, TwoHouseAnalysisInfo o2) {
                    return o1.getAnalysisTime().compareTo(o2.getAnalysisTime());
                }
            });
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
            tab.add("均价");
            for (TwoHouseAnalysisInfo info : list) {
                String month = dateFormat.format(info.getAnalysisTime());
                title.add(month);
                dataOne.add(info.getSaleAmount().toString());
            }
            maps.put("均价", dataOne);
        }

        return EchatsBuildManager.bulid(titleModelRes, "bar", maps, title, tab);
    }

    public List<TwoHouseAnalysisInfo> queryHouseAnalysisByDistrict(GetTwoHouseAnalysisByDistrictIn in) {
        Example example = new Example(TwoHouseAnalysisInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("cityId", in.getCityId());
        if (in.getDistrict() != null && in.getDistrict().length() > 1) {
            criteria.andEqualTo("district", in.getDistrict());
        }
        int total = twoHouseAnalysisInfoMapper.selectCountByExample(example);
        RowBounds rowBounds = new RowBounds(0, total);

        return twoHouseAnalysisInfoMapper.selectByExampleAndRowBounds(example, rowBounds);

    }


    public GetAnalysisByDistrictRes doAnalysisByBasicForType(GetTwoHouseAnalysisByDistrictIn in) {
        List<TwoHouseAnalysisInfo> list = queryHouseAnalysisByDistrict(in);
        return doAnalysisByBasic(list, in.getType());
    }

    public GetAnalysisByDistrictRes doAnalysisByBasic(List<TwoHouseAnalysisInfo> items, int type) {
        if (items == null) {
            return null;
        }
        GetAnalysisByDistrictRes res = new GetAnalysisByDistrictRes();
        //type:近一月 近三个月 近半年
        //同比
        //找涨（>5%），稳，跌(<5%) 总量
        //按小区分组
        int up = 0, eq = 0, down = 0, total = 0;
        Map<String, List<String>> stringListMap = new HashMap<>();
        String upStr = "";
        String eqStr = "";
        String downStr = "";
        Map<Long, List<TwoHouseAnalysisInfo>> maps = new HashMap<>();

        for (TwoHouseAnalysisInfo info : items) {
            if (maps.containsKey(info.getCoummunityId())) {
                List<TwoHouseAnalysisInfo> twolist = maps.get(info.getCoummunityId());
                twolist.add(info);
            } else {
                List<TwoHouseAnalysisInfo> twolist = new ArrayList<>();
                twolist.add(info);
                maps.put(info.getCoummunityId(), twolist);
            }
        }
        total = maps.size();
        for (Long id : maps.keySet()) {

            List<TwoHouseAnalysisInfo> temp = maps.get(id);
            Collections.sort(temp, new Comparator<TwoHouseAnalysisInfo>() {
                @Override
                public int compare(TwoHouseAnalysisInfo o1, TwoHouseAnalysisInfo o2) {
                    return o1.getAnalysisTime().compareTo(o2.getAnalysisTime());
                }
            });
            //排序完成后，取最近的
            TwoHouseAnalysisInfo endOne = temp.get(temp.size() - 1);
            TwoHouseAnalysisInfo lastOne = null;
            if (type == 1) {
                if (temp.size() >= 2) {
                    lastOne = temp.get(temp.size() - 2);
                } else {
                    lastOne = temp.get(0);
                }

            } else if (type == 2) {
                //近3个月
                if (temp.size() > 3) {
                    lastOne = temp.get(temp.size() - 4);
                } else {
                    lastOne = temp.get(0);
                }

            } else if (type == 3) {
                //近半年
                if (temp.size() > 6) {
                    lastOne = temp.get(temp.size() - 7);
                } else {
                    lastOne = temp.get(0);
                }
            } else {
                //从开始到现在
                lastOne = temp.get(0);
            }
            Long endValue = endOne.getSaleAmount().longValue();
            Long lastOneValue = lastOne.getSaleAmount().longValue();
            if (endValue.compareTo(lastOneValue) > 0) {
                if (endValue > ((endValue * 0.05 + lastOneValue))) {
                    //涨了超过5%
                    up++;
                    NumberFormat nf = NumberFormat.getPercentInstance();
                    nf.setMinimumFractionDigits(0);
                    String pe = nf.format(((endValue - lastOneValue) / (double) lastOneValue));
                    upStr += endOne.getCoummunity() + " " + pe + ",";
                } else {
                    eq++;
                    eqStr += endOne.getCoummunity() + ",";
                }
            } else {
                //跌了，计算幅度
                if ((endValue * 0.05 + endOne.getSaleAmount().longValue()) <= lastOneValue) {
                    //跌了超过5%
                    down++;
                    NumberFormat nf = NumberFormat.getPercentInstance();
                    nf.setMinimumFractionDigits(0);
                    String pe = nf.format(((lastOneValue - endValue) / (double) lastOneValue));
                    downStr += endOne.getCoummunity() + " " + pe + ",";
                } else {
                    eq++;
                    eqStr += endOne.getCoummunity() + ",";
                }
            }
        }
        stringListMap.put("eq", Arrays.asList(eqStr.split(",")));
        stringListMap.put("up", Arrays.asList(upStr.split(",")));
        stringListMap.put("down", Arrays.asList(downStr.split(",")));
        res.setDown(down);
        res.setEq(eq);
        res.setUp(up);
        res.setTotal(total);
        res.setListMap(stringListMap);
        return res;
    }
}
