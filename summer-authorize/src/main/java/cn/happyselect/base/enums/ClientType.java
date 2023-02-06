package cn.happyselect.base.enums;

import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:客户端类型
 * @author: minliangqin
 * @date: Created in 2021/5/19 2:54 下午
 */
public enum ClientType {

    /**
     * 服务商APP
     */
    AGENT_APP,
    /**
     * 服务商web
     */
    AGENT,
    /**
     * 商户WEB
     */
    CUSTOMER,

    /**
     * 运营WEB
     */
    OPER;

    private static final Map<String, ClientType> mappings = new HashMap<>();

    static {
        for (ClientType clientType : values()) {
            mappings.put(clientType.name(), clientType);
        }
    }

    public static ClientType resolve(@Nullable String clientType) {
        return (clientType != null ? mappings.get(clientType) : null);
    }

    public static SystemType resolveSystemType(@Nullable String clientType) {
        if (StringUtils.hasText(clientType)) {
            if (ClientType.AGENT_APP.name().equals(clientType)) {
                return SystemType.AGENT;
            } else {
                return SystemType.resolve(clientType);
            }
        } else {
            return null;
        }

    }

}
