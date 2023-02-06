package cn.happyselect.sys.bean.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * 找回密码验证手机号
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class ValidPhoneDto {

    /**
     * 流水号
     */
    @ApiModelProperty("流水号")
    @NotBlank
    private String serialNo;

    /**
     * 手机号
     */
    @ApiModelProperty("手机号")
    @NotBlank
    private String phone;


}
