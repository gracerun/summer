package com.summer.generator.task;

import com.summer.generator.Constants;
import com.summer.generator.config.Configuration;
import com.summer.generator.framework.AbstractApplicationTask;
import com.summer.generator.framework.context.ApplicationContext;
import com.summer.generator.model.ColumnInfo;
import com.summer.generator.model.EntityInfo;
import com.summer.generator.model.TableInfo;
import com.summer.generator.utils.PropertyUtils;
import com.summer.generator.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.Map.Entry;

/**
 * 组装信息-任务
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
@Slf4j
public class CombineInfoTask extends AbstractApplicationTask {

    @Override
    protected boolean doInternal(ApplicationContext context) throws Exception {
        log.info("组装信息");
        // 获取实体相关的配置
        String packageName = Configuration.getString("entity.package");
        // 存放路径
        String path = Configuration.getString("entity.path");
        log.info("所有实体的包名为{}， 路径为：{}", packageName, path);
        // 获取表和实体的映射集合
        Map<String, String> table2Entities = (Map<String, String>) context.getAttribute("tableName.to.entityName");
        Map<String, String> entity2Desc = (Map<String, String>) context.getAttribute("entityName.to.desc");
        Map<String, TableInfo> tableInfoMap = (Map<String, TableInfo>) context.getAttribute("tableInfos");

        List<EntityInfo> entityInfoList = new ArrayList<>();
        for (Entry<String, String> entry : table2Entities.entrySet()) {
            EntityInfo entityInfo = new EntityInfo();
            // 表名
            String tableName = entry.getKey();
            // 实体名
            String entityName = entry.getValue();
            // 表信息
            TableInfo tableInfo = tableInfoMap.get(tableName.toUpperCase());
            Set<String> imports = new HashSet<>();
            Map<String, String> propTypes = new LinkedHashMap<>();
            Map<String, String> propRemarks = new LinkedHashMap<>();
            Map<String, String> propJdbcTypes = new LinkedHashMap<>();
            Map<String, String> propName2ColumnNames = new LinkedHashMap<>();
            Map<String, Integer> propLength = new LinkedHashMap<>();
            Map<String, String> dictMap = new LinkedHashMap<>();
            Map<String, Integer> precisionsMap = new LinkedHashMap<>();
            Map<String, Boolean> nullAbleMap = new LinkedHashMap<>();

            entityInfo.setTableName(tableName);
            entityInfo.setEntityName(entityName);
            entityInfo.setEntityDesc(entity2Desc.get(entityName));
            entityInfo.setClassName(entityName + Constants.ENTITY_SUFFIX);
            entityInfo.setEntityPackage(packageName);

            if (null == tableInfo) {
                continue;
            }
            // 遍历表字段信息
            List<ColumnInfo> columns = tableInfo.getColumnList();
            for (ColumnInfo columnInfo : columns) {
                String fieldName = columnInfo.getName();
                String fieldType = columnInfo.getType();

                // 通过字段名生成属性名
                String propName = StringUtils.convertFieldName2PropName(fieldName);

                // 这里为了兼容oracle，number类型，如果小数精度为0，则映射成Long类型
                String propType;
                if (Constants.DBTYPE_NUMBER.equals(fieldType) && columnInfo.getPrecision() == 0) {
                    if (columnInfo.getLen() == 38) {
                        propType = Constants.PROPTYPE_INTEGER;
                    } else {
                        propType = Constants.PROPTYPE_LONG;
                    }
                } else if (Constants.DBTYPE_NUMBER.equals(fieldType) && columnInfo.getPrecision() > 0) {
                    propType = Constants.PROPTYPE_DOUBLE;
                } else {
                    propType = PropertyUtils.getValueByKey(fieldType);
                }
                propLength.put(propName, columnInfo.getLen());
                precisionsMap.put(propName, columnInfo.getPrecision());
                propTypes.put(propName, propType);
                if (null != columnInfo.getRemark() && columnInfo.getRemark().contains("[") && columnInfo.getRemark().contains("]")) {
                    columnInfo.setDict(columnInfo.getRemark().substring(columnInfo.getRemark().lastIndexOf("[") + 1, columnInfo.getRemark().lastIndexOf("]")));
                    columnInfo.setRemark(columnInfo.getRemark().substring(0, columnInfo.getRemark().lastIndexOf("[")));
                    dictMap.put(propName, columnInfo.getDict());
                }
                propRemarks.put(propName, columnInfo.getRemark());
//                propJdbcTypes.put(propName, PropertyUtils.getValueByKey(Constant.CHARACTER_BOTTOM_LINE + propType));
                propJdbcTypes.put(propName, PropertyUtils.getValueByKey(propType));
                propName2ColumnNames.put(propName, columnInfo.getName().toUpperCase());
                nullAbleMap.put(propName, columnInfo.isNullAble());
            }
            log.info("属性类型：{}", propTypes);
            log.info("属性jdbcTypes：{}", propJdbcTypes);
            // 获取此实体所有的类型
            Collection<String> types = propTypes.values();

            for (String type : types) {
                if (StringUtils.isNotBlank(PropertyUtils.getValueByKey(type))) {
                    imports.add(PropertyUtils.getValueByKey(type));
                }
            }
            log.info("imports:{}", imports);
            entityInfo.setPropTypes(propTypes);
            entityInfo.setPropRemarks(propRemarks);
            entityInfo.setPropJdbcTypes(propJdbcTypes);
            entityInfo.setPropNameColumnNames(propName2ColumnNames);
            entityInfo.setImports(imports);
            entityInfo.setPackageClassName(entityInfo.getEntityPackage() + Constants.CHARACTER_POINT + entityInfo.getClassName());
            entityInfo.setEntityDesc(tableInfo.getRemark());
            entityInfo.setPropLength(propLength);
            entityInfo.setDicts(dictMap);
            entityInfo.setPrecisions(precisionsMap);
            entityInfo.setNullAbles(nullAbleMap);
            entityInfoList.add(entityInfo);
        }

        context.setAttribute("entityInfos", entityInfoList);
        return false;
    }

    public static void main(String[] args) {
        Map<String, Object> map1 = new HashMap<>();
        List<Long> list = new ArrayList<>();
        list.add(1L);
        map1.put("list", list);

        List<Long> list2 = (List<Long>) map1.get("list");
        list2.add(2L);

        System.out.println("list:" + list);
        System.out.println("list:" + map1.get("list"));
    }

}
