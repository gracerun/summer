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
 * 角色模板更新Dto
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-11-09
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class RoleTmpUpdateDto {
    /**
     * 角色编号
     */
    @ApiModelProperty("角色编号")
    @ColumnWidth(20)
    @ExcelProperty("角色编号")
    @NotBlank
    @Length(max = 32, message = "{role.roleCode.length}")
    private String roleCode;
    /**
     * 状态
     */
    @ApiModelProperty("状态")
    @ColumnWidth(20)
    @ExcelProperty("状态")
    @Length(max = 32, message = "{role.status.length}")
    private String status;

    /**
     * 角色名称
     */
    @ApiModelProperty("角色名称")
    @ColumnWidth(20)
    @ExcelProperty("角色名称")
    @Length(max = 64, message = "{role.roleName.length}")
    private String roleName;

    /**
     * 系统类型(AGENT服务商系统、CUSTOMER商户系统)
     */
    @ApiModelProperty("系统类型(AGENT服务商系统、CUSTOMER商户系统)")
    @ColumnWidth(20)
    @ExcelProperty("系统类型(AGENT服务商系统、CUSTOMER商户系统)")
    @Length(max = 32, message = "{roletmp.systemType.length}")
    private String systemType;

}
