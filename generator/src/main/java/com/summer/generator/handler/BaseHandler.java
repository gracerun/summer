package com.summer.generator.handler;


import com.summer.generator.framework.context.ApplicationContext;
import com.summer.generator.utils.DateUtils;
import com.summer.generator.utils.FileHelper;
import com.summer.generator.utils.FreeMarkerUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 基础处理器
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
public abstract class BaseHandler<T> {

    protected ApplicationContext context;
    protected String ftlName;
    protected String savePath;
    protected Map<String, String> param = new HashMap<>();
    protected T info;

    public String generateFinalStr() {
        String temp = FileHelper.readFileToString(this.getClass().getClassLoader().getResource("").getPath() + ftlName);
        return FreeMarkerUtils.getProcessValue(param, temp);
    }

    /**
     * 保存到文件
     *
     * @param str
     */
    public void saveToFile(String str) {
        FileHelper.writeToFile(savePath, str);
    }

    /**
     * 组装参数
     *
     * @param info
     */
    public abstract void assembleParams(T info);

    /**
     * 设置一些公共的参数.
     */
    public void beforeGenerate() {
        String time = DateUtils.formatDataToStr(new Date(), "yyyy年MM月dd日");
        param.put("author", System.getProperty("user.name"));
        param.put("time", time);
    }

    /**
     * 生成文件
     */
    public void execute(ApplicationContext context) {
        this.context = context;
        String str;
        assembleParams(info);
        beforeGenerate();
        str = generateFinalStr();
        saveToFile(str);
    }

}
