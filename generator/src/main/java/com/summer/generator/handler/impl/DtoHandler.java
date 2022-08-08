package com.summer.generator.handler.impl;


import com.summer.generator.Constants;
import com.summer.generator.handler.BaseHandler;
import com.summer.generator.model.DtoInfo;
import com.summer.generator.model.EntityInfo;
import com.summer.generator.config.Configuration;
import com.summer.generator.utils.StringUtils;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Dto-处理器
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
public class DtoHandler extends BaseHandler<DtoInfo> {

    public DtoHandler(String ftlName, DtoInfo info) {
        this.ftlName = ftlName;
        this.info = info;
        this.savePath = Configuration.getString("base.baseDir") + File.separator + Configuration.getString("dto.path")
                + File.separator + info.getClassName() + Constants.FILE_SUFFIX_JAVA;
    }

    @Override
    public void assembleParams(DtoInfo info) {
        EntityInfo entityInfo = info.getEntityInfo();
        this.param.put("packageStr", info.getPackageStr());
        StringBuilder sb = new StringBuilder();
        for (String str : entityInfo.getImports()) {
            sb.append("import ").append(str).append(";\r\n");
        }

        this.param.put("importStr", sb.toString());
        this.param.put("entityDesc", entityInfo.getEntityDesc());
        this.param.put("className", info.getClassName());
        this.param.put("entityImport",
                "import " + entityInfo.getEntityPackage() + "." + entityInfo.getEntityName() + ";\r\n");
        this.param.put("pojoClassName", entityInfo.getEntityName());
        this.param.put("lowerFirstPoClassName", StringUtils.lowerFirst(entityInfo.getEntityName()));
        // 生成属性，getter,setter方法
        sb = new StringBuilder();
        StringBuilder sbMethods = new StringBuilder();
        Map<String, String> propRemarks = entityInfo.getPropRemarks();
        for (Entry<String, String> entry : entityInfo.getPropTypes().entrySet()) {
            String propName = entry.getKey();
            String propType = entry.getValue();

            if ("optimistic".equals(propName)) {
                continue;
            }

            sb.append(System.lineSeparator());
            sb.append("    /**");
            sb.append(System.lineSeparator());
            sb.append("     * ");
            sb.append(propRemarks.get(propName));
            sb.append(System.lineSeparator());
            sb.append("     */");
            sb.append(System.lineSeparator());
            sb.append(String.format("    @ApiModelProperty(\"%s\")", propRemarks.get(propName)));
            sb.append(System.lineSeparator());
            sb.append("    private ");
            sb.append("QueryItem ").append(propName).append(";");
        }

        sb.append(System.lineSeparator());
        this.param.put("propertiesStr", sb.toString());
        this.param.put("methodStr", sbMethods.toString());
        this.param.put("serialVersionNum", StringUtils.generate16LongNum());
    }

}
