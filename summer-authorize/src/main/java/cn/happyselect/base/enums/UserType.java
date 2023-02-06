package cn.happyselect.base.enums;

import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户类型
 */
public enum UserType {

    /**
     * 超级管理员
     */
    ADMIN,

    /**
     * 员工
     */
    EMPLOYEE;

    private static final Map<String, UserType> mappings = new HashMap<>();

    static {
        for (UserType userType : values()) {
            mappings.put(userType.name(), userType);
        }
    }

    public static UserType resolve(@Nullable String userType) {
        return (userType != null ? mappings.get(userType) : null);
    }

}
