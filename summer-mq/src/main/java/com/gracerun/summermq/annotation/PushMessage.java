package com.gracerun.summermq.annotation;

import java.lang.annotation.*;

/**
 * 消息推送注解
 *
 * @author Tom
 * @version 1.0.0
 * @date 12/26/21
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface PushMessage {

}
