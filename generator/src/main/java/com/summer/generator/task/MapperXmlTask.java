package com.summer.generator.task;

import com.summer.generator.framework.AbstractApplicationTask;
import com.summer.generator.framework.context.ApplicationContext;
import com.summer.generator.handler.BaseHandler;
import com.summer.generator.handler.impl.MapperXmlHandler;
import com.summer.generator.model.MapperXmlInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * MapperXml任务
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
@Slf4j
public class MapperXmlTask extends AbstractApplicationTask {

    private static String MAPPER_XML_FTL = "template/MapperXml.ftl";

    private List<MapperXmlInfo> mapperXmlInfoList;

    @Override
    protected boolean doInternal(ApplicationContext context) throws Exception {
        log.info("开始生成MapperXml");

        // 获取实体信息
        mapperXmlInfoList = (List<MapperXmlInfo>) context.getAttribute("mapperXmlList");

        BaseHandler<MapperXmlInfo> handler;
        for (MapperXmlInfo mapperXmlInfo : mapperXmlInfoList) {
            handler = new MapperXmlHandler(MAPPER_XML_FTL, mapperXmlInfo);
            handler.execute(context);
        }
        log.info("生成MapperXml完成");
        return false;
    }

}
