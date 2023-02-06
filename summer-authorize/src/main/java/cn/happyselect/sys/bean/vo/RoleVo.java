package cn.happyselect.sys.bean.vo;

import cn.happyselect.sys.entity.Role;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 角色vo
 *
 * @author yaodan
 * @date 2020年09月27日
 * @version 1.0
 * @since 1.8
 */
@ApiModel(value = "RoleVo", description = "角色视图类")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper=true)
public class RoleVo extends Role implements Serializable {
private static final long serialVersionUID = 86213544197232L;


}
