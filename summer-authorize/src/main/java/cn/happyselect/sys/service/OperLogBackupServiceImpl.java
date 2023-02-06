package cn.happyselect.sys.service;

import cn.happyselect.sys.mapper.OperLogBackupMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 操作日志备份表service服务类
 *
 * @author yaodan
 * @version 1.0
 * @date 2020年09月01日
 * @since 1.8
 */
@Service
@Slf4j
@Transactional
public class OperLogBackupServiceImpl {

    @Resource
    private OperLogBackupMapper operLogBackupMapper;
}
