package cn.happyselect.sys.config;

import cn.happyselect.sys.constant.RedisKeyConstant;
import cn.happyselect.sys.service.OperLogServiceImpl;
import com.gracerun.summermq.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 操作日志定时备份
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-09-03
 */
@Component
@ConditionalOnProperty(name = "sys.operlogbackup.enabled", havingValue = "true")
public class OperLogScheduled {

    @Autowired
    private OperLogServiceImpl operLogBizService;

    @Autowired
    private RedisService redisService;

    /**
     * 操作日志备份
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void operLogBackup() {
        redisService.withRenderLock(RedisKeyConstant.OPERLOG_BACKUP_LOCKED, 3600, () -> operLogBizService.logBackUp());
    }

}
