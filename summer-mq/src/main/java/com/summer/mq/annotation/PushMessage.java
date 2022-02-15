package com.summer.mq.annotation;

import java.lang.annotation.*;

/**
 * 消息推送注解
 * @author Tom
 * @date 12/26/21
 * @version 1.0.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface PushMessage {

}
