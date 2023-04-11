
package com.gracerun.log.annotation;

import com.gracerun.log.constant.Level;
import com.gracerun.log.serializer.LogSerializer;
import com.gracerun.log.serializer.ToStringSerializer;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 日志记录
 *
 * @author Tom
 * @version 1.0.0
 * @date 4/13/22
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Logging {

    /**
     * 日志名称
     *
     * @return
     */
    @AliasFor("name")
    String value() default "";

    /**
     * 日志名称
     *
     * @return
     */
    @AliasFor("value")
    String name() default "";

    /**
     * 序列化参数
     *
     * @return
     */
    Class<? extends LogSerializer> serializeArgsUsing() default ToStringSerializer.class;

    /**
     * 序列化返回值
     *
     * @return
     */
    Class<? extends LogSerializer> serializeReturnUsing() default ToStringSerializer.class;

    /**
     * 定义方法入参与返回值的日志级别
     * Level.OFF: 关闭方法入参与返回值的日志
     *
     * @return
     */
    Level level() default Level.INFO;

    /**
     * 单行日志最大长度, 异常堆栈不受此配置限制
     * -1:不限制长度
     *
     * @return
     */
    int maxLength() default -1;

    /**
     * 异常日志
     *
     * @return
     */
    ThrowableLog[] throwableLog() default {};

}
