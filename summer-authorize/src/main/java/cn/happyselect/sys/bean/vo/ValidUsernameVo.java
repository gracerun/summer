package cn.happyselect.sys.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 验证登录名返回信息
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class ValidUsernameVo {

    /**
     * 手机号
     */
    @ApiModelProperty("手机号")
    private String phone;

    /**
     * 流水号
     */
    @ApiModelProperty("流水号")
    private String serialNo;

    /**
     * 下一步
     */
    @ApiModelProperty("下一步流程,validPhone:验证手机号,validShortMsg:验证短信验证码")
    private String nextStep;


}
