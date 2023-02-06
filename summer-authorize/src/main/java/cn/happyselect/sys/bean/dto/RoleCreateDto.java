package cn.happyselect.sys.bean.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * 角色创建Dto
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-09-08
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class RoleCreateDto {

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    @ColumnWidth(20)
    @ExcelProperty("状态")
    @NotBlank
    @Length(max = 32, message = "{role.status.length}")
    private String status;

    /**
     * 角色名称
     */
    @ApiModelProperty("角色名称")
    @ColumnWidth(20)
    @ExcelProperty("角色名称")
    @NotBlank
    @Length(max = 64, message = "{role.roleName.length}")
    private String roleName;

    /**
     * 新增的菜单编码
     */
    @ApiModelProperty("新增的菜单编码")
    private String addMenuCodes;

    /**
     * 新增的菜单按钮编码
     */
    @ApiModelProperty("新增的菜单按钮编码")
    private String addMenuFunctionCodes;
}
