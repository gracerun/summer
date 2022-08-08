package com.summer.generator.task;

import com.summer.generator.framework.AbstractApplicationTask;
import com.summer.generator.framework.context.ApplicationContext;
import com.summer.generator.handler.BaseHandler;
import com.summer.generator.handler.impl.DtoHandler;
import com.summer.generator.model.DtoInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Dto任务
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
@Slf4j
public class DtoTask extends AbstractApplicationTask {

    private static String DTO_FTL = "template/Dto.ftl";
    private List<DtoInfo> dtoList;

    @Override
    protected boolean doInternal(ApplicationContext context) throws Exception {
        log.info("开始生成dto");
        dtoList = (List<DtoInfo>) context.getAttribute("dtoList");

        BaseHandler<DtoInfo> handler;
        for (DtoInfo dtoInfo : dtoList) {
            handler = new DtoHandler(DTO_FTL, dtoInfo);
            handler.execute(context);
        }
        log.info("结束生成dto");
        return false;
    }

}
