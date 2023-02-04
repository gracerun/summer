package com.test.message.web;

import com.gracerun.log.annotation.Logging;
import com.gracerun.summermq.bean.MessageBody;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;

@Slf4j
public class AnnotationUtilsTest {

    @Test
    public void exeTest() {
        log.info("findAnnotation:{}", AnnotationUtils.findAnnotation(MessageController.class, Logging.class));
        log.info("getAnnotation:{}", AnnotationUtils.getAnnotation(MessageController.class, Logging.class));

        log.info("findAnnotation:{}", AnnotatedElementUtils.findMergedAnnotation(MessageController.class, Logging.class));
        log.info("getAnnotation:{}", AnnotatedElementUtils.getMergedAnnotation(MessageController.class, Logging.class));

        log.info("findMethodAnnotation:{}", AnnotationUtils.findAnnotation(
                BeanUtils.findDeclaredMethod(MessageController.class, "push", MessageBody.class),
                Logging.class));

        log.info("getMethodAnnotation:{}", AnnotationUtils.getAnnotation(
                BeanUtils.findDeclaredMethod(MessageController.class, "push", MessageBody.class),
                Logging.class));

        log.info("findMethodAnnotation:{}", AnnotatedElementUtils.findMergedAnnotation(
                BeanUtils.findDeclaredMethod(MessageController.class, "push", MessageBody.class),
                Logging.class));

        log.info("getMethodAnnotation:{}", AnnotatedElementUtils.getMergedAnnotation(
                BeanUtils.findDeclaredMethod(MessageController.class, "push", MessageBody.class),
                Logging.class));
    }

}