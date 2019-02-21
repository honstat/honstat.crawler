package com.honstat.crawler.models.enums;


/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.model.enums
 * @Description: TODO
 * @date 2018/11/5 10:40
 */
public enum LookTradeAnaylsisType {
    District(1),Community(2),QueryTop(3),RealPriceAndSalePrice(4),Month(5),Style(6);

    private int value;

    private LookTradeAnaylsisType(int value) {
        this.value = value;
    }
    public int getValue(){
        return  value;
    }
}

