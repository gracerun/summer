package com.summer.generator.handler.impl;

import com.summer.generator.Constants;
import com.summer.generator.handler.BaseHandler;
import com.summer.generator.config.Configuration;
import com.summer.generator.model.ApiInfo;
import com.summer.generator.model.EntityInfo;

import java.io.File;

/**
 * API-处理器
 *
 * @author xiaojie.zhang
 * @version V1.0.0
 * @since 2017/3/6
 */
public class ApiHandler extends BaseHandler<ApiInfo> {


	public ApiHandler(String ftlName, ApiInfo info) {
		this.ftlName = ftlName;
		this.info = info;
		this.savePath = Configuration.getString("base.baseDir") + File.separator
				+ Configuration.getString("api.path") + File.separator + info.getRequestMapping() + "_api"
				+ Constants.FILE_SUFFIX_JS;
	}

	/**
	 * 组装参数
	 *
	 * @param info ui信息
	 */
	@Override
	public void assembleParams(ApiInfo info) {
		EntityInfo entityInfo = info.getEntityInfo();
		this.param.put("entityName", entityInfo.getEntityName());
		this.param.put("entityDesc", entityInfo.getEntityDesc());
		this.param.put("requestMapping", info.getRequestMapping());
	}
}
