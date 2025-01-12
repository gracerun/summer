package com.gracerun.idempotent.scheduled;

import com.gracerun.component.service.RedisService;
import com.gracerun.idempotent.repository.IdempotentRepository;
import com.gracerun.log.annotation.Logging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;

/**
 * @author Tom
 * @version 1.0.0
 * @date 2024/2/4
 */
@Slf4j
@Logging
public class IdempotentScheduled {

    @Resource
    private IdempotentRepository idempotentRepository;
    @Resource
    private RedisService redisService;

    @Scheduled(cron = "0 0 2 * * *")
    public void clearHistory() {
        redisService.withRenderLock(IdempotentScheduled.class.getName(), 10 * 60, () -> {
            idempotentRepository.clearHistory();
            return true;
        });
    }

}
