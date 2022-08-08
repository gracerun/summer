package ${packageStr};

${entityImport}
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.io.Serializable;

/**
 * ${entityDesc}vo
 *
 * @author ${author}
 * @date ${time}
 * @version 1.0
 * @since 1.8
 */
@ApiModel(value = "${className}", description = "${entityDesc}视图类")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper=true)
public class ${className} extends ${pojoClassName} implements Serializable {
private static final long serialVersionUID = ${serialVersionNum};

${propertiesStr}
<#--${propertiesStr}
${methodStr}-->

<#--@Override
public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
}-->
}