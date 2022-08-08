package com.summer.generator.handler.impl;

import com.summer.generator.Constants;
import com.summer.generator.handler.BaseHandler;
import com.summer.generator.model.EntityInfo;
import com.summer.generator.model.JspInfo;
import com.summer.generator.config.Configuration;
import com.summer.generator.utils.StringUtils;

import java.io.File;
import java.util.Map;

/**
 * Jsp-处理器
 *
 * @author xiaojie.zhang
 * @version V1.0.0
 * @since 2017/3/6
 */
public class JspHandler extends BaseHandler<JspInfo> {


    public JspHandler(String ftlName, JspInfo jspInfo) {
        this.ftlName = ftlName;
        this.info = jspInfo;
        this.savePath = Configuration.getString("base.baseDir") + File.separator
                + Configuration.getString("ui.path") + File.separator + info.getClassName()
                + Constants.FILE_SUFFIX_JS;
    }

    /**
     * 组装参数
     *
     * @param jspInfo jsp信息
     */
    @Override
    public void assembleParams(JspInfo jspInfo) {

        EntityInfo entityInfo = info.getEntityInfo();
        info.setRequestMapping(StringUtils.lowerFirst(entityInfo.getEntityName()));

        Map<String, String> propRemarks = entityInfo.getPropRemarks();
        Map<String, String> propTypes = entityInfo.getPropTypes();
        Map<String, String> dictList = entityInfo.getDicts();

        StringBuilder createStringBuilder = new StringBuilder();
        StringBuilder queryStringBuilder = new StringBuilder();
        StringBuilder descStringBuilder = new StringBuilder();
        StringBuilder infoStringBuilder = new StringBuilder();

        for (Map.Entry<String, String> entry : propTypes.entrySet()) {
            if ("optimistic".equals(entry.getKey()) || "version".equals(entry.getKey())) {
                continue;
            }

            if ("".equals(entry.getValue())){

            }

        }

        this.param.put("entityDesc", jspInfo.getEntityDesc());
        this.param.put("className", jspInfo.getClassName());
        this.param.put("lowerClassName", jspInfo.getRequestMapping());
        this.param.put("entityPropertyCreate", createStringBuilder.toString());
        this.param.put("entityPropertyQuery", queryStringBuilder.toString());
        this.param.put("entityPropertyDesc", descStringBuilder.toString());
        this.param.put("entityPropertyInfo", infoStringBuilder.toString());
    }
}
