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
 * 角色模板实体
 *
 * @author yaodan
 * @version 1.0
 * @date 2020年09月08日
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
public class RoleTmp extends BaseEntity {

    private static final long serialVersionUID = 8334712055328106L;

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
     * 系统类型(AGENT服务商系统、CUSTOMER商户系统、OPER运营系统)
     */
    @ApiModelProperty("系统类型(AGENT服务商系统、CUSTOMER商户系统、OPER运营系统)")
    @ColumnWidth(20)
    @ExcelProperty("系统类型(AGENT服务商系统、CUSTOMER商户系统、OPER运营系统)")
    @NotBlank
    @Length(max = 32, message = "{roletmp.systemType.length}")
    private String systemType;
    /**
     * 角色编号
     */
    @ApiModelProperty("角色编号")
    @ColumnWidth(20)
    @ExcelProperty("角色编号")
    @Length(max = 32, message = "{roletmp.roleCode.length}")
    private String roleCode;
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
     * 角色等级
     */
    @ApiModelProperty("角色等级")
    @ColumnWidth(20)
    @ExcelProperty("角色等级")
    @Digits(integer = 10, fraction = 0)
    private Integer level;

}
