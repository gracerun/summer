package cn.happyselect.sys.bean.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * 找回密码验证短信验证码
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class ValidSmsDto {

    /**
     * 流水号
     */
    @ApiModelProperty("流水号")
    @NotBlank
    private String serialNo;

    /**
     * 短信验证码
     */
    @ApiModelProperty("短信验证码")
    @NotBlank
    protected String smsCode;

}
