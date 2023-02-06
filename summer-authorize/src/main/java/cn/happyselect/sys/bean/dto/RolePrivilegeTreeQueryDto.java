package cn.happyselect.sys.bean.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collection;
import java.util.List;

/**
 * 角色权限查询
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-08-11
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class RolePrivilegeTreeQueryDto {

    private Collection<String> roleCodes;
    private List<String> types;
    private String status;

    private String authRoleCode;

}
