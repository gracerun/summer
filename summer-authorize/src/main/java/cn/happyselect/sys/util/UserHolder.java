package cn.happyselect.sys.util;

import cn.happyselect.base.bean.dto.UserContext;
import cn.happyselect.base.enums.SystemType;
import cn.happyselect.base.enums.UserType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

/**
 * 用户上下文获取工具
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-07-31
 */
@Slf4j
public abstract class UserHolder {

    /**
     * 获取当前登录用户信息
     *
     * @return
     */
    public static UserContext getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("{}", authentication);
        return (UserContext) authentication;
    }

    public static void validUserIdRelation(String dataUserId) {
        validUserIdRelation(dataUserId, null);
    }

    /**
     * 验证用户数据归属关系
     * 1. 本人可以修改自己的信息与员工的信息
     * 2. 员工的权限与上级的数据权限一致
     *
     * @param dataUserId
     */
    public static void validUserIdRelation(String dataUserId, String parentUserId) {
        UserContext userContext = UserHolder.getCurrentUser();
        if (SystemType.AGENT == userContext.getSystemType()
                || SystemType.CUSTOMER == userContext.getSystemType()) {
            if (UserType.ADMIN == userContext.getUserType()) {
                Assert.isTrue(
                        userContext.getUserId().equals(dataUserId)
                                || userContext.getUserId().equals(parentUserId), "无权限,请联系运营操作");
            } else if (UserType.EMPLOYEE == userContext.getUserType()) {
                Assert.isTrue(
                        userContext.getParentUserId().equals(dataUserId)
                                || userContext.getParentUserId().equals(parentUserId), "无权限,请联系运营操作");
            }
        }
    }
}
