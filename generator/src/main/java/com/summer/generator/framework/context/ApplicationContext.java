package com.summer.generator.framework.context;

import java.util.HashMap;
import java.util.Map;

/**
 * 应用程序-上下文-抽象类
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
public abstract class ApplicationContext {

    protected Map<String, Object> ctx = new HashMap<>();

    public abstract void setAttribute(String key, Object obj);

    public abstract Object getAttribute(String key);

    public Map<String, Object> getCtx() {
        return ctx;
    }

    public void setCtx(Map<String, Object> ctx) {
        this.ctx = ctx;
    }
}
