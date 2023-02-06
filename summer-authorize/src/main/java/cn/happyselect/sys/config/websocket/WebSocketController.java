package cn.happyselect.sys.config.websocket;

import cn.happyselect.sys.bean.dto.WsMessageDto;
import cn.happyselect.sys.bean.dto.WsMessageUpdateDto;
import cn.happyselect.sys.service.MessageBizService;
import cn.happyselect.sys.constant.MsgTypeConstant;
import cn.happyselect.sys.service.MessageServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Api(value = "websocket接口", tags = "websocket接口")
@Controller
@MessageMapping("/message")
public class WebSocketController {

    @Autowired
    private MessageServiceImpl messageService;

    @ApiOperation(value = "发送消息", notes = "发送消息")
    @MessageMapping("/send")
    public void send(WsMessageDto dto, Principal principal) {
        dto.setTitle("消息通知");
        dto.setFromUserId(principal.getName());
        dto.setMsgType(MsgTypeConstant.TEXT);
        MessageBizService.saveAndSendMsgToUser(dto);
    }

    /**
     * 更新消息状态
     *
     * @param dto
     * @return
     */
    @ApiOperation(value = "更新消息状态", notes = "更新消息状态")
    @MessageMapping("/update")
    public void update(WsMessageUpdateDto dto) {
        messageService.messageUpdate(dto);
    }

}
