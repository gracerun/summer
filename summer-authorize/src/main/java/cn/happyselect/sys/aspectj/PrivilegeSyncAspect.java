package cn.happyselect.sys.aspectj;

import cn.happyselect.sys.service.PrivilegeSyncProducer;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 权限同步
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-08-27
 */
@Aspect
@Order(Ordered.LOWEST_PRECEDENCE + 10)
@Component
@Slf4j
public class PrivilegeSyncAspect {

    private static final Integer INDEX_INIT_VALUE = 0;
    private static final ThreadLocal<Integer> THREAD_LOCAL = ThreadLocal.withInitial(() -> INDEX_INIT_VALUE);

    @Autowired
    private PrivilegeSyncProducer privilegeSyncProducer;

    @Pointcut("@annotation(cn.happyselect.sys.annotation.PrivilegeSync)")
    public void privilegeSyncPointCut() {
    }

    @Around(value = "privilegeSyncPointCut()")
    public Object aroundAdvice(ProceedingJoinPoint pjp) throws Throwable {
        try {
            THREAD_LOCAL.set(THREAD_LOCAL.get() + 1);
            Object proceed = pjp.proceed();
            if (INDEX_INIT_VALUE.equals(THREAD_LOCAL.get() - 1)) {
                privilegeSyncProducer.sendPrivilegeChangeMessage();
            }
            return proceed;
        } finally {
            THREAD_LOCAL.set(THREAD_LOCAL.get() - 1);
        }

    }

}
