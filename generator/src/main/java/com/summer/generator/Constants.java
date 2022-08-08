package com.summer.generator;

/**
 * 常量
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
public class Constants {

    /**
     * 空字符串
     */
    public static String EMPTY_STR = "";
    public static String PERCENT = "%";

    /**
     * 实体后缀
     */
    public static String ENTITY_SUFFIX = "";

    public static String DAO_SUFFIX = "Mapper";

    public static String MAPPER_XML_SUFFIX = "Mapper";

    public static String VO_SUFFIX = "Vo";
    public static String DTO_SUFFIX = "Dto";

    public static String SERVICE_SUFFIX = "Service";
    public static String UI_SUFFIX = "";


    public static String SERVICE_IMPL_SUFFIX = "Service";

    public static String SERVICE_TEST_SUFFIX = "ServiceTest";

    public static String CONTROLLER_SUFFIX = "Controller";

    /**
     * 数据库类型
     */
    public enum DbType {
        mysql,
        oracle,
        postgresql
    }

    /**
     * number类型
     */
    public static String DBTYPE_NUMBER = "number";

    /**
     * Long类型
     */
    public static String PROPTYPE_LONG = "Long";
    public static String PROPTYPE_INTEGER = "Integer";
    public static String PROPTYPE_DOUBLE = "Double";

    /**
     * 文件后缀
     */
    public static String FILE_SUFFIX_JAVA = ".java";
    public static String FILE_SUFFIX_XML = ".xml";
    public static String FILE_SUFFIX_JS = ".js";
    public static String FILE_SUFFIX_VUE = ".vue";

    /**
     * 下划线
     */
    public static String CHARACTER_BOTTOM_LINE = "_";

    /**
     * 点号
     */
    public static String CHARACTER_POINT = ".";

    /**
     * 分号
     */
    public static String CHARACTER_SPLIT = ";";

    /**
     * 逗号
     */
    public static String CHARACTER_COMMA = ",";

}
