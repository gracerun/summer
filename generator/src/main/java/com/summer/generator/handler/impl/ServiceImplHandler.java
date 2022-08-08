package com.summer.generator.handler.impl;

import com.summer.generator.Constants;
import com.summer.generator.model.ServiceImplInfo;
import com.summer.generator.config.Configuration;
import com.summer.generator.handler.BaseHandler;

import java.io.File;

/**
 * 业务实现-处理器
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
public class ServiceImplHandler extends BaseHandler<ServiceImplInfo> {

    public ServiceImplHandler(String ftlName, ServiceImplInfo info) {
        this.ftlName = ftlName;
        this.info = info;
        this.savePath = Configuration.getString("base.baseDir") + File.separator
                + Configuration.getString("serviceImpl.path") + File.separator + info.getClassName()
                + Constants.FILE_SUFFIX_JAVA;
    }

    @Override
    public void assembleParams(ServiceImplInfo info) {
        this.param.put("packageStr", info.getPackageStr());
        this.param.put("serviceType", info.getServiceType());
        String voType = info.getVoType();
        String utilProject = voType.substring(0,info.getPackageStr().lastIndexOf(".service"));
        utilProject = utilProject.substring(utilProject.lastIndexOf(".")+1);

        this.param.put("voType", voType);
        this.param.put("utilProject", utilProject);
        this.param.put("daoType", info.getDaoType());
        this.param.put("entityDesc", info.getEntityDesc());
        this.param.put("lowerEntityName", info.getLowerEntityName());
        this.param.put("entityName", info.getEntityName());
        this.param.put("entityPackage", info.getEntityPackage());
    }
}
