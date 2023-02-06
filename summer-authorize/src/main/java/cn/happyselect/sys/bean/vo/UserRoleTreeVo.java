package cn.happyselect.sys.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 用户角色数据
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-08-11
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class UserRoleTreeVo {

    @ApiModelProperty("角色编号")
    private String roleCode;

    @ApiModelProperty("角色名称")
    private String roleName;

    @ApiModelProperty("节点的 checkBox / radio 的 勾选状态")
    private String checked;

    @ApiModelProperty("节点的 checkbox / radio 是否禁用")
    private String chkDisabled;

}
