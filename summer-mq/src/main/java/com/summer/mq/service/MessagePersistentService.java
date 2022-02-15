package com.summer.mq.service;

import com.summer.mq.bean.MessageBody;
import com.summer.mq.bean.MessagePage;
import com.summer.mq.bean.MessageQuery;

/**
 * redis消息持久化服务
 *
 * @author Tom
 * @version 1.0.0
 * @date 12/26/21
 */
public interface MessagePersistentService {

    MessagePage findByPage(MessageQuery queryDto);

    int save(MessageBody message);

    int updateById(MessageBody message);
}