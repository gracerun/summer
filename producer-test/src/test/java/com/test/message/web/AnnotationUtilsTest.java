package com.test.message.web;

import com.gracerun.log.annotation.Logging;
import com.gracerun.summermq.bean.MessageBody;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;

@Slf4j
public class AnnotationUtilsTest {

    @Test
    public void exeTest() {
        log.info("AnnotationUtils.findClassAnnotation:\n {}", AnnotationUtils.findAnnotation(MessageInterface.class, Logging.class));

        log.info("AnnotationUtils.findClassAnnotation:\n {}", AnnotationUtils.findAnnotation(MessageController.class, Logging.class));
        log.info("AnnotationUtils.getClassAnnotation:\n {}", AnnotationUtils.getAnnotation(MessageController.class, Logging.class));
        log.info("AnnotatedElementUtils.findClassMergedAnnotation:\n {}", AnnotatedElementUtils.findMergedAnnotation(MessageController.class, Logging.class));
        log.info("AnnotatedElementUtils.getClassMergedAnnotation:\n {}", AnnotatedElementUtils.getMergedAnnotation(MessageController.class, Logging.class));

        Method method = BeanUtils.findDeclaredMethod(MessageController.class, "log", MessageBody.class);

        log.info("AnnotationUtils.findMethodAnnotation:\n {}", AnnotationUtils.findAnnotation(method, Logging.class));
        log.info("AnnotationUtils.getMethodAnnotation:\n {}", AnnotationUtils.getAnnotation(method, Logging.class));

        log.info("AnnotatedElementUtils.findMethodMergeAnnotation:\n {}", AnnotatedElementUtils.findMergedAnnotation(method, Logging.class));
        log.info("AnnotatedElementUtils.getMethodMergeAnnotation:\n {}", AnnotatedElementUtils.getMergedAnnotation(method, Logging.class));

        log.info("AnnotatedElementUtils.findMethodMergeAnnotation:\n {}", AnnotatedElementUtils.findMergedAnnotationAttributes(method, Logging.class, false, false));
        log.info("AnnotatedElementUtils.getMethodMergeAnnotation:\n {}", AnnotatedElementUtils.findMergedAnnotationAttributes(method, Logging.class, false, false));
    }

}