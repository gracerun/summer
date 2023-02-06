package cn.happyselect.sys.service;

import cn.happyselect.sys.bean.dto.OperLogBackupQueryDto;
import cn.happyselect.sys.entity.OperLog;
import cn.happyselect.sys.mapper.OperLogBackupMapper;
import cn.happyselect.sys.mapper.OperLogMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 操作日志service服务类
 *
 * @author yaodan
 * @version 1.0
 * @date 2020年08月07日
 * @since 1.8
 */
@Service
@Slf4j
@Transactional
public class OperLogServiceImpl {

    @Resource
    private OperLogMapper operLogMapper;

    @Autowired
    private OperLogBackupMapper operLogBackupMapper;

    public boolean logBackUp() {
        DateTime dateTime = new DateTime();
        dateTime = dateTime.plusDays(-10).plusMillis(-dateTime.getMillisOfDay());
        return logBackUp(dateTime.toDate());
    }

    public boolean logBackUp(Date time) {
        OperLogBackupQueryDto dto = new OperLogBackupQueryDto();
        dto.setCreateTime(time);
        int insertSize = operLogBackupMapper.batchBackup(dto);

        LambdaQueryWrapper<OperLog> query = new LambdaQueryWrapper<>();
        query.lt(OperLog::getCreateTime, time);

        int deleteSize = operLogMapper.delete(query);

        Assert.isTrue(insertSize == deleteSize, "操作日志数据备份条数与删除条数不一致");
        log.info("成功备份{}条", insertSize);
        return true;
    }

    public void saveOperLog(OperLog operLog) {
        operLogMapper.insert(operLog);
    }

    public OperLog findById(Long id) {
        return operLogMapper.selectById(id);
    }
}
