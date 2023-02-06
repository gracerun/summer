package cn.happyselect.sys.entity;

import cn.happyselect.base.entity.BaseEntity;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;

/**
 * 菜单实体
 *
 * @author yaodan
 * @version 1.0
 * @date 2020年08月07日
 * @since 1.8
 */
@Accessors(chain = true)
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel
@ColumnWidth(35)
@HeadRowHeight(20)
@ContentRowHeight(16)
public class Menu extends BaseEntity {

    private static final long serialVersionUID = 9859906045648546L;

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    @ColumnWidth(20)
    @ExcelProperty("状态")
    @Length(max = 32, message = "{menu.status.length}")
    private String status;
    /**
     * 菜单编号
     */
    @ApiModelProperty("菜单编号")
    @ColumnWidth(20)
    @ExcelProperty("菜单编号")
    @Length(max = 32, message = "{menu.menuCode.length}")
    private String menuCode;
    /**
     * 菜单名称
     */
    @ApiModelProperty("菜单名称")
    @ColumnWidth(20)
    @ExcelProperty("菜单名称")
    @NotBlank
    @Length(max = 64, message = "{menu.menuName.length}")
    private String menuName;
    /**
     * 菜单级别
     */
    @ApiModelProperty("菜单级别")
    @ColumnWidth(20)
    @ExcelProperty("菜单级别")
    @Digits(integer = 10, fraction = 0)
    private Integer level;
    /**
     * 菜单链接地址
     */
    @ApiModelProperty("菜单链接地址")
    @ColumnWidth(20)
    @ExcelProperty("菜单链接地址")
    @Length(max = 256, message = "{menu.url.length}")
    private String url;
    /**
     * 上级菜单ID
     */
    @ApiModelProperty("上级菜单ID")
    @ColumnWidth(20)
    @ExcelProperty("上级菜单ID")
    @Length(max = 32, message = "{menu.parentCode.length}")
    private String parentCode;
    /**
     * 备注
     */
    @ApiModelProperty("备注")
    @ColumnWidth(20)
    @ExcelProperty("备注")
    @Length(max = 128, message = "{menu.remark.length}")
    private String remark;
    /**
     * TRUE:表示父节点，FALSE：表示叶子节点
     */
    @ApiModelProperty("TRUE:表示父节点，FALSE：表示叶子节点")
    @ColumnWidth(20)
    @ExcelProperty("TRUE:表示父节点，FALSE：表示叶子节点")
    @Length(max = 32, message = "{menu.nodeType.length}")
    private String nodeType;
    /**
     * 显示顺序
     */
    @ApiModelProperty("显示顺序")
    @ColumnWidth(20)
    @ExcelProperty("显示顺序")
    @Digits(integer = 10, fraction = 0)
    private Integer displayOrder;

}
