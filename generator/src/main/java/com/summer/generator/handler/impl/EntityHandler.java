package com.summer.generator.handler.impl;

import com.summer.generator.Constants;
import com.summer.generator.handler.BaseHandler;
import com.summer.generator.config.Configuration;
import com.summer.generator.model.EntityInfo;
import com.summer.generator.utils.StringUtils;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 实体-处理器
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
public class EntityHandler extends BaseHandler<EntityInfo> {

    public EntityHandler(String ftlName, EntityInfo info) {
        this.ftlName = ftlName;
        this.info = info;
        this.savePath = Configuration.getString("base.baseDir") + File.separator
                + Configuration.getString("entity.path") + File.separator + info.getClassName()
                + Constants.FILE_SUFFIX_JAVA;

    }

    @Override
    public void assembleParams(EntityInfo entityInfo) {
        this.param.put("packageStr", entityInfo.getEntityPackage());
        this.param.put("enumsPackageStr", entityInfo.getEntityPackage().replace("po", "enums.*"));
        StringBuilder sb = new StringBuilder();
        for (String str : entityInfo.getImports()) {
            sb.append("import ").append(str).append(";\r\n");
        }
        this.param.put("importStr", sb.toString());
        this.param.put("entityDesc", entityInfo.getEntityDesc());
        this.param.put("className", entityInfo.getClassName());

        // 生成属性，getter,setter方法
        sb = new StringBuilder();
        Map<String, String> propRemarks = entityInfo.getPropRemarks();
        Map<String, Integer> propLength = entityInfo.getPropLength();
        Map<String, String> propNameColumnNames = entityInfo.getPropNameColumnNames();
        Map<String, String> dicts = entityInfo.getDicts();
        Map<String, Integer> precisionsMap = entityInfo.getPrecisions();
        Map<String, Boolean> nullAbleMap = entityInfo.getNullAbles();
        int charIndex = 65;
        int j = 65;
        Set<String> imports = new HashSet<>();
        for (Entry<String, String> entry : entityInfo.getPropTypes().entrySet()) {
            String propName = entry.getKey();
            String propType = entry.getValue();
            // 注释、类型、名称
//            if ("id".equalsIgnoreCase(propName)) {
//                sb.append("    @Id\r\n");
//                if (Configuration.getString("base.database").equals(Constants.DbType.oracle.name())) {
//                    sb.append("    @GeneratedValue(strategy = GenerationType.IDENTITY,generator = \"SELECT " + entityInfo.getTableName() + "_SEQ.NEXTVAL FROM DUAL\")\r\n");
//
//                } else {
//                    sb.append("    @GeneratedValue(generator = \"JDBC\")\r\n");
//                }
//            } else {
//                sb.append("    @Column(name = \"");
//                sb.append(propNameColumnNames.get(propName));
//                sb.append("\")\r\n");
//            }
//            sb.append("    @ApiModelProperty(value = \"");
//            sb.append(propRemarks.get(propName));
//
//            sb.append("\", dataType = \"" + propType);
//            sb.append("\"");
//            if ("id".equals(propName) || "optimistic".equals(propName) || "createTime".equals(propName)) {
//                sb.append(", hidden = true");
//            }
//            sb.append(")\r\n");
//
//            if ("createTime".equals(propName)) {
//                sb.append("    @MyBatisPluginPlus(isQueryCol = false, orderByClause = \"DESC\")").append("\r\n");
//            }


            if (!"id".equals(propName) && !"optimistic".equals(propName) && !"version".equals(propName) && !"createTime".equals(propName) && !"lastUpdateTime".equals(propName)) {
//                sb.append("    @ExcelField(name = \"")
//                        .append(propRemarks.get(propName));
//                if ("Date".equals(propType)) {
//                    sb.append("\", formatDate = \"yyyy-MM-dd HH:mm:ss");
//                }
//                if (StringUtils.isNotBlank(dicts.get(propName))) {
//                    sb.append("\", dict = \"" + dicts.get(propName));
//                }
//
//                /*列*/
//                char columnChar = (char) charIndex++;
//                String columnStr = "";
//                if (charIndex > 91) {
//                    columnStr = "A" + String.valueOf((char) (j++));
//                }
//                columnStr = StringUtils.isNotBlank(columnStr) ? columnStr : String.valueOf(columnChar);
//                sb.append("\", columnWidth = \"20")
//                        .append("\", column = \"" + columnStr + "\")\r\n");

            } else {
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
            sb.append("    @ColumnWidth(20)");
            sb.append(System.lineSeparator());
            sb.append(String.format("    @ExcelProperty(\"%s\")", propRemarks.get(propName)));
            sb.append(System.lineSeparator());
            if (nullAbleMap.get(propName)) {
                if ("String".equals(propType)) {
                    sb.append("    @NotBlank").append(System.lineSeparator());
                    imports.add("import javax.validation.constraints.NotBlank;");
                } else {
                    sb.append("    @NotNull").append(System.lineSeparator());
                    imports.add("import javax.validation.constraints.NotNull;");
                }
            }
            if ("String".equals(propType) && StringUtils.isBlank(dicts.get(propName))) {
                sb.append("    @Length(max = ")
                        .append(propLength.get(propName))
                        .append(", message = \"{" + entityInfo.getClassName().toLowerCase() + "." + propName + ".length" + "}\")\r\n");
            }
            if ("Date".equals(propType) && StringUtils.isBlank(dicts.get(propName))) {
                sb.append("    @JsonFormat(pattern=\"yyyy-MM-dd HH:mm:ss\",timezone = \"GMT+8\")");
                sb.append(System.lineSeparator());
                imports.add("import java.util.Date;");
                imports.add("import com.fasterxml.jackson.annotation.JsonFormat;");
            }
            if ("Integer".equals(propType) || "Long".equals(propType)) {
                sb.append("    @Digits(integer = " + propLength.get(propName) + ", fraction = 0)");
                sb.append(System.lineSeparator());
                imports.add("import javax.validation.constraints.Digits;");
            }
            if ("Double".equals(propType) || "BigDecimal".equals(propType)) {
                sb.append("    @Digits(integer = " + propLength.get(propName) + ", fraction = " + precisionsMap.get(propName) + ")");
                sb.append(System.lineSeparator());
                imports.add("import javax.validation.constraints.Digits;");
            }
            if ("JSONObject".equals(propType)) {
                imports.add("import com.alibaba.fastjson.JSONObject;");
            }
            sb.append("    private ");
            if (StringUtils.isNotBlank(dicts.get(propName))) {
                //字典类型用枚举
                sb.append(StringUtils.upperFirst(StringUtils.convertFieldName2PropName(dicts.get(propName).toLowerCase())));
            } else {
                sb.append(propType);
            }
            sb.append(" ")
                    .append(propName)
                    .append(";");
        }

        StringBuilder ims = new StringBuilder();
        for (String im : imports) {
            ims.append(im).append(System.lineSeparator());
        }
        this.param.put("imports", ims.length() == 0 ? "" : ims.substring(0, ims.length() - 1));
        this.param.put("propertiesStr", sb.toString());
        this.param.put("serialVersionNum", StringUtils.generate16LongNum());
        this.param.put("tableName", entityInfo.getTableName());
    }
}
