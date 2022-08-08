package com.summer.generator.task;

import com.summer.generator.Constants;
import com.summer.generator.Main;
import com.summer.generator.config.Configuration;
import com.summer.generator.framework.AbstractApplicationTask;
import com.summer.generator.framework.context.ApplicationContext;
import com.summer.generator.model.ColumnInfo;
import com.summer.generator.model.TableInfo;
import com.summer.generator.utils.DbUtils;
import com.summer.generator.utils.FileHelper;
import com.summer.generator.utils.PropertyUtils;
import com.summer.generator.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

/**
 * 初始化-任务
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
@Slf4j
public class InitTask extends AbstractApplicationTask {

    @Override
    protected boolean doInternal(ApplicationContext context) throws Exception {
        log.info("初始化任务");

        //首先清空baseDir下的所有文件 位配合mybatis的逆向工程,不清除文件
        String baseDir = Configuration.getString("base.baseDir");
        FileHelper.deleteDirectory(baseDir);

        // 加载属性文件
        // 字段类型与属性类型的映射
        switch (Constants.DbType.valueOf(Configuration.getString("base.database"))){
            case mysql:
                PropertyUtils.loadProp("columnType2PropType.properties");
                break;
            case oracle:
                PropertyUtils.loadProp("columnType2PropType_oracle.properties");
                break;
            case postgresql:
                PropertyUtils.loadProp("columnType2PropType_postgresql.properties");
                break;
        }
        // 属性类型与包类名的映射
        PropertyUtils.loadProp("propType2Package.properties");
        // 属性类型与jdbc类型的映射，注意这里为了防止与上面冲突，属性类型前加了_
        PropertyUtils.loadProp("propType2JdbcType.properties");
        // 获取所有需要生成的表名
        List<String> tableList = Arrays.asList(Main.TABLES);
        log.info("需要生成的表：{}", tableList);

        // 对应的实体名
        List<String> entityNames = new ArrayList<>();
        for (String tableName : tableList) {
            entityNames.add(StringUtils.convertDbName2ClassName(tableName));
        }

//        List<String> entityDescList = StringUtils.splitStr2List(Configuration.getString("base.entityDescs"),
//                Constants.CHARACTER_COMMA);

        List<String> entityDescList = new ArrayList<>();
        // 添加映射关系
        Map<String, String> table2Entity = new HashMap<>(20);
        for (int i = 0; i < tableList.size(); i++) {
            table2Entity.put(tableList.get(i), entityNames.get(i));
        }

        // 连接数据库
        Connection conn = null;
        ResultSet tableRS = null;
        ResultSet columnRS = null;
        DatabaseMetaData dbMetaData;
        String schemaPattern;
        try {
            conn = DbUtils.getConn();
            dbMetaData = conn.getMetaData();
            if (Configuration.getString("base.database").equals(Constants.DbType.oracle.name())) {
                schemaPattern = Configuration.getString("jdbc.username").toUpperCase();
            } else {
                schemaPattern = Configuration.getString("base.schemaPattern");
            }

            // 获取表的结果集
            if (Configuration.getString("base.database").equals(Constants.DbType.oracle.name())) {
                tableRS = dbMetaData.getTables(null, schemaPattern, Constants.PERCENT, new String[]{"TABLE"});
            } else {
                tableRS = dbMetaData.getTables(null, schemaPattern, Constants.PERCENT, new String[]{"TABLE"});
                if (tableRS.getRow() == 0) {
                    tableRS = dbMetaData.getTables(null, schemaPattern, Constants.PERCENT, new String[]{"TABLE"});
                }
            }

            // 遍历
            Map<String, TableInfo> tableMap = new HashMap<>();
            while (tableRS.next()) {
                // 表名
                String tableName = tableRS.getString("TABLE_NAME").toUpperCase();
                log.info("扫描表名：{}", tableName);
                if (tableList.contains(tableName.toUpperCase()) || tableList.contains(tableName.toLowerCase())) {
                    log.info("数据库表名：{}", tableName);
                    TableInfo tableInfo = new TableInfo();
                    tableInfo.setName(tableName);
                    log.info("*****************************");
                    log.info("tableName:{}", tableName);
                    StringBuilder builder;
                    if (Configuration.getString("base.database").equals(Constants.DbType.oracle.name())) {
                        builder = new StringBuilder("SELECT COMMENTS AS TABLE_COMMENT FROM USER_TAB_COMMENTS WHERE 1=1");
                        builder.append(" and table_name = '").append(tableName.toUpperCase()).append("'");
                    } else if(Configuration.getString("base.database").equals(Constants.DbType.mysql.name())) {
                        builder = new StringBuilder("SELECT TABLE_COMMENT FROM information_schema.TABLES WHERE table_schema='");
                        builder.append(DbUtils.getSchema()).append("' and table_name = '").append(tableName).append("'");
                    } else {
                        builder = new StringBuilder("select col_description(c.oid, 0) as TABLE_COMMENT from pg_class c where relkind = 'r'");
                        builder.append(" and relname = '").append(tableName.toLowerCase()).append("'");
                    }

                    Statement statement = conn.createStatement();
                    ResultSet resultSet = statement.executeQuery(builder.toString());
                    while (resultSet.next()) {
                        String tableComment = resultSet.getString("TABLE_COMMENT");
                        // 表注释
                        log.info("表{}的注释:{}", tableName, tableComment);
                        entityDescList.add(tableComment);
                        tableInfo.setRemark(tableComment);
                    }
                    statement.close();
                    // 表类型
                    String tableType = tableRS.getString("TABLE_TYPE");
                    tableInfo.setType(tableType);
                    log.info("表{}的类型:{}", tableName, tableType);

                    // 字段
                    // 获取列的结果集
                    if (Configuration.getString("base.database").equals(Constants.DbType.oracle.name())) {
                        columnRS = dbMetaData.getColumns(null, schemaPattern, tableName.toUpperCase(), Constants.PERCENT);
                    } else {
                        columnRS = dbMetaData.getColumns(null, schemaPattern, tableName.toLowerCase(), Constants.EMPTY_STR);
                        if (columnRS.getFetchSize() == 0){
                            columnRS = dbMetaData.getColumns(null, schemaPattern, tableName.toUpperCase(), Constants.PERCENT);
                        }
                    }
                    List<ColumnInfo> columnList = new ArrayList<>();
                    while (columnRS.next()) {
                        String columnName = columnRS.getString("COLUMN_NAME").toLowerCase();
                        String columnType = columnRS.getString("TYPE_NAME").toLowerCase();
                        String columnRemark = columnRS.getString("REMARKS");
                        log.info("字段名称：{}, 字段类型：{}, 字段注释：{}", columnName, columnType, columnRemark);

                        int len = columnRS.getInt("COLUMN_SIZE");
                        //log.info("字段长度：{}", len);

                        int precision = columnRS.getInt("DECIMAL_DIGITS");
                        //log.info("字段类型精度：{}", precision);

                        if (StringUtils.isBlank(columnName)) {
                            continue;
                        }

                        int nullAble = columnRS.getInt("NULLABLE");

                        ColumnInfo ci = new ColumnInfo();
                        ci.setName(columnName);
                        ci.setType(columnType);
                        ci.setRemark(columnRemark);
                        ci.setLen(len);
                        ci.setPrecision(precision);
                        ci.setNullAble(nullAble == 0 ? true : false);
                        columnList.add(ci);
                    }
                    log.info("*****************************");
                    tableInfo.setColumnList(columnList);
                    tableMap.put(tableName, tableInfo);
                }
            }

            // 实体对应的描述
            Map<String, String> entity2Desc = new HashMap<>(20);
            if (entityNames.size() == 0 || entityDescList.size() == 0) {
                log.info("在数据库没有匹配到相应的表");
                return true;
            }
            for (int i = 0; i < entityNames.size(); i++) {
                entity2Desc.put(entityNames.get(i), entityDescList.get(i));
            }
            // 放入上下文
            context.setAttribute("tableInfos", tableMap);
            context.setAttribute("tableName.to.entityName", table2Entity);
            context.setAttribute("entityName.to.desc", entity2Desc);

            if (tableMap.size() == 0) {
                log.info("在数据库没有匹配到相应的表");
                return true;
            }
        } catch (Exception e) {
            log.info("初始化任务异常", e);

        } finally {
            DbUtils.closeResource(conn, null, tableRS);
            DbUtils.closeResource(null, null, columnRS);
        }
        return false;
    }

}
