package ${packageStr};

import com.summer.base.bean.dto.QueryItem;
import com.summer.base.bean.dto.QueryBaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * ${entityDesc}dto
 *
 * @author ${author}
 * @version 1.0
 * @date ${time}
 * @since 1.8
 */
@ApiModel(value = "${className}", description = "${entityDesc}入参类")
@Accessors(chain = true)
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
public class ${className} extends QueryBaseDto {
${propertiesStr}
}
