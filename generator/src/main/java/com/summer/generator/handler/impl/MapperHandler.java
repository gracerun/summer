package com.summer.generator.handler.impl;


import com.summer.generator.Constants;
import com.summer.generator.handler.BaseHandler;
import com.summer.generator.config.Configuration;
import com.summer.generator.model.DaoInfo;

import java.io.File;

/**
 * Dao-处理器
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
public class MapperHandler extends BaseHandler<DaoInfo> {

    public MapperHandler(String ftlName, DaoInfo info) {
        this.ftlName = ftlName;
        this.info = info;
        this.savePath = Configuration.getString("base.baseDir") + File.separator
                + Configuration.getString("mapper.path") + File.separator + info.getClassName()
                + Constants.FILE_SUFFIX_JAVA;

    }

    @Override
    public void assembleParams(DaoInfo daoInfo) {
        this.param.put("packageStr", daoInfo.getPackageStr());
        this.param.put("className", daoInfo.getClassName());
        this.param.put("entityClass", daoInfo.getEntityInfo().getClassName());
        this.param.put("entityPackage", daoInfo.getEntityInfo().getEntityPackage());
        this.param.put("voClass", daoInfo.getVoInfo().getClassName());
        this.param.put("coPackage", daoInfo.getVoInfo().getPackageStr());
        this.param.put("entityDesc", daoInfo.getEntityInfo().getEntityDesc());
    }
}
