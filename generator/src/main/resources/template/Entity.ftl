package ${packageStr};

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
import org.hibernate.validator.constraints.Length;
import com.summer.base.entity.BaseEntity;
import lombok.experimental.Accessors;
${imports}

/**
 * ${entityDesc}实体
 *
 * @author ${author}
 * @date ${time}
 * @version 1.0
 * @since 1.8
 */
@Accessors(chain = true)
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
@ApiModel
@ColumnWidth(35)
@HeadRowHeight(20)
@ContentRowHeight(16)
public class ${className} extends BaseEntity {

    private static final long serialVersionUID = ${serialVersionNum};
${propertiesStr}
${methodStr}
}
