package cn.happyselect.sys.bean.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 角色url数据
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-08-11
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class RoleUrlVo {

    private String url;

    private String type;

    private String roleCode;

}
