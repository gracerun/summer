package cn.happyselect.sys.annotation;

import java.lang.annotation.*;

/**
 * 权限同步注解
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-08-27
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface PrivilegeSync {

}
