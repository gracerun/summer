package com.summer.generator.config;

import com.summer.generator.utils.StringHelper;
import com.summer.generator.utils.XMLHelper;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 配置
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
@Slf4j
public class Configuration {

    private static Map items = new HashMap();

    private static String CONFIG_FILE_NAME = "configuration.xml";

    static {
        loadConfig();
    }

    /**
     * 读入配置文件
     */
    private static void loadConfig() {
        try {
            Document document = XMLHelper.getDocument(Configuration.class, CONFIG_FILE_NAME);
            if (document != null) {
                Element systemElement = document.getRootElement();
                List catList = systemElement.elements("category");
                for (Iterator catIterator = catList.iterator(); catIterator.hasNext(); ) {
                    Element catElement = (Element) catIterator.next();
                    String catName = catElement.attributeValue("name");
                    if (StringHelper.isEmpty(catName)) {
                        continue;
                    }

                    List itemList = catElement.elements("item");
                    for (Iterator itemIterator = itemList.iterator(); itemIterator.hasNext(); ) {
                        Element itemElement = (Element) itemIterator.next();
                        String itemName = itemElement.attributeValue("name");
                        String value = itemElement.attributeValue("value");
                        if (!StringHelper.isEmpty(itemName)) {
                            items.put(catName + "." + itemName, value);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            log.error("读入配置文件出错", ex);
        }

        items.forEach((k, v) -> {
            if (String.valueOf(k).indexOf(".package") > -1) {
                v = Configuration.getString("projectInfo.packageName") + "."
                        + Configuration.getString("projectInfo.projectName") + "." + v;
                items.put(k, v);
            }
        });
    }

    /**
     * 获得字串配置值
     *
     * @param name
     * @return String
     */
    public static String getString(String name) {
        String value = (String) items.get(name);
        return (value == null) ? "" : value;
    }

    /**
     * 获得字串配置值，若为空，则返回缺省值
     *
     * @param name
     * @param defaultValue
     * @return String
     */
    public static String getString(String name, String defaultValue) {
        String value = (String) items.get(name);
        if (value != null && value.length() > 0) {
            return value;
        } else {
            return defaultValue;
        }
    }

    /**
     * 获得整型配置值
     *
     * @param name
     * @return
     */
    public static int getInt(String name) {
        String value = getString(name);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            log.debug("配置文件key[" + name + "]配置错误，return 0", ex);
        }
        return 0;
    }

    /**
     * 获得整型配置值
     *
     * @param name
     * @return
     */
    public static int getInt(String name, int defaultValue) {
        String value = getString(name);
        if ("".equals(value)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            log.debug("配置文件key[" + name + "]配置错误，return " + defaultValue, ex);
        }
        return defaultValue;
    }

    /**
     * 获得布尔型配置值
     *
     * @param name
     * @return
     */
    public static boolean getBoolean(String name) {
        String value = getString(name);
        return Boolean.valueOf(value).booleanValue();
    }

    /**
     * 获得双精度浮点数配置值
     *
     * @param name
     * @return double
     */
    public static double getDouble(String name, double defaultValue) {
        String value = getString(name);
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            log.error("配置文件key[" + name + "]配置错误，return " + defaultValue, ex);
        }
        return defaultValue;
    }

    public static Map getItems() {
        return items;
    }
}
