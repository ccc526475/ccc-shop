package com.baidu.shop.utils;

/**
 * @ClassName StringUtil
 * @Description: TODO
 * @Author cuikangpu
 * @Date 2020/8/31
 * @Version V1.0
 **/
public class StringUtil {

    public static Boolean isNotEmpty(String str){

        return null != str && !"".equals(str);
    }

    public static Boolean isEmpty(String str){
        return null == str || "".equals(str);
    }

    public static Integer toInteger(String str){
        if (null != str && !"".equals(str)) return Integer.parseInt(str);
        return 0;
    }

}
