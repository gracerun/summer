package cn.happyselect.sys.bean.dto;

import cn.happyselect.base.enums.SystemType;
import cn.happyselect.base.enums.UserType;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * 添加用户
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-08-07
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class UserCreateDto {

    /**
     * 状态
     */
    @ApiModelProperty("状态,例:有效:TRUE,无效:FALSE,删除:DELETE,锁定:LOCKED")
    @NotBlank(message = "状态不能为空")
    @Length(max = 32, message = "{user.status.length}")
    private String status;

    /**
     * 用户ID
     */
    @ApiModelProperty("用户ID")
    @Length(max = 32, message = "{user.userId.length}")
    private String userId;

    /**
     * 昵称
     */
    @ApiModelProperty("昵称")
    @NotBlank(message = "昵称不能为空")
    @Length(max = 64, message = "{user.nickname.length}")
    private String nickname;

    /**
     * 业务编码
     */
    @Length(max = 32, message = "{user.bussinessNo.length}")
    private String businessNo;

    /**
     * 用户名
     */
    @ApiModelProperty("用户名")
    @ColumnWidth(20)
    @ExcelProperty("用户名")
    @Length(max = 32, message = "{user.username.length}")
    @Pattern(regexp = "^[a-zA-Z0-9_\\.]+$", message = "用户名格式错误")
    private String username;

    /**
     * 邮箱
     */
    @ApiModelProperty("邮箱")
    @ColumnWidth(20)
    @ExcelProperty("邮箱")
    @Length(max = 64, message = "{user.email.length}")
    @Pattern(regexp = "^[a-zA-Z0-9_\\.]+@[a-zA-Z0-9-]+[\\.a-zA-Z]+$", message = "电子邮箱格式错误")
    private String email;

    /**
     * 手机号
     */
    @ApiModelProperty("手机号")
    @ColumnWidth(20)
    @ExcelProperty("手机号")
    @Length(max = 32, message = "{user.phone.length}")
    @Pattern(regexp = "^[1]\\d{10}$", message = "手机号格式错误")
    private String phone;

    /**
     * 密码
     */
    @ApiModelProperty("密码")
    @ColumnWidth(20)
    @ExcelProperty("密码")
    @Length(max = 64, message = "{user.password.length}")
    private String password;

    /**
     * 角色权限
     */
    @ApiModelProperty("角色权限")
    private List<String> roleCodes;

    /**
     * 上级用户ID
     */
    private String parentUserId;

    /**
     * 系统类型
     */
    private SystemType systemType;

    /**
     * 用户类型
     */
    private UserType userType;

    /**
     * 微信open_id
     */
    @ApiModelProperty("微信open_id")
    @ColumnWidth(20)
    @ExcelProperty("微信open_id")
    @NotBlank
    @Length(max = 64, message = "{user.openId.length}")
    private String openId;

}
