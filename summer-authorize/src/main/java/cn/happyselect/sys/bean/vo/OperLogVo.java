package cn.happyselect.sys.bean.vo;

import cn.happyselect.sys.entity.OperLog;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 操作日志vo
 *
 * @author yaodan
 * @date 2020年09月27日
 * @version 1.0
 * @since 1.8
 */
@ApiModel(value = "OperLogVo", description = "操作日志视图类")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper=true)
public class OperLogVo extends OperLog implements Serializable {
private static final long serialVersionUID = 1228349569547837L;


}
