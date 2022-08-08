package com.summer.generator.handler.impl;


import com.summer.generator.Constants;
import com.summer.generator.handler.BaseHandler;
import com.summer.generator.config.Configuration;
import com.summer.generator.model.ServiceInfo;

import java.io.File;

/**
 * 业务-处理器
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
public class ServiceHandler extends BaseHandler<ServiceInfo> {

    public ServiceHandler(String ftlName, ServiceInfo info) {
        this.info = info;
        this.ftlName = ftlName;
        this.savePath = Configuration.getString("base.baseDir") + File.separator
                + Configuration.getString("service.path") + File.separator + info.getClassName()
                + Constants.FILE_SUFFIX_JAVA;
    }

    @Override
    public void assembleParams(ServiceInfo info) {
        this.param.put("packageStr", info.getPackageStr());
        this.param.put("entityDesc", info.getEntityDesc());
        this.param.put("className", info.getClassName());
        this.param.put("entityName", info.getEntityName());
        this.param.put("entityPackage", info.getEntityPackage());
        this.param.put("voName", info.getVoInfo().getClassName());
        this.param.put("voPackage", info.getVoInfo().getPackageStr());
    }

}
