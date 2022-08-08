package com.summer.generator.application;


import com.summer.generator.framework.Application;
import com.summer.generator.task.*;

/**
 * 代码模板-程序入口
 *
 * @author xiaojie.zhang
 * @version V1.0.0
 * @since 2017/3/6
 */
public class CodeMakerApplication {

    public static void doCodeMaker(String[] args) {
        // service 等交给模板生成
        Application application = new Application(CodeMakerApplication.class.getSimpleName());
        application.parseArgs(args);
        application.setApplicationName(CodeMakerApplication.class.getName());
        application.addApplicationTask(InitTask.class) // 获取数据库表以及字段相关信息
                .addApplicationTask(CombineInfoTask.class) // 基本信息封装
                .addApplicationTask(EntityTask.class) // 生成Entity
                .addApplicationTask(MapperTask.class) // 生成Dao
                .addApplicationTask(MapperXmlTask.class) // 生成Mapper.xml
                .addApplicationTask(VoTask.class) // 生成Vo
                .addApplicationTask(DtoTask.class) // 生成Dto
                .addApplicationTask(ServiceTask.class) // 生成Service
                .addApplicationTask(ServiceImplTask.class) // 生成ServiceImpl
                .addApplicationTask(ControllerTask.class) // 生成Controller
                .addApplicationTask(UiTask.class) // 生成ui
                .addApplicationTask(ApiTask.class) // 生产api
//                .addApplicationTask(FileTask.class) // 移动文件
                .work();
    }
}
