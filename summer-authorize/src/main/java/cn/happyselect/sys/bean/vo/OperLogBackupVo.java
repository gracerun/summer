package cn.happyselect.sys.bean.vo;

import cn.happyselect.sys.entity.OperLogBackup;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 操作日志备份表vo
 *
 * @author yaodan
 * @date 2020年09月27日
 * @version 1.0
 * @since 1.8
 */
@ApiModel(value = "OperLogBackupVo", description = "操作日志备份表视图类")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper=true)
public class OperLogBackupVo extends OperLogBackup implements Serializable {
private static final long serialVersionUID = 4007145742667907L;


}
