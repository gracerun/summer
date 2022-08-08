package com.summer.generator.utils;

import java.text.MessageFormat;

/**
 * 系统输出类
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
public class SysOutUtils {

    public static void printInfo(String str, Object... objects) {
        System.out.println(MessageFormat.format(str, objects));
    }
}
