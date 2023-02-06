//package cn.happyselect.sys.service.impl;
//
//import cn.happyselect.base.utils.BeanUtil;
//import cn.happyselect.sys.bean.dto.WsMessageDto;
//import cn.happyselect.sys.entity.Message;
//import org.apache.ibatis.session.ResultContext;
//import org.apache.ibatis.session.ResultHandler;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.function.Consumer;
//
//public class MessageResultHandler implements ResultHandler<WsMessageDto> {
//
//    protected final Logger logger = LoggerFactory.getLogger(getClass());
//
//    private final static int BATCH_SIZE = 1000;
//
//    private int size;
//
//    private List<WsMessageDto> tempList = new ArrayList<>(BATCH_SIZE);
//
//    private Consumer<List<WsMessageDto>> consumer;
//
//    public MessageResultHandler(Consumer<List<WsMessageDto>> consumer) {
//        this.consumer = consumer;
//    }
//
//    @Override
//    public void handleResult(ResultContext<? extends WsMessageDto> resultContext) {
//        // 这里获取流式查询每次返回的单条结果
//        Message msg = resultContext.getResultObject();
//        WsMessageDto dto = new WsMessageDto();
//        BeanUtil.copyPropertiesIgnoreNull(msg, dto);
//        tempList.add(dto);
//        size++;
//        if (size == BATCH_SIZE) {
//            handle();
//        }
//    }
//
//    private void handle() {
//        // 在这里可以对你获取到的批量结果数据进行需要的业务处理
//        logger.info("tempList.size={}", tempList.size());
//        if (consumer != null) {
//            consumer.accept(tempList);
//        }
//        // 处理完每批数据后,创建新的集合
//        size = 0;
//        tempList = new ArrayList<>(BATCH_SIZE);
//
//    }
//
//    // 这个方法给外面调用，用来完成最后一批数据处理
//    public void end() {
//        handle();// 处理最后一批不到BATCH_SIZE的数据
//    }
//}
