package com.gracerun.summermq.producer;

import com.gracerun.component.service.RedisService;
import com.gracerun.summermq.bean.MessagePage;
import com.gracerun.summermq.bean.MessageQuery;
import com.gracerun.summermq.constant.MessageStatusConstant;
import com.gracerun.summermq.constant.QueueConstant;
import com.gracerun.summermq.service.MessagePersistentService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;

/**
 * 数据库消息生产者
 *
 * @author Tom
 * @version 1.0.0
 * @date 12/26/21
 */
@Slf4j
public class DataBaseProducer {

    @Resource
    private MessagePersistentService messagePersistentService;

    @Resource
    private RedisMessageProducer redisMessageProducer;

    @Resource
    private RedisService redisService;

    /**
     * 每隔10分钟检查定时任务表
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void loadDB() {
        try {
            String lockKey = QueueConstant.QUEUE_LOAD_LOCK;
            redisService.withRenderLock(lockKey, 600, () -> {
                MessageQuery messageQuery = new MessageQuery();
                messageQuery.setStatus(MessageStatusConstant.INIT);
                messageQuery.setNextExecuteTime(new DateTime().plusSeconds(-30 * 60).toDate());
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
    }

}


