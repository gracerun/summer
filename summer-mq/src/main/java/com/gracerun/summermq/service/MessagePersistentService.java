package com.gracerun.summermq.service;

import com.gracerun.summermq.bean.MessageBody;
import com.gracerun.summermq.bean.MessagePage;
import com.gracerun.summermq.bean.MessageQuery;

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