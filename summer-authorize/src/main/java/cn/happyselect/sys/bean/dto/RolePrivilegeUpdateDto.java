package cn.happyselect.sys.bean.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 角色权限更新
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class RolePrivilegeUpdateDto {

    /**
     * 角色编码
     */
    @ApiModelProperty("角色编码")
    private String roleCode;

    /**
     * 新增的菜单编码
     */
    @ApiModelProperty("新增的菜单编码")
    private String addMenuCodes;

    /**
     * 删除的菜单编码
     */
    @ApiModelProperty("删除的菜单编码")
    private String deleteMenuCodes;

    /**
     * 新增的菜单按钮编码
     */
    @ApiModelProperty("新增的菜单按钮编码")
    private String addMenuFunctionCodes;

    /**
     * 删除的菜单按钮编码
     */
    @ApiModelProperty("删除的菜单按钮编码")
    private String deleteMenuFunctionCodes;

}
