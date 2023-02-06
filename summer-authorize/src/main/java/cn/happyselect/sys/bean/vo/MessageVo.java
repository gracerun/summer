package cn.happyselect.sys.bean.vo;

import cn.happyselect.sys.entity.Message;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 消息表vo
 *
 * @author yaodan
 * @date 2020年10月22日
 * @version 1.0
 * @since 1.8
 */
@ApiModel(value = "MessageVo", description = "消息表视图类")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper=true)
public class MessageVo extends Message implements Serializable {
private static final long serialVersionUID = 9498767699655122L;


}
