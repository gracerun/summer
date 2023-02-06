package cn.happyselect.sys.bean.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.StringUtils;

/**
 * 找回密码流程数据
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class IforgotProcessDto {

    /**
     * 客户端类型
     */
    @ApiModelProperty("AGENT_APP:服务商APP,AGENT:服务商WEB,CUSTOMER:商户系统,OPER:运营系统,界面隐藏")
    protected String clientType;

    public String getClientType() {
        if (!StringUtils.hasText(clientType)) {
            return systemType;
        }
        return clientType;
    }

    /**
     * 系统类型
     */
    @Deprecated
    private String systemType;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户名验证状态
     */
    private String usernameStatus;

    /**
     * 手机号验证状态
     */
    private String phoneStatus;

    /**
     * 短信验证状态
     */
    private String smsCodeStatus;

    /**
     * 数据库手机号
     */
    private String dbPhone;

}
