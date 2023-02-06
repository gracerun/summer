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

import javax.validation.constraints.NotBlank;

/**
 * 菜单功能实体
 *
 * @author yaodan
 * @date 2020年08月07日
 * @version 1.0
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
public class MenuFunction extends BaseEntity {

    private static final long serialVersionUID = 9060529164082403L;

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    @ColumnWidth(20)
    @ExcelProperty("状态")
    @Length(max = 32, message = "{menufunction.status.length}")
    private String status;

    /**
     * 菜单编号
     */
    @ApiModelProperty("菜单编号")
    @ColumnWidth(20)
    @ExcelProperty("菜单编号")
    @NotBlank
    @Length(max = 32, message = "{menufunction.menuCode.length}")
    private String menuCode;
    /**
     * 功能编号
     */
    @ApiModelProperty("功能编号")
    @ColumnWidth(20)
    @ExcelProperty("功能编号")
    @Length(max = 32, message = "{menufunction.functionCode.length}")
    private String functionCode;
    /**
     * 功能名称
     */
    @ApiModelProperty("功能名称")
    @ColumnWidth(20)
    @ExcelProperty("功能名称")
    @NotBlank
    @Length(max = 64, message = "{menufunction.functionName.length}")
    private String functionName;
    /**
     * 菜单链接地址
     */
    @ApiModelProperty("菜单链接地址")
    @ColumnWidth(20)
    @ExcelProperty("菜单链接地址")
    @NotBlank
    @Length(max = 256, message = "{menufunction.url.length}")
    private String url;

}
