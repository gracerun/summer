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
 * 角色模板创建Dto
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-11-09
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class RoleTmpCreateDto {

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    @ColumnWidth(20)
    @ExcelProperty("状态")
    @NotBlank
    @Length(max = 32, message = "{roletmp.status.length}")
    private String status;

    /**
     * 系统类型(AGENT服务商系统、CUSTOMER商户系统)
     */
    @ApiModelProperty("系统类型(AGENT服务商系统、CUSTOMER商户系统)")
    @ColumnWidth(20)
    @ExcelProperty("系统类型(AGENT服务商系统、CUSTOMER商户系统)")
    @NotBlank
    @Length(max = 32, message = "{roletmp.systemType.length}")
    private String systemType;

    /**
     * 角色名称
     */
    @ApiModelProperty("角色名称")
    @ColumnWidth(20)
    @ExcelProperty("角色名称")
    @NotBlank
    @Length(max = 64, message = "{roletmp.roleName.length}")
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
