package cn.happyselect.base.bean.dto;

import cn.happyselect.base.enums.ClientType;
import cn.happyselect.base.enums.SystemType;
import cn.happyselect.base.enums.UserType;

import java.util.Collection;

/**
 * 增加用户上下文扩展接口
 * 使用场景:在用户登录或业务端需动态更新用户业务属性
 */
public interface UserContextFace {

    /**
     * 设置实名认证状态
     *
     * @param verified
     * @return
     */
    UserContextFace setVerified(String verified);

    /**
     * 设置银行账户类型
     *
     * @param accountType
     */
    UserContextFace setAccountType(String accountType);

    /**
     * 服务商等级
     *
     * @param level
     * @return
     */
    UserContextFace setLevel(Integer level);

    /**
     * 层级信息
     *
     * @param levelDetail
     * @return
     */
    UserContextFace setLevelDetail(String levelDetail);

    String getStatus();

    String getUserId();

    String getNickname();

    SystemType getSystemType();

    String getBusinessNo();

    String getIdentityType();

    String getLoginType();

    String getIdentifier();

    boolean isExpireStatus();

    String getVerified();

    Collection<String> getRoles();

    UserType getUserType();

    ClientType getClientType();

    String getParentUserId();

}
