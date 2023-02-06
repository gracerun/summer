package cn.happyselect.sys.bean.vo;

import cn.happyselect.sys.entity.Role;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collection;

/**
 * 用户详情
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-09-07
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class UserDetailVo {

    /**
     * 状态
     */
    @ApiModelProperty("状态,例:有效:TRUE,无效:FALSE,删除:DELETE,锁定:LOCKED")
    private String status;
    /**
     * 昵称
     */
    @ApiModelProperty("昵称")
    private String nickname;

    /**
     * 业务编码
     */
    @ApiModelProperty("业务编码, 服务商和商户系统必填,运营系统选填")
    private String businessNo;

    /**
     * 用户名
     */
    @ApiModelProperty("用户名")
    private String username;

    /**
     * 邮箱
     */
    @ApiModelProperty("邮箱")
    private String email;

    /**
     * 手机号
     */
    @ApiModelProperty("手机号")
    private String phone;

    /**
     * 角色
     */
    @ApiModelProperty("角色")
    private Collection<Role> roles;

    /**
     * 上级用户ID
     */
    @ApiModelProperty("上级用户ID")
    private String parentUserId;

    /**
     * 用户类型（ADMIN: 超级管理员 EMPLOYEE:员工）
     */
    @ApiModelProperty("用户类型（ADMIN: 超级管理员 EMPLOYEE:员工）")
    private String userType;

}
