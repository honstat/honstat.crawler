package com.honstat.house.utils;

import java.util.UUID;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.utils
 * @Description: TODO
 * @date 2018/12/27 10:04
 */
public class UuidUtil {
    public static String getUUID32(){
        String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        return uuid;
    }

}
