package com.summer.generator.framework;


import com.summer.generator.framework.context.ApplicationContext;

/**
 * 应用程序任务接口
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
public interface ApplicationTask extends SkipAble {

    boolean perform(ApplicationContext context) throws Exception;

    boolean hasNext();

    void registerNextTask(ApplicationTask nextTask);

    ApplicationTask next();

    //void initLogger(String applicationTaskId, String applicationId);
}
