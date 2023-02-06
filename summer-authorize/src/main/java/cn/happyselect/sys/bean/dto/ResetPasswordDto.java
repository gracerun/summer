package cn.happyselect.sys.bean.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * 重置密码
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class ResetPasswordDto {

    /**
     * 流水号
     */
    @ApiModelProperty("流水号")
    @NotBlank
    private String serialNo;

    /**
     * 新密码凭证
     */
    @ApiModelProperty("新密码凭证")
    private String password;

}
