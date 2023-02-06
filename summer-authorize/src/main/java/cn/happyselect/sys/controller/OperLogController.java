package cn.happyselect.sys.controller;

import cn.happyselect.sys.entity.OperLog;
import cn.happyselect.sys.service.OperLogServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 操作日志控制层
 *
 * @author yaodan
 * @version 1.0
 * @date 2020年08月28日
 * @since 1.8
 */
@Slf4j
@Api(value = "操作日志", tags = "操作日志")
@RestController

@RequestMapping("/operLog")
public class OperLogController {

    @Autowired
    private OperLogServiceImpl operLogService;

//    /**
//     * 条件分页查询操作日志
//     *
//     * @param queryDto 条件信息
//     * @return
//     */
//    @Translation
//    @ApiOperation(value = "分页查询操作日志", notes = "按条件分页查询操作日志", httpMethod = "POST", produces = "application/json")
//    @PostMapping("/findByPage")
//    public IPage<OperLog> findByPage(@Valid @RequestBody OperLogDto queryDto) {
//        return operLogService.findByPage(queryDto, queryDto.getPageNum(), queryDto.getPageSize());
//    }

    /**
     * 通过id查询操作日志
     *
     * @return
     */
    @ApiOperation(value = "通过id查询操作日志", notes = "通过id查询操作日志", httpMethod = "POST", produces = "application/json")
    @PostMapping("/findById/{id}")
    public OperLog findById(@PathVariable Long id) {
        return operLogService.findById(id);
    }

}
