package com.summer.mq.producer;

import com.summer.mq.bean.MessagePage;
import com.summer.mq.bean.MessageQuery;
import com.summer.mq.constant.MessageStatusConstant;
import com.summer.mq.constant.QueueConstant;
import com.summer.mq.service.MessagePersistentService;
import com.summer.mq.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 数据库消息生产者
 *
 * @author Tom
 * @version 1.0.0
 * @date 12/26/21
 */
@Slf4j
public class DataBaseProducer {

    @Autowired
    private MessagePersistentService messagePersistentService;

    @Autowired
    private RedisMessageProducer redisMessageProducer;

    @Autowired
    private RedisService redisService;

    /**
     * 每隔10分钟检查定时任务表
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void loadDB() {
        log.info("LoadDB begin");
        try {
            String lockKey = QueueConstant.QUEUE_LOAD_LOCK;
            redisService.withRenderLock(lockKey, 600, () -> {
                MessageQuery messageQuery = new MessageQuery();
                messageQuery.setStatus(MessageStatusConstant.INIT);
                messageQuery.setNextExecuteTime(new DateTime().plusSeconds(-60).toDate());
                messageQuery.setPageNum(1);
                messageQuery.setPageSize(500);
                MessagePage page = messagePersistentService.findByPage(messageQuery);
                page.getRecords().forEach(e -> e.setPersistent(true));
                redisMessageProducer.asyncSend(page.getRecords());
                for (int i = 2; i <= page.getPages(); i++) {
                    messageQuery.setPageNum(i);
                    page = messagePersistentService.findByPage(messageQuery);
                    page.getRecords().forEach(e -> e.setPersistent(true));
                    redisMessageProducer.asyncSend(page.getRecords());
                }
                return true;
            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("LoadDB end");
    }

}


