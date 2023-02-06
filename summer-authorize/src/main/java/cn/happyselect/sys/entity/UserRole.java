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
import java.util.Date;
/**
 * 用户角色关系实体
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
public class UserRole extends BaseEntity {

    private static final long serialVersionUID = 7930172586269025L;

    /**
     * 主键
     */
    @ApiModelProperty("主键")
    @ColumnWidth(20)
    @ExcelProperty("主键")
    @NotBlank
    @Length(max = 32, message = "{userrole.userId.length}")
    private String userId;
    /**
     * 主键
     */
    @ApiModelProperty("主键")
    @ColumnWidth(20)
    @ExcelProperty("主键")
    @NotBlank
    @Length(max = 32, message = "{userrole.roleCode.length}")
    private String roleCode;


    private transient Long id;
    /**
     * 乐观锁
     */
    private transient Integer optimistic;
    /**
     * 创建时间
     */
    private transient Date createTime;
    /**
     * 最后更新时间
     */
    private transient Date lastUpdateTime;

}
