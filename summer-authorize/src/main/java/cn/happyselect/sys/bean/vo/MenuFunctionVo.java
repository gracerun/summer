package cn.happyselect.sys.bean.vo;

import cn.happyselect.sys.entity.MenuFunction;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 菜单功能vo
 *
 * @author yaodan
 * @date 2020年09月27日
 * @version 1.0
 * @since 1.8
 */
@ApiModel(value = "MenuFunctionVo", description = "菜单功能视图类")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper=true)
public class MenuFunctionVo extends MenuFunction implements Serializable {
private static final long serialVersionUID = 9501668405890406L;


}
