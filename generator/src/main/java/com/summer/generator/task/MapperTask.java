package com.summer.generator.task;

import com.summer.generator.Constants;
import com.summer.generator.framework.AbstractApplicationTask;
import com.summer.generator.framework.context.ApplicationContext;
import com.summer.generator.handler.BaseHandler;
import com.summer.generator.handler.impl.MapperHandler;
import com.summer.generator.model.DaoInfo;
import com.summer.generator.model.MapperXmlInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Dao任务
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
@Slf4j
public class MapperTask extends AbstractApplicationTask {

    private final static String DAO_FTL = "template/Mapper.ftl";

    private List<DaoInfo> daoInfoList;

    @Override
    protected boolean doInternal(ApplicationContext context) throws Exception {
        log.info("开始生成Dao");

        // 获取实体信息
        daoInfoList = (List<DaoInfo>) context.getAttribute("daoList");

        BaseHandler<DaoInfo> handler;
        for (DaoInfo daoInfo : daoInfoList) {
            handler = new MapperHandler(DAO_FTL, daoInfo);
            handler.execute(context);
        }
        log.info("生成Dao完成");
        return false;
    }

    @Override
    protected void doAfter(ApplicationContext context) throws Exception {
        super.doAfter(context);
        List<MapperXmlInfo> mapperXmlInfoList = new ArrayList<>();
        // 组装Dao信息、组装Vo信息
        MapperXmlInfo mapperXmlInfo;
        for (DaoInfo daoInfo : daoInfoList) {
            mapperXmlInfo = new MapperXmlInfo();
            mapperXmlInfo.setNameSpace(daoInfo.getPackageStr() + "." + daoInfo.getClassName());
            mapperXmlInfo.setEntityInfo(daoInfo.getEntityInfo());
            mapperXmlInfo.setVoInfo(daoInfo.getVoInfo());
            mapperXmlInfo.setClassName(daoInfo.getEntityInfo().getEntityName() + Constants.MAPPER_XML_SUFFIX);
            mapperXmlInfoList.add(mapperXmlInfo);
        }

        context.setAttribute("mapperXmlList", mapperXmlInfoList);
    }

}
