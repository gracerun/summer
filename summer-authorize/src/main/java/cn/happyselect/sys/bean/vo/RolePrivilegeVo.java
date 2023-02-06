package cn.happyselect.sys.bean.vo;

import cn.happyselect.sys.entity.RolePrivilege;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 角色权限关系vo
 *
 * @author yaodan
 * @date 2020年09月27日
 * @version 1.0
 * @since 1.8
 */
@ApiModel(value = "RolePrivilegeVo", description = "角色权限关系视图类")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper=true)
public class RolePrivilegeVo extends RolePrivilege implements Serializable {
private static final long serialVersionUID = 1198444545267571L;


}
