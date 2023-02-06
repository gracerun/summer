package cn.happyselect.sys.bean.dto;

import cn.happyselect.sys.entity.Message;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class WsMessageDto extends Message {

    /**
     * 接收方httpSessionId
     */
    private String toHttpSessionId;

    /**
     * 接收方wsSessionId
     */
    private String toWsSessionId;

    /**
     * 事件
     */
    private String event;

}
