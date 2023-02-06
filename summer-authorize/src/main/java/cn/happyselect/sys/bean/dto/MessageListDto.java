package cn.happyselect.sys.bean.dto;

import java.util.List;
import java.util.StringJoiner;

public class MessageListDto {

    private List<WsMessageDto> msgList;

    public MessageListDto(List<WsMessageDto> msgList) {
        this.msgList = msgList;
    }

    public List<WsMessageDto> getMsgList() {
        return msgList;
    }

    public void setMsgList(List<WsMessageDto> msgList) {
        this.msgList = msgList;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MessageListDto.class.getSimpleName() + "[", "]")
                .add("msgList size=" + (msgList == null ? 0 : msgList.size()))
                .toString();
    }
}
