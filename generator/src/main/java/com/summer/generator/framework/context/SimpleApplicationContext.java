package com.summer.generator.framework.context;

/**
 * 简单应用上下文
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
public class SimpleApplicationContext extends ApplicationContext {

    @Override
    public void setAttribute(String key, Object obj) {
        this.ctx.put(key, obj);
    }

    @Override
    public Object getAttribute(String key) {
        return this.ctx.get(key);
    }

}
