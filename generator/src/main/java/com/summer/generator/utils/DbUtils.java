package com.summer.generator.utils;

import com.summer.generator.config.Configuration;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.Properties;

/**
 * 数据库连接工具类
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
@Slf4j
public class DbUtils {

    /*
      加载驱动
     */
    static {
        String driverName = Configuration.getString("jdbc.driverName");
        if (StringUtils.isNotBlank(driverName)) {
            try {
                Class.forName(driverName);
            } catch (Exception e) {
                log.error("database driver:[{}], load failed:[{}]", driverName, e);
            }
        } else {
            log.error("database driver params is empty or is null");
        }
    }

    /**
     * 获取连接
     *
     * @return Connection
     */
    public static Connection getConn() {
        Connection conn = null;
        try {
            String jdbcUrl = Configuration.getString("jdbc.url");
            String userName = Configuration.getString("jdbc.username");
            String password = Configuration.getString("jdbc.password");
            Properties props = new Properties();
            props.put("remarksReporting", "true");
            props.put("user", userName);
            props.put("password", password);
            props.put("nullCatalogMeansCurrent", true);
            conn = DriverManager.getConnection(jdbcUrl, props);
        } catch (SQLException e) {
            log.error("database connection error, [{}]", e);
        }
        return conn;
    }

    /**
     * closeResource 关闭资源
     *
     * @param conn      连接
     * @param stat      Statement对象
     * @param resultSet 结果集
     */
    public static void closeResource(Connection conn, Statement stat, ResultSet resultSet) {
        try {
            if (conn != null) {
                conn.close();
            }
            if (stat != null) {
                stat.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
            log.info("close resource success");
        } catch (SQLException e) {
            log.error("close resource error, [{}]", e);
        }
    }

    /**
     * 获取数据库的库名
     *
     * @return String Schema概要
     */
    public static String getSchema() {
        String jdbcUrl = Configuration.getString("jdbc.url");
        if (jdbcUrl.indexOf("postgresql") > -1) {
            return jdbcUrl.substring(jdbcUrl.lastIndexOf("/") + 1);
        }
        return jdbcUrl.substring(0, jdbcUrl.lastIndexOf("?")).substring(jdbcUrl.lastIndexOf("/") + 1);
    }

    public static void main(String[] args) {
        System.out.println(getSchema());
    }
}
