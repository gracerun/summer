package cn.happyselect.sys.bean.dto;

import cn.happyselect.sys.bean.ShortMessageDtoInterface;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotBlank;

/**
 * 发送短信DTO
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class ShortMessageDto implements ShortMessageDtoInterface {

    /**
     * 手机号
     */
    @ApiModelProperty("手机号")
    @NotBlank
    private String phone;

    /**
     * 短信类型
     */
    @ApiModelProperty("短信类型--> 0:登录短信,1:找回密码短信,2:注册短信,3:修改密码短信")
    @NotBlank
    private String messageType;

    /**
     * 系统类型
     */
    @Deprecated
    protected String systemType;

    /**
     * 客户端类型
     */
    @ApiModelProperty("AGENT_APP:服务商APP,AGENT:服务商WEB,CUSTOMER:商户系统,OPER:运营系统,界面隐藏")
    protected String clientType;

    @Override
    public String getClientType() {
        if (!StringUtils.hasText(clientType)) {
            return systemType;
        }
        return clientType;
    }

}
