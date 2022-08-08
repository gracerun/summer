package com.summer.generator.task;

import com.summer.generator.framework.AbstractApplicationTask;
import com.summer.generator.framework.context.ApplicationContext;
import com.summer.generator.handler.BaseHandler;
import com.summer.generator.handler.impl.VoHandler;
import com.summer.generator.model.VoInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Vo任务处理
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
@Slf4j
public class VoTask extends AbstractApplicationTask {

    private static String VO_FTL = "template/Vo.ftl";
    private List<VoInfo> voList;

    @Override
    protected boolean doInternal(ApplicationContext context) throws Exception {
        log.info("开始生成vo");
        voList = (List<VoInfo>) context.getAttribute("voList");

        BaseHandler<VoInfo> handler;
        for (VoInfo voInfo : voList) {
            handler = new VoHandler(VO_FTL, voInfo);
            handler.execute(context);
        }
        log.info("结束生成vo");
        return false;
    }

}
