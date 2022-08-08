package com.summer.generator.task;

import com.summer.generator.Constants;
import com.summer.generator.config.Configuration;
import com.summer.generator.framework.AbstractApplicationTask;
import com.summer.generator.framework.context.ApplicationContext;
import com.summer.generator.handler.BaseHandler;
import com.summer.generator.handler.impl.ServiceHandler;
import com.summer.generator.model.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 业务层-任务
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
@Slf4j
public class ServiceTask extends AbstractApplicationTask {

    private static String SERVICE_FTL = "template/Service.ftl";

    @Override
    protected boolean doInternal(ApplicationContext context) throws Exception {
        log.info("开始生成service...");

        List<ServiceInfo> serviceInfos = (List<ServiceInfo>) context.getAttribute("serviceInfos");

//        BaseHandler<ServiceInfo> baseHandler;
//        for (ServiceInfo serviceInfo : serviceInfos) {
//            baseHandler = new ServiceHandler(SERVICE_FTL, serviceInfo);
//            baseHandler.execute(context);
//        }

        log.info("结束生成service...");
        return false;
    }

    @Override
    protected void doAfter(ApplicationContext context) throws Exception {
        super.doAfter(context);

        List<ServiceInfo> serviceInfoList = (List<ServiceInfo>) context.getAttribute("serviceInfos");
        List<EntityInfo> entityInfoList = (List<EntityInfo>) context.getAttribute("entityInfos");
        List<DaoInfo> daoInfoList = (List<DaoInfo>) context.getAttribute("daoList");
        List<VoInfo> voInfoList = (List<VoInfo>) context.getAttribute("voList");
        List<DtoInfo> dtoInfoList = (List<DtoInfo>) context.getAttribute("dtoList");

        List<ServiceImplInfo> serviceImplInfoList = new ArrayList<>();
        ServiceImplInfo serviceImplInfo;
        for (int i = 0; i < serviceInfoList.size(); i++) {
            ServiceInfo serviceInfo = serviceInfoList.get(i);
            EntityInfo entityInfo = entityInfoList.get(i);
            serviceImplInfo = new ServiceImplInfo();
            serviceImplInfo.setClassName(entityInfo.getEntityName() + Constants.SERVICE_IMPL_SUFFIX);
            serviceImplInfo.setEntityDesc(entityInfo.getEntityDesc());
            serviceImplInfo.setEntityName(entityInfo.getEntityName());
            serviceImplInfo.setLowerEntityName(
                    entityInfo.getEntityName().substring(0, 1).toLowerCase() + entityInfo.getEntityName().substring(1));
            serviceImplInfo.setPackageStr(Configuration.getString("serviceImpl.package"));
            serviceImplInfo
                    .setServiceType(serviceInfo.getPackageStr() + Constants.CHARACTER_POINT + serviceInfo.getClassName());
            serviceImplInfo.setVoType(voInfoList.get(i).getPackageStr() + "." + voInfoList.get(i).getClassName());
            serviceImplInfo.setDtoType(dtoInfoList.get(i).getPackageStr() + "." + dtoInfoList.get(i).getClassName());
            serviceImplInfo.setEntityPackage(serviceInfoList.get(i).getEntityPackage());
            serviceImplInfo.setDaoType(daoInfoList.get(i).getPackageStr() + "." + daoInfoList.get(i).getClassName());
            serviceImplInfo.setVoInfo(voInfoList.get(i));
            serviceImplInfo.setDtoInfo(dtoInfoList.get(i));
            serviceImplInfo.setServiceInfo(serviceInfo);

            serviceImplInfoList.add(serviceImplInfo);
        }
        context.setAttribute("serviceImplInfos", serviceImplInfoList);
    }

}
