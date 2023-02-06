package cn.happyselect.sys.bean.vo;

import cn.happyselect.sys.entity.UserRole;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 用户角色关系vo
 *
 * @author yaodan
 * @date 2020年09月27日
 * @version 1.0
 * @since 1.8
 */
@ApiModel(value = "UserRoleVo", description = "用户角色关系视图类")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper=true)
public class UserRoleVo extends UserRole implements Serializable {
private static final long serialVersionUID = 3075722105439130L;


}
