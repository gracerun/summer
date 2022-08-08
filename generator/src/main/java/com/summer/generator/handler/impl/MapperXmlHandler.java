package com.summer.generator.handler.impl;


import com.summer.generator.Constants;
import com.summer.generator.handler.BaseHandler;
import com.summer.generator.model.EntityInfo;
import com.summer.generator.model.MapperXmlInfo;
import com.summer.generator.config.Configuration;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * MapperXml-处理器
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
public class MapperXmlHandler extends BaseHandler<MapperXmlInfo> {

    public MapperXmlHandler(String ftlName, MapperXmlInfo info) {
        this.ftlName = ftlName;
        this.info = info;
        this.savePath = Configuration.getString("base.baseDir") + File.separator
                + Configuration.getString("mapperXml.path") + File.separator + info.getClassName()
                + Constants.FILE_SUFFIX_XML;

    }

    @Override
    public void assembleParams(MapperXmlInfo mapperXmlInfo) {
        EntityInfo entityInfo = mapperXmlInfo.getEntityInfo();
        Map<String, String> propNameColumnNames = entityInfo.getPropNameColumnNames();
        Map<String, String> propJdbcTypes = entityInfo.getPropJdbcTypes();
        //列
        List<String> columnList = new LinkedList<>();
        //属性
        List<String> propertyList = new LinkedList<>();
        //数据库对应类型
        List<String> jdbcTypeList = new LinkedList<>();

        Set<Entry<String, String>> entries = propNameColumnNames.entrySet();
        for (Entry<String, String> entry : entries) {
            String key = entry.getKey();
            propertyList.add(key);
            jdbcTypeList.add(propJdbcTypes.get(key));
            columnList.add(entry.getValue().toLowerCase());
        }

        String baseResultMap = "        <id column=\"" + columnList.get(0) + "\" property=\"" + propertyList.get(0)
                + "\" jdbcType=\"" + jdbcTypeList.get(0) + "\"/>\r\n";

        for (int i = 1; i < propertyList.size(); i++) {
            baseResultMap += "        <result column=\"" + columnList.get(i) + "\" property=\"" + propertyList.get(i)
                    + "\" jdbcType=\"" + jdbcTypeList.get(i) + "\"/>\r\n";
        }
        baseResultMap = baseResultMap.substring(0,baseResultMap.length()-2);
        this.param.put("namespace", mapperXmlInfo.getNameSpace());
        this.param.put("entityImport", entityInfo.getEntityPackage() + "." + entityInfo.getEntityName());
        this.param.put("BaseResultMap", baseResultMap);

    }

}

