package cn.happyselect.base.bean.dto;

import cn.happyselect.base.enums.ClientType;
import cn.happyselect.base.enums.SystemType;
import cn.happyselect.base.enums.UserType;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Collection;

@Accessors(chain = true)
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
public class UserContext extends AbstractAuthenticationToken implements UserContextFace, Serializable {

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private String status;
    /**
     * 用户ID
     */
    @ApiModelProperty("用户ID")
    private String userId;
    /**
     * 昵称
     */
    @ApiModelProperty("昵称")
    private String nickname;

    /**
     * 用户名
     */
    @ApiModelProperty("用户名")
    private String username;
    /**
     * 邮箱
     */
    @ApiModelProperty("邮箱")
    private String email;
    /**
     * 手机号
     */
    @ApiModelProperty("手机号")
    private String phone;

    /**
     * 系统类型
     */
    @ApiModelProperty("系统类型")
    private SystemType systemType;
    /**
     * 业务编码
     */
    @ApiModelProperty("业务编码")
    private String businessNo;

    /**
     * 登录账号类型 (手机号/邮箱/用户名) 或第三方应用名称 (微信 , 微博等)
     */
    @ApiModelProperty("账号类型")
    private String identityType;

    /**
     * 登录类型
     */
    @ApiModelProperty("登录类型")
    private String loginType;

    /**
     * 标识 (手机号/邮箱/用户名或第三方应用的唯一标识)
     */
    @ApiModelProperty("登录账号")
    private String identifier;

    /**
     * 密码过期状态 true:表示已过期,false:未过期
     */
    @ApiModelProperty("密码过期状态 true:表示已过期,false:未过期")
    private boolean expireStatus;

    /**
     * 认证状态
     */
    @ApiModelProperty("认证状态,SUCCESS:表示实名认证通过")
    private String verified;

    /**
     * 账户类型
     */
    @ApiModelProperty("账户类型")
    private String accountType;

    /**
     * 服务商等级
     */
    @ApiModelProperty("服务商等级")
    private Integer level;
    /**
     * 层级信息
     */
    @ApiModelProperty("层级信息")
    private String levelDetail;

    /**
     * 用户角色
     */
    @ApiModelProperty("用户角色")
    private Collection<String> roles;

    /**
     * 用户类型
     */
    @ApiModelProperty("用户类型")
    private UserType userType;

    /**
     * 客户端类型
     */
    @ApiModelProperty("客户端类型")
    private ClientType clientType;

    /**
     * 上级用户ID
     */
    @ApiModelProperty("上级用户ID")
    private String parentUserId;

    private String password;

    public UserContext() {
        super(null);
    }

    public UserContext(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return clientType.name() + userId;
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        password = null;
    }

    public UserContext createNewUserContext() {
        UserContext userContext = new UserContext(this.getAuthorities());
        BeanUtil.copyProperties(this, userContext, CopyOptions.create().ignoreNullValue());
        return userContext;
    }

}
