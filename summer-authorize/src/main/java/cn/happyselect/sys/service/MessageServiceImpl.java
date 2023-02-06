package cn.happyselect.sys.service;

import cn.happyselect.base.constants.StatusConstant;
import cn.happyselect.sys.util.UserHolder;
import cn.happyselect.sys.bean.dto.WsMessageDto;
import cn.happyselect.sys.bean.dto.WsMessageUpdateDto;
import cn.happyselect.sys.constant.ReadStatusConstant;
import cn.happyselect.sys.entity.Message;
import cn.happyselect.sys.mapper.MessageMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 消息表service服务类
 *
 * @author yaodan
 * @version 1.0
 * @date 2020年09月22日
 * @since 1.8
 */
@Service
@Slf4j
@Transactional
public class MessageServiceImpl {

    @Resource
    private MessageMapper messageMapper;

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    public Message saveMessage(@Valid WsMessageDto dto) {
        messageMapper.insert(dto);
        return dto;
    }

    public void messageUpdate(@Valid WsMessageUpdateDto dto) {
        LambdaQueryWrapper<Message> updateWrapper = new LambdaQueryWrapper();
        updateWrapper.eq(Message::getMsgId, dto.getMsgId());
        updateWrapper.eq(Message::getToUserId, UserHolder.getCurrentUser().getUserId());
        Message t = new Message();
        boolean updateFlag = false;
        if (StringUtils.hasText(dto.getStatus())) {
            t.setStatus(dto.getStatus());
            updateFlag = true;
        }
        if (StringUtils.hasText(dto.getReadStatus())) {
            t.setReadStatus(dto.getReadStatus());
            if (ReadStatusConstant.READED.equals(dto.getReadStatus())) {
                t.setStatus(StatusConstant.SUCCESS);
            }
            updateFlag = true;
        }
        if (updateFlag) {
            messageMapper.update(t, updateWrapper);
        }
    }

    public Message selectById(Long id){
        return messageMapper.selectById(id);
    }

}
