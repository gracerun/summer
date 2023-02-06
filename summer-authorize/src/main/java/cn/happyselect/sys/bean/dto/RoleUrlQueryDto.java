package cn.happyselect.sys.bean.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collection;

/**
 * @author adc
 * @date 2017-09-01
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class RoleUrlQueryDto {

	private Collection<String> roleCodes;

	private String status;


}
