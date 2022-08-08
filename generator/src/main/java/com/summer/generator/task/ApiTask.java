package com.summer.generator.task;

import com.summer.generator.framework.AbstractApplicationTask;
import com.summer.generator.framework.context.ApplicationContext;
import com.summer.generator.handler.BaseHandler;
import com.summer.generator.handler.impl.ApiHandler;
import com.summer.generator.model.ApiInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * api-任务
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
@Slf4j
public class ApiTask extends AbstractApplicationTask {

    private static String UI_FTL = "template/api.ftl";

    private List<ApiInfo> apiInfoList;

    @Override
    protected boolean doInternal(ApplicationContext context) throws Exception {
        log.info("开始生成api");

        // 获取实体信息
        apiInfoList = (List<ApiInfo>) context.getAttribute("apiInfos");

        BaseHandler<ApiInfo> handler;
        for (ApiInfo apiInfo : apiInfoList) {
            handler = new ApiHandler(UI_FTL, apiInfo);
            handler.execute(context);
        }
        log.info("生成api完成");
        return false;
    }

    @Override
    protected void doAfter(ApplicationContext context) throws Exception {
        super.doAfter(context);
    }

}
