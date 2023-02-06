package cn.happyselect.sys.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 角色权限树
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-08-10
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class RolePrivilegeTreeVo {

    private String nodeId;

    private String parentNodeId;

    private String privilegeCode;

    private String privilegeName;

    private String nodeType;

    private String type;

    @ApiModelProperty("节点的 checkBox / radio 的 勾选状态")
    private String checked;

    @ApiModelProperty("节点的 checkbox / radio 是否禁用")
    private String chkDisabled;

}
