package com.summer.generator.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
public class DateUtils {

    /**
     * 年月日 日期格式
     */
    public static String PATTERN_YMD = "yyyy-MM-dd";
    /**
     * 月日 日期格式
     */
    public static String PATTERN_MD = "MM-dd";

    /**
     * 字符串转日期 yyyy-MM-dd
     *
     * @param str 日期字符串
     * @return Date 日期
     */
    public static Date strFormatYMDDate(String str) {
        SimpleDateFormat sdf = new SimpleDateFormat(PATTERN_YMD);
        try {
            return sdf.parse(str);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 字符串转日期 MM-dd
     *
     * @param str 日期字符串
     * @return Date 日期
     */
    public static Date strFormatMDDate(String str) {
        SimpleDateFormat sdf = new SimpleDateFormat(PATTERN_MD);
        try {
            return sdf.parse(str);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 日期格式化字符串 yyyy-MM-dd
     *
     * @param date 日期
     * @return String 日期字符串
     */
    public static String dateFormatYMDStr(Date date) {
        return new SimpleDateFormat(PATTERN_YMD).format(date);
    }

    /**
     * 计算时间差 单位秒s
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return long 时间差 单位秒s
     */
    public static long calExecuteSecondTime(Date startTime, Date endTime) {
        return (endTime.getTime() - startTime.getTime()) / 1000;
    }

    /**
     * 计算时间差 单位毫秒ms
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return long 时间差 单位毫秒ms
     */
    public static long calExecuteMilliTime(Date startTime, Date endTime) {
        return endTime.getTime() - startTime.getTime();
    }

    /**
     * 获取当前日期 中文 “星期几“
     *
     * @return String 星期几
     */
    public static String getTodayWeek() {
        switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            case 1:
                return "星期天";
            case 2:
                return "星期一";
            case 3:
                return "星期二";
            case 4:
                return "星期三";
            case 5:
                return "星期四";
            case 6:
                return "星期五";
            case 7:
                return "星期六";
            default:
                return "日期异常";
        }
    }

    /**
     * 日期按指定pattern转为字符串
     *
     * @param date    待格式化日期
     * @param pattern 格式
     * @return String 格式化日期字符串
     */
    public static String formatDataToStr(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    public static void main(String[] args) {
        System.out.println(getTodayWeek());
    }
}
