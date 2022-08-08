package com.summer.generator.utils;

import com.summer.generator.Constants;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Properties;

/**
 * 属性工具类
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
@Slf4j
public class PropertyUtils {

    /**
     * 属性类
     */
    public static Properties properties = new Properties();

    /**
     * 加载属性文件
     *
     * @param fileName 文件名
     */
    public static void loadProp(String fileName) {
        Reader reader = null;

        // 首先在classpath中找，如果找不到，则在工作目录下找
        InputStream is = PropertyUtils.class.getClassLoader().getResourceAsStream(fileName);
        log.info("在classpath下找{},是否找到：{}", fileName, is == null ? "否" : "是");
        if (is == null) {
            // 没找到，则在使用绝对路径，找到jar所在的根目录
            // String rootPath = System.getProperty("user.dir");
            String rootPath = System.getProperty("logDirPath");
            log.info("rootPath:{}", rootPath);

            // 截取一下文件名：BirthdayReminder/生日名单.xls ->
            if (StringUtils.subBySplit(fileName, "/") != null) {
                fileName = StringUtils.subBySplit(fileName, "/");
            } else if (StringUtils.subBySplit(fileName, "\\") != null) {
                fileName = StringUtils.subBySplit(fileName, "\\");
            }

            try {
                is = new FileInputStream(rootPath + File.separator + fileName);
                reader = new InputStreamReader(is, "UTF-8");
                log.info("在classpath下没找到，在用户工作目录下找{},是否找到：{}", fileName, is == null ? "否" : "是");
            } catch (FileNotFoundException e) {
                log.info("获取输入流异常:{}", e);
            } catch (UnsupportedEncodingException e) {
                log.info("不支持的编码异常:{}", e);
            }
        } else {
            try {
                reader = new InputStreamReader(is, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.info("不支持的编码异常:{}", e);
            }
        }

        try {
            properties.load(reader);
            log.info("加载属性文件成功：{}", fileName);
        } catch (IOException e) {
            log.info("加载属性文件异常：{}", fileName);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.info("输入流关闭异常:{}", e);
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {

                    log.info("字符流关闭异常:{}", e);
                }
            }
        }
    }

    /**
     * 加载属性文件
     *
     * @param fileName 文件名
     */
    public static void loadPropWithoutLog(String fileName) {
        Reader reader = null;

        // 首先在classpath中找，如果找不到，则在工作目录下找
        InputStream is = PropertyUtils.class.getClassLoader().getResourceAsStream(fileName);
        SysOutUtils.printInfo("在classpath下找{0},是否找到：{1}", fileName, is == null ? "否" : "是");
        if (is == null) {
            // 没找到，则在使用绝对路径，找到jar所在的根目录
            // String rootPath = System.getProperty("user.dir");
            String rootPath = System.getProperty("logDirPath");
            SysOutUtils.printInfo("rootPath:{0}", rootPath);

            // 截取一下文件名：BirthdayReminder/生日名单.xls ->
            if (StringUtils.subBySplit(fileName, "/") != null) {
                fileName = StringUtils.subBySplit(fileName, "/");
            } else if (StringUtils.subBySplit(fileName, "\\") != null) {
                fileName = StringUtils.subBySplit(fileName, "\\");
            }

            try {
                is = new FileInputStream(rootPath + File.separator + fileName);
                reader = new InputStreamReader(is, "UTF-8");
                SysOutUtils.printInfo("在classpath下没找到，在用户工作目录下找{0},是否找到：{1}", fileName, is == null ? "否" : "是");
            } catch (FileNotFoundException e) {
                SysOutUtils.printInfo("获取输入流异常:{0}", e);
            } catch (UnsupportedEncodingException e) {
                SysOutUtils.printInfo("不支持的编码异常:{0}", e);
            }
        } else {
            try {
                reader = new InputStreamReader(is, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                SysOutUtils.printInfo("不支持的编码异常:{0}", e);
            }
        }

        try {
            properties.load(reader);
            SysOutUtils.printInfo("加载属性文件成功：{0}", fileName);
        } catch (IOException e) {
            SysOutUtils.printInfo("加载属性文件异常：{0}", fileName);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    SysOutUtils.printInfo("输入流关闭异常:{0}", e);
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    SysOutUtils.printInfo("字符流关闭异常:{0}", e);
                }
            }
        }
    }

    /**
     * 根据文件名获取输入流
     *
     * @param fileName 文件名
     * @return InputStream 输入流InputStream
     */
    public static InputStream getInputStreamByFileName(String fileName) {
        // 首先在classpath中找，如果找不到，则在工作目录下找
        InputStream is = PropertyUtils.class.getClassLoader().getResourceAsStream(fileName);
        log.info("在classpath下找{},是否找到：{}", fileName, is == null ? "否" : "是");

        if (is == null) {
            // 没找到，则在使用绝对路径，找到jar所在的根目录
            // String rootPath = System.getProperty("user.dir");
            String rootPath = System.getProperty("logDirPath");
            log.info("rootPath:{}", rootPath);

            // 截取一下文件名：BirthdayReminder/生日名单.xls ->
            if (StringUtils.subBySplit(fileName, "/") != null) {
                fileName = StringUtils.subBySplit(fileName, "/");
            } else if (StringUtils.subBySplit(fileName, "\\") != null) {
                fileName = StringUtils.subBySplit(fileName, "\\");
            }

            try {
                is = new FileInputStream(rootPath + File.separator + fileName);
                log.info("在classpath下没找到，在用户工作目录下找{},是否找到：{}", fileName, is == null ? "否" : "是");
            } catch (FileNotFoundException e) {
                log.info("获取输入流异常");
            }
        }
        return is;
    }

    /**
     * 根据文件名获取输入流
     *
     * @param fileName 文件名
     * @return Reader 输入流Reader
     */
    public static Reader getReaderByFileName(String fileName) {
        Reader reader = null;
        // 首先在classpath中找，如果找不到，则在工作目录下找
        InputStream is = PropertyUtils.class.getClassLoader().getResourceAsStream(fileName);
        log.info("在classpath下找{},是否找到：{}", fileName, is == null ? "否" : "是");

        if (is == null) {
            // 没找到，则在使用绝对路径，找到jar所在的根目录
            // String rootPath = System.getProperty("user.dir");
            String rootPath = System.getProperty("logDirPath");
            log.info("rootPath:{}", rootPath);

            // 截取一下文件名：BirthdayReminder/生日名单.xls ->
            if (StringUtils.subBySplit(fileName, "/") != null) {
                fileName = StringUtils.subBySplit(fileName, "/");
            } else if (StringUtils.subBySplit(fileName, "\\") != null) {
                fileName = StringUtils.subBySplit(fileName, "\\");
            }

            try {
                is = new FileInputStream(rootPath + File.separator + fileName);
                reader = new InputStreamReader(is, "UTF-8");
                log.info("在classpath下没找到，在用户工作目录下找{},是否找到：{}", fileName, is == null ? "否" : "是");
            } catch (FileNotFoundException e) {
                log.error("获取输入流异常:[{}]", e);
            } catch (UnsupportedEncodingException e) {
                log.error("unSupport encode exception : [{}]", e);
            }
        }
        return reader;
    }

    /**
     * 通过key取对应的值
     *
     * @param key 键
     * @return String key对应的值
     */
    public static String getValueByKey(String key) {
        String value = Constants.EMPTY_STR;
        if (key != null && key.length() > 0) {
            try {
                // 首先在系统属性中获取，如果没有，则在配置中获取
                if (!StringUtils.isEmpty(System.getProperty(key))) {
                    value = System.getProperty(key);
                    // log.info("在系统属性中获取{}的value为：{}", key, value);
                } else {
                    value = properties.getProperty(key);
                    // log.info("在配置文件中获取{}的value为：{}", key, value);
                }
            } catch (Exception e) {

            }
        }
        return value;
    }

    /**
     * 通过key取对应的值
     *
     * @param key 键
     * @return String key对应的值
     */
    public static String getValueByKeyWithoutLog(String key) {
        String value = Constants.EMPTY_STR;
        if (key != null && key.length() > 0) {
            try {
                // 首先在系统属性中获取，如果没有，则在配置中获取
                if (!StringUtils.isEmpty(System.getProperty(key))) {
                    value = System.getProperty(key);
                    SysOutUtils.printInfo("在系统属性中获取{0}的value为：{1}", key, value);
                } else {
                    value = properties.getProperty(key);
                    SysOutUtils.printInfo("在配置文件中获取{0}的value为：{1}", key, value);
                }
            } catch (Exception e) {
                log.error("{}", e);
            }
        }
        return value;
    }

    /**
     * 设置一个属性
     *
     * @param key   键
     * @param value 值
     */
    public static void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    /**
     * 获取配置路径
     *
     * @return String配置路径
     */
    public static String getConfigurationPath() {
        String path = PropertyUtils.class.getProtectionDomain().getCodeSource().getLocation().getFile();

        if (!path.endsWith("/")) {
            path = path.substring(0, path.lastIndexOf("/") + 1);
            SysOutUtils.printInfo("jar文件所在文件夹的路径:{0}", path);
        } else {
            SysOutUtils.printInfo("classpath路径:{0}", path);
        }

        // String libDir = "/" + "lib" + "/";
        // if (path.endsWith(libDir)) {
        // path = path.replaceAll(libDir, "/");
        // }
        return path;
    }

    public static void main(String[] args) {
        // loadProp("jdbc.properties");
        // System.out.println(PropertyUtils.getValueByKey("driver"));
        // loadProp("system.properties");
        // File f = new File("E:/武汉/张伟/以图搜图/有匹配排重(整理后)/SUV有匹配");
        // boolean isSuccess = f.mkdirs();
        // System.out.println(isSuccess);
        String rootPath = System.getProperty("user.dir");
        System.out.println(rootPath);
    }
}
