package com.summer.generator.task;

import com.summer.generator.framework.AbstractApplicationTask;
import com.summer.generator.framework.context.ApplicationContext;
import com.summer.generator.handler.BaseHandler;
import com.summer.generator.handler.impl.ControllerHandler;
import com.summer.generator.model.ControllerInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 控制器-任务
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
@Slf4j
public class ControllerTask extends AbstractApplicationTask {

    private final static String CONTROLLER_FTL = "template/Controller.ftl";

    private static List<ControllerInfo> controllerInfoList;

    @Override
    protected boolean doInternal(ApplicationContext context) throws Exception {
        log.info("开始生成Controller...");
        controllerInfoList = (List<ControllerInfo>) context.getAttribute("controllerList");

        BaseHandler<ControllerInfo> baseHandler;
        for (ControllerInfo info : controllerInfoList) {
            baseHandler = new ControllerHandler(CONTROLLER_FTL, info);
            baseHandler.execute(context);
        }

        log.info("结束生成Controller...");
        return false;
    }
}
