package cn.happyselect.sys.service;

import cn.happyselect.base.constants.StatusConstant;
import cn.happyselect.sys.bean.dto.WsMessageDto;
import cn.happyselect.sys.constant.DestinationConstant;
import cn.happyselect.sys.constant.ReadStatusConstant;
import cn.happyselect.sys.entity.Message;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

/**
 * websocket消息服务
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-10-09
 */
@Service
@Slf4j
@ConditionalOnBean(annotation = {EnableWebSocketMessageBroker.class})
public class MessageBizService {

    private static MessageServiceImpl messageService;

    private static WebSocketMessageProducer messageProducer;

    @Autowired
    public void setMessageService(MessageServiceImpl messageService) {
        MessageBizService.messageService = messageService;
    }

    @Autowired
    public void setMessageProducer(WebSocketMessageProducer messageProducer) {
        MessageBizService.messageProducer = messageProducer;
    }

    /**
     * 重发单条消息
     *
     * @param id
     * @return
     */
    public void resend(Long id) {
        Message message = messageService.selectById(id);
        if (message != null) {
            WsMessageDto dto = new WsMessageDto();
            BeanUtil.copyProperties(message, dto, CopyOptions.create().ignoreNullValue());
            messageProducer.push(dto);
        }
    }

    /**
     * 保存消息并发给指定用户
     *
     * @param dto
     */
    public static void saveAndSendMsgToUser(WsMessageDto dto) {
        dto.setDestination(DestinationConstant.QUEUE_MESSAGE);
        dto.setStatus(StatusConstant.INIT);
        dto.setReadStatus(ReadStatusConstant.UNREAD);
        dto.setMsgId(Long.toString(IdWorker.getId()));
        messageService.saveMessage(dto);
        messageProducer.push(dto);
    }

    /**
     * 发送消息给指定用户
     *
     * @param dto
     */
    public static void sendToUser(WsMessageDto dto) {
        dto.setDestination(DestinationConstant.QUEUE_MESSAGE);
        dto.setMsgId(Long.toString(IdWorker.getId()));
        messageProducer.push(dto);
    }

    /**
     * 发送广播消息
     *
     * @param dto
     */
    public static void sendToTopic(WsMessageDto dto) {
        dto.setMsgId(Long.toString(IdWorker.getId()));
        messageProducer.push(dto);
    }

}
