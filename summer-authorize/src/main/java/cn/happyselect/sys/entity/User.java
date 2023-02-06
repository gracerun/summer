package cn.happyselect.sys.entity;

import cn.happyselect.base.entity.BaseEntity;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 用户实体
 *
 * @author yaodan
 * @version 1.0
 * @date 2020年09月27日
 * @since 1.8
 */
@Accessors(chain = true)
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel
@ColumnWidth(35)
@HeadRowHeight(20)
@ContentRowHeight(16)
public class User extends BaseEntity {

    private static final long serialVersionUID = 876838991416014L;

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    @ColumnWidth(20)
    @ExcelProperty("状态")
    @NotBlank
    @Length(max = 32, message = "{user.status.length}")
    private String status;
    /**
     * 用户ID
     */
    @ApiModelProperty("用户ID")
    @ColumnWidth(20)
    @ExcelProperty("用户ID")
    @NotBlank
    @Length(max = 32, message = "{user.userId.length}")
    private String userId;
    /**
     * 昵称
     */
    @ApiModelProperty("昵称")
    @ColumnWidth(20)
    @ExcelProperty("昵称")
    @NotBlank
    @Length(max = 64, message = "{user.nickname.length}")
    private String nickname;
    /**
     * 业务编码
     */
    @ApiModelProperty("业务编码")
    @ColumnWidth(20)
    @ExcelProperty("业务编码")
    @Length(max = 32, message = "{user.businessNo.length}")
    private String businessNo;
    /**
     * 上级用户ID
     */
    @ApiModelProperty("上级用户ID")
    @ColumnWidth(20)
    @ExcelProperty("上级用户ID")
    @Length(max = 32, message = "{user.parentUserId.length}")
    private String parentUserId;
    /**
     * 系统类型(AGENT服务商系统、CUSTOMER商户系统、OPER运营系统)
     */
    @ApiModelProperty("系统类型(AGENT服务商系统、CUSTOMER商户系统、OPER运营系统)")
    @ColumnWidth(20)
    @ExcelProperty("系统类型(AGENT服务商系统、CUSTOMER商户系统、OPER运营系统)")
    @Length(max = 32, message = "{user.systemType.length}")
    private String systemType;
    /**
     * 用户类型（ADMIN: 超级管理员 EMPLOYEE:员工）
     */
    @ApiModelProperty("用户类型（ADMIN: 超级管理员 EMPLOYEE:员工）")
    @ColumnWidth(20)
    @ExcelProperty("用户类型（ADMIN: 超级管理员 EMPLOYEE:员工）")
    @Length(max = 32, message = "{user.userType.length}")
    private String userType;
    /**
     * 创建人
     */
    @ApiModelProperty("创建人")
    @ColumnWidth(20)
    @ExcelProperty("创建人")
    @Length(max = 32, message = "{user.creator.length}")
    private String creator;
    /**
     * 用户名
     */
    @ApiModelProperty("用户名")
    @ColumnWidth(20)
    @ExcelProperty("用户名")
    @Length(max = 32, message = "{user.username.length}")
    private String username;
    /**
     * 邮箱
     */
    @ApiModelProperty("邮箱")
    @ColumnWidth(20)
    @ExcelProperty("邮箱")
    @Length(max = 64, message = "{user.email.length}")
    private String email;
    /**
     * 手机号
     */
    @ApiModelProperty("手机号")
    @ColumnWidth(20)
    @ExcelProperty("手机号")
    @Length(max = 32, message = "{user.phone.length}")
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
     * 过期时间
     */
    @ApiModelProperty("过期时间")
    @ColumnWidth(20)
    @ExcelProperty("过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date expireTime;
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
