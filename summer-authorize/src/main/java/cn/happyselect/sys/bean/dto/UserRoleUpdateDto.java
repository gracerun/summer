package cn.happyselect.sys.bean.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 用户角色更新对象
 * @author adc
 * @date 2020-08-06
 * @version 1.0.0
 */

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class UserRoleUpdateDto {

    @ApiModelProperty("用户ID")
    private String userId;

    @ApiModelProperty("新增的角色")
    private String addRoleCodes;

    @ApiModelProperty("删除的角色")
    private String deleteRoleCodes;

}
