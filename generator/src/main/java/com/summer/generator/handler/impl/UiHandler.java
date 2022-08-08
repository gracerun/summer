package com.summer.generator.handler.impl;

import com.summer.generator.Constants;
import com.summer.generator.config.Configuration;
import com.summer.generator.handler.BaseHandler;
import com.summer.generator.model.EntityInfo;
import com.summer.generator.model.UiInfo;

import java.io.File;
import java.util.Map;
import java.util.Set;

/**
 * UI-处理器
 *
 * @author xiaojie.zhang
 * @version V1.0.0
 * @since 2017/3/6
 */
public class UiHandler extends BaseHandler<UiInfo> {


    public UiHandler(String ftlName, UiInfo info) {
        this.ftlName = ftlName;
        this.info = info;
        this.savePath = Configuration.getString("base.baseDir") + File.separator
                + Configuration.getString("ui.path") + File.separator + info.getLowerEntityName()
                + Constants.FILE_SUFFIX_VUE;
    }

    /**
     * 组装参数
     *
     * @param info ui信息
     */
    @Override
    public void assembleParams(UiInfo info) {

        EntityInfo entityInfo = info.getEntityInfo();
        info.setRequestMapping(entityInfo.getEntityName().substring(0, 1).toLowerCase() + entityInfo.getEntityName().substring(1, entityInfo.getEntityName().length()));

        Map<String, String> propRemarks = entityInfo.getPropRemarks();
        Map<String, String> propTypes = entityInfo.getPropTypes();
        Set<Map.Entry<String, String>> entries = propRemarks.entrySet();

        StringBuilder formRules = new StringBuilder();
        StringBuilder formEntityProps = new StringBuilder();
        StringBuilder searchElements = new StringBuilder();
        StringBuilder tables = new StringBuilder();
        StringBuilder searchElementInputs = new StringBuilder();
        StringBuilder addFormElementList = new StringBuilder();
        StringBuilder editFormElementList = new StringBuilder();
        boolean importDate = false;
        for (Map.Entry<String, String> entry : entries) {
            String remark = propRemarks.get(entry.getKey());
            String type = propTypes.get(entry.getKey());
            if ("optimistic".equals(entry.getKey()) || "id".equals(entry.getKey())) {
                continue;
            }

            if (!"createTime".equals(entry.getKey()) && !"lastUpdateTime".equals(entry.getKey())) {
                addFormElementList.append(String.format(UiInfo.formElement, remark, entry.getKey(), "addForm." + entry.getKey(), remark)).append(System.lineSeparator());
                editFormElementList.append(String.format(UiInfo.formElement, remark, entry.getKey(), "editForm." + entry.getKey(), remark)).append(System.lineSeparator());
                formRules.append(String.format(UiInfo.formRules, entry.getKey(), remark)).append(System.lineSeparator());
                formEntityProps.append(String.format(UiInfo.formEntityProps, entry.getKey(), remark)).append(System.lineSeparator());
            }

            String comparer = "EQU";
            if ("Date".equals(type)) {
                comparer = "INTERVAL";
            }
            if (entry.getKey().toLowerCase().indexOf("name") > -1) {
                comparer = "LIKE";
            }

            if (!"lastUpdateTime".equals(entry.getKey())) {
                if ("Date".equals(type)) {
                    importDate = true;
                    searchElements.append(String.format(UiInfo.searchDateElements, entry.getKey(), comparer, remark)).append(System.lineSeparator());
                } else {
                    searchElements.append(String.format(UiInfo.searchElements, entry.getKey(), comparer, remark)).append(System.lineSeparator());
                }

                if (entry.getKey().toLowerCase().indexOf("status") > -1) {
                    tables.append(String.format(UiInfo.eumnTables, remark, entry.getKey(), info.getLowerEntityName()));
                } else {
                    tables.append(String.format(UiInfo.tables, remark, entry.getKey()));
                }
                tables.append(System.lineSeparator());

                if ("Date".equals(type)) {
                    searchElementInputs.append(String.format(UiInfo.searchDate, remark, entry.getKey(), entry.getKey(), entry.getKey(), entry.getKey(), entry.getKey(), entry.getKey(), entry.getKey(), entry.getKey()));
                } else if (entry.getKey().toLowerCase().indexOf("status") > -1) {
                    searchElementInputs.append(String.format(UiInfo.searchStatus, entry.getKey(), remark, entry.getKey(), entry.getKey(), entry.getKey()));
                } else {
                    searchElementInputs.append(String.format(UiInfo.searchInput, entry.getKey(), remark, entry.getKey(), entry.getKey(), entry.getKey(), entry.getKey()));
                }
                searchElementInputs.append(System.lineSeparator());
            }
        }

        int lineLen = System.lineSeparator().length();
        int splitLen = 1;
        this.param.put("lowerEntityName", info.getLowerEntityName());
        this.param.put("entityName", info.getEntityInfo().getEntityName());
        this.param.put("entityDesc", info.getEntityInfo().getEntityDesc());
        this.param.put("formRules", formRules.length() > 0 ? formRules.substring(0, formRules.length() - lineLen - splitLen) : "");
        this.param.put("formEntityProps", formEntityProps.length() > 0 ? formEntityProps.substring(0, formEntityProps.length() - lineLen - splitLen) : "");
        this.param.put("searchElements", searchElements.length() > 0 ? searchElements.substring(0, searchElements.length() - lineLen - splitLen) : "");
        this.param.put("tables", tables.length() > 0 ? tables.substring(0, tables.length() - lineLen - splitLen) : "");
        this.param.put("searchElementInputs", searchElementInputs.length() > 0 ? searchElementInputs.substring(0, searchElementInputs.length() - lineLen - splitLen) : "");
        this.param.put("addFormElementList", addFormElementList.length() > 0 ? addFormElementList.substring(0, addFormElementList.length() - lineLen) : "");
        this.param.put("editFormElementList", editFormElementList.length() > 0 ? editFormElementList.substring(0, editFormElementList.length() - lineLen) : "");
        this.param.put("requestMapping", info.getRequestMapping());
        this.param.put("imports", importDate ? "import { Today } from \"@src/common/dateSerialize\";" : "");
    }

}
