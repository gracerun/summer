package com.summer.generator.task;

import com.summer.generator.Constants;
import com.summer.generator.config.Configuration;
import com.summer.generator.framework.AbstractApplicationTask;
import com.summer.generator.framework.context.ApplicationContext;
import com.summer.generator.handler.BaseHandler;
import com.summer.generator.handler.impl.EntityHandler;
import com.summer.generator.model.*;
import com.summer.generator.utils.PropertyUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 实体任务类
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
@Slf4j
public class EntityTask extends AbstractApplicationTask {

	private static String ENTITY_FTL = "template/Entity.ftl";

	private List <EntityInfo> entityInfoList;

	@Override
	protected boolean doInternal(ApplicationContext context) throws Exception {
		log.info("开始生成实体");

		// 获取实体信息
		entityInfoList = (List <EntityInfo>) context.getAttribute("entityInfos");

		BaseHandler<EntityInfo> handler;
		for (EntityInfo entityInfo : entityInfoList) {
			handler = new EntityHandler(ENTITY_FTL, entityInfo);
			handler.execute(context);
		}
		log.info("生成实体类完成");
		return false;
	}

	@Override
	protected void doAfter(ApplicationContext context) throws Exception {
		super.doAfter(context);
		List <DaoInfo> daoList = new ArrayList <>();
		List <VoInfo> voList = new ArrayList <>();
		List <DtoInfo> dtoList = new ArrayList <>();
		List <ServiceInfo> serviceList = new ArrayList <>();
		List <UiInfo> uiInfoList = new ArrayList <>();
		List<ApiInfo> apiInfos = new ArrayList<>();

		// 组装Dao信息、组装Vo信息
		DaoInfo daoInfo;
		VoInfo voInfo;
		DtoInfo dtoInfo;
		ServiceInfo serviceInfo;
		UiInfo uiInfo;
		ApiInfo apiInfo;
		for (EntityInfo entityInfo : entityInfoList) {
			voInfo = new VoInfo();
			voInfo.setPackageStr(Configuration.getString("vo.package"));
			voInfo.setClassName(entityInfo.getEntityName() + Constants.VO_SUFFIX);
			voInfo.setEntityInfo(entityInfo);
			voList.add(voInfo);

			dtoInfo = new DtoInfo();
			dtoInfo.setPackageStr(Configuration.getString("dto.package"));
			dtoInfo.setClassName(entityInfo.getEntityName() + Constants.DTO_SUFFIX);
			dtoInfo.setEntityInfo(entityInfo);
			dtoList.add(dtoInfo);

			daoInfo = new DaoInfo();
			daoInfo.setClassName(entityInfo.getEntityName() + Constants.DAO_SUFFIX);
			daoInfo.setEntityInfo(entityInfo);
			daoInfo.setPackageStr(Configuration.getString("mapper.package"));
			daoInfo.setVoInfo(voInfo);
			daoList.add(daoInfo);

			serviceInfo = new ServiceInfo();
			serviceInfo.setPackageStr(Configuration.getString("service.package"));
			serviceInfo.setClassName(entityInfo.getEntityName() + Constants.SERVICE_SUFFIX);
			serviceInfo.setEntityDesc(entityInfo.getEntityDesc());
			serviceInfo.setEntityName(entityInfo.getEntityName());
			serviceInfo.setEntityPackage(entityInfo.getEntityPackage());
			serviceInfo.setVoInfo(voInfo);
			serviceList.add(serviceInfo);

			uiInfo = new UiInfo();
			uiInfo.setEntityInfo(entityInfo);
			uiInfo.setClassName(entityInfo.getEntityName() + Constants.UI_SUFFIX);
			uiInfo.setLowerEntityName(entityInfo.getEntityName().substring(0, 1).toLowerCase() + entityInfo.getEntityName().substring(1));
			uiInfoList.add(uiInfo);

			apiInfo = new ApiInfo();
			apiInfo.setClassName(entityInfo.getEntityName() + Constants.UI_SUFFIX);
			apiInfo.setEntityInfo(entityInfo);
			apiInfo.setRequestMapping(entityInfo.getEntityName().substring(0, 1).toLowerCase() + entityInfo.getEntityName().substring(1));
			apiInfos.add(apiInfo);
		}

		context.setAttribute("daoList", daoList);
		context.setAttribute("voList", voList);
		context.setAttribute("dtoList", dtoList);
		context.setAttribute("serviceInfos", serviceList);
		context.setAttribute("uiInfos", uiInfoList);
		context.setAttribute("apiInfos", apiInfos);
	}

	public static void main(String[] args) {
		File file = new File(
				"/D:\\devsoftware\\workspace\\winit-java-generator\\target\\classes\\template\\Entity.ftl");
		System.out.println(EntityTask.class.getClassLoader().getResource("").getPath());
		System.out.println(file.exists());

		PropertyUtils.setProperty("name", "qyk1");
		PropertyUtils.setProperty("NAME", "qyk22");
	}
}
