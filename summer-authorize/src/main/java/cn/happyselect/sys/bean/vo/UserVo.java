package cn.happyselect.sys.bean.vo;

import cn.happyselect.sys.entity.User;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 用户vo
 *
 * @author yaodan
 * @date 2020年09月27日
 * @version 1.0
 * @since 1.8
 */
@ApiModel(value = "UserVo", description = "用户视图类")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper=true)
public class UserVo extends User implements Serializable {
private static final long serialVersionUID = 3495729815934955L;


}
