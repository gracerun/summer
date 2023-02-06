package cn.happyselect.sys.controller;

import cn.happyselect.sys.service.MessageBizService;
import cn.happyselect.sys.service.WebSocketMessageProducer;
import cn.happyselect.sys.entity.Message;
import cn.happyselect.sys.service.MessageServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 消息表控制层
 *
 * @author yaodan
 * @version 1.0
 * @date 2020年09月22日
 * @since 1.8
 */
@Slf4j
@Api(value = "消息表", tags = "消息表")
@RestController

@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageServiceImpl messageService;

    @Autowired
    private MessageBizService messageBizService;

    @Autowired
    private WebSocketMessageProducer webSocketMessageProducer;

    /**
     * 条件分页查询消息表
     *
     * @param queryDto 条件信息
     * @return
     */
//    @ApiOperation(value = "分页查询消息表", notes = "按条件分页查询消息表", httpMethod = "POST", produces = "application/json")
//    @PostMapping("/findByPage")
//    public IPage<Message> findByPage(@Valid @RequestBody MessageDto queryDto) {
//        return messageService.findByPage(queryDto, queryDto.getPageNum(), queryDto.getPageSize());
//    }

    /**
     * 新增消息表
     *
     * @param pojo
     * @return
     */
//    @ApiOperation(value = "新增消息表数据", notes = "新增消息表数据", httpMethod = "POST", produces = "application/json")
//    @PostMapping("/create")
//    public Message create(@Valid @RequestBody Message pojo) {
//        return messageService.create(pojo);
//    }

    /**
     * 更新消息表
     *
     * @param pojo
     * @return
     */
//    @ApiOperation(value = "更新消息表数据", notes = "更新消息表数据", httpMethod = "POST", produces = "application/json")
//    @PostMapping("/update")
//    public Message update(@RequestBody Message pojo) {
//        return messageService.update(pojo);
//    }

    /**
     * 重发单条消息
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "重发单条消息", notes = "重发单条消息", httpMethod = "POST", produces = "application/json")
    @PostMapping("/resend/{id}")
    public void resend(@PathVariable Long id) {
        messageBizService.resend(id);
    }

    /**
     * 重发所有待发送的消息
     *
     * @return
     */
//    @ApiOperation(value = "重发所有待发送的消息", notes = "重发所有待发送的消息", httpMethod = "POST", produces = "application/json")
//    @PostMapping("/resend")
//    public String resend() {
//        return webSocketMessageProducer.executeLoad();
//    }

}
