package cn.happyselect.sys.bean.dto;

import cn.happyselect.sys.bean.ShortMessageDtoInterface;
import cn.happyselect.sys.constant.LoginTypeConstant;
import cn.happyselect.sys.constant.SMSTypeConstant;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotBlank;
import java.util.Collection;

/**
 * 登录参数
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginToken extends AbstractAuthenticationToken implements ShortMessageDtoInterface {

    /**
     * 登录类型
     */
    @ApiModelProperty("0:使用用户名/邮箱/手机号+密码登录,1:使用手机号+短信登录")
    @NotBlank
    protected String loginType;

    /**
     * 客户端类型
     */
    @ApiModelProperty("AGENT_APP:服务商APP,AGENT:服务商WEB,CUSTOMER:商户系统,OPER:运营系统,界面隐藏")
    protected String clientType;

    /**
     * 系统类型
     */
    @Deprecated
    protected String systemType;

    /**
     * 用户名/邮箱/手机号
     */
    @ApiModelProperty("用户名/邮箱/手机号")
    @NotBlank
    protected String username;

    /**
     * 密码
     */
    @ApiModelProperty("密码")
    protected String password;

    /**
     * 短信验证码
     */
    @ApiModelProperty("短信验证码")
    protected String smsCode;

    /**
     * 图片验证码
     */
    @ApiModelProperty("图片验证码")
    protected String pcCode;

    /**
     * 图片ID
     */
    @ApiModelProperty("图片ID")
    protected String pcId;

    public LoginToken() {
        super(null);
    }

    @Override
    public String getClientType() {
        if (!StringUtils.hasText(clientType)) {
            return systemType;
        }
        return clientType;
    }

    public LoginToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
    }

    @Override
    public Object getPrincipal() {
        return username;
    }

    @Override
    public Object getCredentials() {
        if (LoginTypeConstant.PASSWORD.equals(loginType)) {
            return password;
        } else if (LoginTypeConstant.SMS_CODE.equals(loginType)) {
            return smsCode;
        } else {
            throw new PreAuthenticatedCredentialsNotFoundException("登录参数错误");
        }
    }

    @Override
    public String getPhone() {
        return username;
    }

    @Override
    public String getMessageType() {
        return SMSTypeConstant.LOGIN;
    }
}
