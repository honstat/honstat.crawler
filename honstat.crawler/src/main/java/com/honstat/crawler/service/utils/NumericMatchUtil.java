package com.honstat.crawler.service.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.utils
 * @Description: TODO
 * @date 2018/12/19 13:30
 */
public class NumericMatchUtil {
    final static Pattern p = Pattern.compile("^([0-9]+.\\d+)|(\\d+)$");
    final static Pattern p2 = Pattern.compile("([0-9]+.\\d+)|(\\d+)");
    final static Pattern pdouble=Pattern.compile("([0-9]+.\\d+)|(\\d+)");
    /**是否包含数字*/
    public static boolean hasNumeric(String content) {
        boolean flag = false;

        Matcher m = p.matcher(content);
        if (m.find()) {
            flag = true;
        }
        return flag;
    }
    /**截取字符串里面包含的数字（第一组）**/
    public static String getNumeric(String content){

        Matcher m = p2.matcher(content);
        boolean result = m.find();
        String find_result = null;
        if (result) {
            find_result = m.group(0);
        }
        return find_result;
    }
    /**截取字符串里面包含的数字（第一组）**/
    public static String getDoubleNumeric(String content){

        Matcher m = pdouble.matcher(content);
        boolean result = m.find();
        String find_result = null;
        if (result) {
            find_result = m.group(0);
        }
        return find_result;
    }
    /**截取字符串里面包含的数字（第一组）,自己传匹配方式**/
    public static String getNumeric(Pattern pattern,String content){

        Matcher m = pattern.matcher(content);
        boolean result = m.find();
        String find_result = null;
        if (result) {
            find_result = m.group(0);
        }
        return find_result;
    }
}
