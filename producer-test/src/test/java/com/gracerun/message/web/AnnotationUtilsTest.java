package com.gracerun.message.web;

import com.gracerun.log.annotation.Logging;
import com.gracerun.message.service.AppleServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;

@Slf4j
public class AnnotationUtilsTest {

    @Test
    public void exeTest() {
        Class clazz = AppleServiceImpl.class;
        Method method = BeanUtils.findDeclaredMethod(clazz, "printName");
        log.info("{}", AnnotatedElementUtils.findMergedAnnotation(clazz, Logging.class));
        log.info("{}", AnnotatedElementUtils.findMergedAnnotation(method, Logging.class));
        log.info("----------------------------------------------------------------------");
        log.info("{}", AnnotatedElementUtils.getMergedAnnotation(clazz, Logging.class));
        log.info("{}", AnnotatedElementUtils.getMergedAnnotation(method, Logging.class));
    }

}