package com.summer.generator.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

/**
 * 开放模板-工具类
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
@Slf4j
public class FreeMarkerUtils {

    public static Configuration configuration;

    static {
        configuration = new Configuration(Configuration.VERSION_2_3_23);
        configuration.setObjectWrapper(Configuration.getDefaultObjectWrapper(Configuration.VERSION_2_3_23));
        configuration.setDefaultEncoding("utf-8");
        configuration.setClassForTemplateLoading(StringUtils.class, "/");
    }

    /**
     * 获取解析后的值.
     *
     * @param params 参数
     * @param temp 临时参数
     * @return String
     */
    public static String getProcessValue(Map<String, String> params, String temp) {
        try {
            Template template = new Template("",
                    new StringReader("<#escape x as (x)!>" + temp + "</#escape>"),
                    configuration);
            StringWriter sw = new StringWriter();
            template.process(params, sw);
            return sw.toString();
        } catch (Exception e) {
            log.error("[{}]", e);
        }
        return null;
    }
}
