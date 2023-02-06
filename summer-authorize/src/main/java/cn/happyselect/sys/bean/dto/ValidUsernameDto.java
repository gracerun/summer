package cn.happyselect.sys.bean.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotBlank;

/**
 * 找回密码验证登录名
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class ValidUsernameDto {

    /**
     * 客户端类型
     */
    @ApiModelProperty("AGENT_APP:服务商APP,AGENT:服务商WEB,CUSTOMER:商户系统,OPER:运营系统,界面隐藏")
//    @EnumValidator(message = "客户端类型错误", target = ClientType.class)
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
     * 用户名/邮箱/手机号
     */
    @ApiModelProperty("用户名/邮箱/手机号")
    @NotBlank
    private String identifier;

    /**
     * 图片验证码
     */
    @ApiModelProperty("图片验证码")
    @NotBlank
    private String pcCode;

    /**
     * 图片ID
     */
    @ApiModelProperty("图片ID")
    @NotBlank
    private String pcId;

}
