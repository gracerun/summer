package cn.happyselect.sys.bean.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class DragMenuDto {

    /**
     * 被移动的菜单编码
     */
    @ApiModelProperty("被移动的菜单编码")
    private String menuCode;

    /**
     * 目标菜单编码
     */
    @ApiModelProperty("目标菜单编码")
    private String targetCode;

    /**
     * 移动方向
     */
    @ApiModelProperty("移动方向, prev:拖动菜单栏到目标的上方, next:拖动菜单栏到目标的上方")
    private String moveType;

}
