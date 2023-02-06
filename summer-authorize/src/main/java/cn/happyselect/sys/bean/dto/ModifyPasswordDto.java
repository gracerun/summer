package cn.happyselect.sys.bean.dto;

import cn.happyselect.sys.enums.PwdModifyType;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 密码修改
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-08-11
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class ModifyPasswordDto {

    /**
     * 密码修改方式
     */
    @ApiModelProperty("密码修改方式--> OLD_PASSWORD:使用旧密码修改密码,SMS_CODE:使用手机短信修改密码")
//    @EnumValidator(target = PwdModifyType.class, message = "密码修改方式错误")
    private String pwdModifyType = PwdModifyType.OLD_PASSWORD.name();

    /**
     * 旧密码凭证
     */
    @ApiModelProperty("旧密码凭证")
    private String oldPassword;
    /**
     * 新密码凭证
     */
    @ApiModelProperty("新密码凭证")
    private String password;

    /**
     * 短信验证码
     */
    @ApiModelProperty("短信验证码")
    protected String smsCode;

}
