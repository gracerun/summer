package cn.happyselect.base.enums;

import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统类型
 *
 * @author xiaojie.zhang
 * @version V1.0.0
 * @since 2020/8/4
 */
public enum SystemType {
    /**
     * 自动获取
     */
    AUTO,
    /**
     * 服务商系统
     */
    AGENT,
    /**
     * 商户系统
     */
    CUSTOMER,

    /**
     * 运营系统
     */
    OPER;

    private static final Map<String, SystemType> mappings = new HashMap<>();

    static {
        for (SystemType systemType : values()) {
            mappings.put(systemType.name(), systemType);
        }
    }

    public static SystemType resolve(@Nullable String systemType) {
        return (systemType != null ? mappings.get(systemType) : null);
    }

}
