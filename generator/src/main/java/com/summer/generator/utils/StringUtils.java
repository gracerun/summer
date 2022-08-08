package com.summer.generator.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 字符串工具类
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    /**
     * 根据分隔符截取字符串
     *
     * @param str   字符串
     * @param split 分隔符
     * @return String
     */
    public static String subBySplit(String str, String split) {
        String sub = null;
        if (!isEmpty(str) && str.lastIndexOf(split) > -1) {
            sub = str.substring(str.lastIndexOf(split) + 1);
        }
        return sub;
    }

    /**
     * 将字符串的首字母转换为大写
     *
     * @param str 字符串
     * @return String
     */
    public static String upperFirst(String str) {
        if (isEmpty(str)) {
            return null;
        }
        if (1 == str.length()) {
            return str.toUpperCase();
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * 将字符串的首字母转换为小写
     *
     * @param str 字符串
     * @return String
     */
    public static String lowerFirst(String str) {
        if (isEmpty(str)) {
            return null;
        }
        if (1 == str.length()) {
            return str.toLowerCase();
        }
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }


    /**
     * 将字符串拆分为list
     *
     * @param str   字符串
     * @param regex 表达式
     * @return List<String>
     */
    public static List<String> splitStr2List(String str, String regex) {
        List<String> list = new ArrayList<>();
        for (String s : str.split(regex)) {
            list.add(s.trim());
        }
        return list;
    }

    /**
     * 将数据库字段名，转为属性名 TODO Add comments here.
     *
     * @param fieldName
     * @return
     */
    public static String convertFieldName2PropName(String fieldName) {
        String propName = "";
        String[] strArray;
        if (fieldName.contains("_")) {
            strArray = fieldName.split("_");
        } else if (fieldName.contains("-")) {
            strArray = fieldName.split("-");
        } else {
            return fieldName;
        }

        for (int i = 0; i < strArray.length; i++) {
            if (i == 0) {
                propName = strArray[0];
            } else {
                propName += strArray[i].substring(0, 1).toUpperCase() + strArray[i].substring(1);
            }
        }
        return propName;
    }


    /**
     * 将数据库字段名转为类名
     *
     * @param fieldName 字段名
     * @return
     */
    public static String convertDbName2ClassName(String fieldName) {
        String className = "";
        String[] strArray = fieldName.split("_");
        for (int i = 0; i < strArray.length; i++) {
            className += strArray[i].substring(0, 1).toUpperCase() + strArray[i].substring(1).toLowerCase();
        }
        return className;
    }

    /**
     * 生成随机序列值
     *
     * @return
     */
    public static String generate16LongNum() {
        Random ran = new Random();
        int a = ran.nextInt(99999999);
        int b = ran.nextInt(99999999);
        long l = a * 100000000L + b;
        String num = String.valueOf(l) + "L";
        return num;
    }

    public static void main(String[] args) {
        System.out.println(subBySplit("BirthdayReminder\\生日名单.xls", "/"));
//		System.out.println(convertFieldName2PropName("bossServerMenu"));
        //for (int i = 0; i < 1000; i++) {
        //	System.out.println(generate16LongNum());
        //}

    }
}
