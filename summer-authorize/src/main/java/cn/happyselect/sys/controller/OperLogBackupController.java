package cn.happyselect.sys.controller;

import cn.happyselect.sys.service.OperLogBackupServiceImpl;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 操作日志备份表控制层
 *
 * @author yaodan
 * @version 1.0
 * @date 2020年09月01日
 * @since 1.8
 */
@Slf4j
@Api(value = "操作日志备份表", tags = "操作日志备份表")
@RestController

@RequestMapping("/operLogBackup")
public class OperLogBackupController {

    @Autowired
    private OperLogBackupServiceImpl operLogBackupService;

    /**
     * 条件分页查询操作日志备份表
     *
     * @param queryDto 条件信息
     * @return
     */
//    @Translation
//    @ApiOperation(value = "分页查询操作日志备份表", notes = "按条件分页查询操作日志备份表", httpMethod = "POST", produces = "application/json")
//    @PostMapping("/findByPage")
//    public IPage<OperLogBackup> findByPage(@Valid @RequestBody OperLogBackupDto queryDto) {
//        return operLogBackupService.findByPage(queryDto, queryDto.getPageNum(), queryDto.getPageSize());
//    }

}
