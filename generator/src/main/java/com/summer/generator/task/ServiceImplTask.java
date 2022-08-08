package com.summer.generator.task;

import com.summer.generator.Constants;
import com.summer.generator.config.Configuration;
import com.summer.generator.framework.AbstractApplicationTask;
import com.summer.generator.framework.context.ApplicationContext;
import com.summer.generator.handler.BaseHandler;
import com.summer.generator.handler.impl.ServiceImplHandler;
import com.summer.generator.model.ControllerInfo;
import com.summer.generator.model.ServiceImplInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 业务实现-任务类
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
@Slf4j
public class ServiceImplTask extends AbstractApplicationTask {

    /**
     * 业务实现模板
     */
    private static String SERVICE_IMPL_FTL = "template/ServiceImpl.ftl";

    private List<ServiceImplInfo> serviceImplInfoList = new ArrayList<>();

    @Override
    protected boolean doInternal(ApplicationContext context) throws Exception {
        log.info("开始生成serviceImpl...");
        serviceImplInfoList = (List<ServiceImplInfo>) context.getAttribute("serviceImplInfos");

        BaseHandler<ServiceImplInfo> baseHandler;
        for (ServiceImplInfo info : serviceImplInfoList) {
            baseHandler = new ServiceImplHandler(SERVICE_IMPL_FTL, info);
            baseHandler.execute(context);
        }

        log.info("结束生成serviceImpl...");
        return false;
    }

    @Override
    protected void doAfter(ApplicationContext context) throws Exception {
        super.doAfter(context);
        List<ControllerInfo> controllerList = new ArrayList<>();
        // 组装Controller信息
        ControllerInfo controllerInfo;
        for (ServiceImplInfo serviceImplInfo : serviceImplInfoList) {
            controllerInfo = new ControllerInfo();
            controllerInfo.setClassName(serviceImplInfo.getEntityName() + Constants.CONTROLLER_SUFFIX);
            controllerInfo.setPackageStr(Configuration.getString("controller.package"));
            controllerInfo.setVoInfo(serviceImplInfo.getVoInfo());
            controllerInfo.setDtoInfo(serviceImplInfo.getDtoInfo());
            controllerInfo.setServiceInfo(serviceImplInfo.getServiceInfo());
            String entityName = serviceImplInfo.getEntityName();
            controllerInfo.setEntityName(entityName);
            controllerInfo.setRequestMapping(entityName.substring(0, 1).toLowerCase() + entityName.substring(1, entityName.length()));
            controllerInfo.setEntityPackage(serviceImplInfo.getEntityPackage());
            controllerInfo.setEntityDesc(serviceImplInfo.getEntityDesc());
            controllerList.add(controllerInfo);
        }

        context.setAttribute("controllerList", controllerList);
    }
}
