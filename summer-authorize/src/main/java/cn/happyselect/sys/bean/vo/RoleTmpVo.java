package cn.happyselect.sys.bean.vo;

import cn.happyselect.sys.entity.RoleTmp;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 角色模板vo
 *
 * @author yaodan
 * @date 2020年09月27日
 * @version 1.0
 * @since 1.8
 */
@ApiModel(value = "RoleTmpVo", description = "角色模板视图类")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper=true)
public class RoleTmpVo extends RoleTmp implements Serializable {
private static final long serialVersionUID = 7723744576484321L;


}
