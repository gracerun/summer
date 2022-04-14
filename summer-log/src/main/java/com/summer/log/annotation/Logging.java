
package com.summer.log.annotation;

import java.lang.annotation.*;

/**
 * 日志记录
 * @author Tom
 * @date 4/13/22
 * @version 1.0.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Logging {

    String value() default "";

    Class<? extends Throwable>[] printFor() default {};

}
