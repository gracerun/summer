package cn.happyselect.sys.bean.vo;

import cn.happyselect.sys.entity.Menu;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 菜单vo
 *
 * @author yaodan
 * @date 2020年09月27日
 * @version 1.0
 * @since 1.8
 */
@ApiModel(value = "MenuVo", description = "菜单视图类")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper=true)
public class MenuVo extends Menu implements Serializable {
private static final long serialVersionUID = 435793795463710L;


}
