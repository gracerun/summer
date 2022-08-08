package com.summer.generator.handler.impl;

import com.summer.generator.Constants;
import com.summer.generator.handler.BaseHandler;
import com.summer.generator.model.EntityInfo;
import com.summer.generator.model.VoInfo;
import com.summer.generator.config.Configuration;
import com.summer.generator.utils.StringUtils;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Vo-处理器
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
public class VoHandler extends BaseHandler<VoInfo> {

	public VoHandler(String ftlName, VoInfo info) {
		this.ftlName = ftlName;
		this.info = info;
		this.savePath = Configuration.getString("base.baseDir") + File.separator + Configuration.getString("vo.path")
				+ File.separator + info.getClassName() + Constants.FILE_SUFFIX_JAVA;
	}

	@Override
	public void assembleParams(VoInfo info) {
		EntityInfo entityInfo = info.getEntityInfo();
		this.param.put("packageStr", info.getPackageStr());
		StringBuilder sb = new StringBuilder();
		for (String str : entityInfo.getImports()) {
			sb.append("import ").append(str).append(";\r\n");
		}

		this.param.put("importStr", sb.toString());
		this.param.put("entityDesc", entityInfo.getEntityDesc());
		this.param.put("className", info.getClassName());
		this.param.put("entityImport",
				"import " + entityInfo.getEntityPackage() + "." + entityInfo.getEntityName() + ";\r\n");
		this.param.put("pojoClassName", info.getClassName().substring(0, info.getClassName().length() - 2));


		// 生成属性，getter,setter方法
		// 生成属性，getter,setter方法
		sb = new StringBuilder();
		Map <String, String> propRemarks = entityInfo.getPropRemarks();
		Map <String, Integer> propLength = entityInfo.getPropLength();
		Map <String, String> propNameColumnNames = entityInfo.getPropNameColumnNames();
		Map <String, String> dicts = entityInfo.getDicts();
		int charIndex = 65;
		int j = 65;


		for (Entry <String, String> entry : dicts.entrySet()) {
			String propName = entry.getKey();
			String propType = entry.getValue();
			// 注释、类型、名称
			if (StringUtils.isNotBlank(dicts.get(propName)) && null != dicts.get(propName)) {
				sb.append("    @ExcelField(name = \"")
						.append(propRemarks.get(propName));
				sb.append("\", dict = \"" + dicts.get(propName));
				/*列*/
				char columnChar = (char) charIndex++;
				String columnStr = "";
				if (charIndex > 91) {
					columnStr = "A" + String.valueOf((char) (j++));
				}
				columnStr = StringUtils.isNotBlank(columnStr) ? columnStr : String.valueOf(columnChar);
				sb.append("\", column = \"" + columnStr + "\")\r\n");
			}
			sb.append("    private ");
			sb.append("String");
			sb.append(" ")
					.append(propName + "Label")
					.append(";\r\n\r\n");
		}

		for (Entry <String, String> entry : entityInfo.getPropTypes().entrySet()) {
			String propName = entry.getKey();
			String propType = entry.getValue();
			if ("areaCode".equals(propName)) {
				sb.append("    @ExcelField(name = \"地区码\", column = \"ZZ\")\n");
				sb.append("    private ");
				sb.append("String");
				sb.append(" ")
						.append(propName + "Label")
						.append(";\r\n\r\n");
			}
		}


		this.param.put("propertiesStr", sb.toString());
		this.param.put("serialVersionNum", StringUtils.generate16LongNum());
		this.param.put("tableName", entityInfo.getTableName());
	}

}
