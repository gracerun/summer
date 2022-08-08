package com.summer.generator;

import com.summer.generator.application.CodeMakerApplication;

/**
 * 程序入口
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
public class Main {

    public static final String[] TABLES = {
            "tourism_sys_user",
    };

    // 主函数
    public static void main(String[] args) {
        CodeMakerApplication.doCodeMaker(args);
    }

}
