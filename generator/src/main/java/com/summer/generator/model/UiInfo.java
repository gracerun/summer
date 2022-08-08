package com.summer.generator.model;

import lombok.Data;

/**
 * Ui信息
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
@Data
public class UiInfo {

    /**
     * 类名
     */
    private String className;

    /**
     * 小写实体名称
     */
    private String lowerEntityName;

    /**
     * 请求映射
     */
    private String requestMapping;

    /**
     * 实体信息
     */
    private EntityInfo entityInfo;

    public static final String formElement = "        <el-form-item label=\"%s\" prop=\"%s\" :label-width=\"formLabelWidth\">\n" +
            "          <el-input v-model=\"%s\" placeholder=\"请输入%s\"></el-input>\n" +
            "        </el-form-item>";
    public static final String formRules = "          %s: [\n" +
            "            { required: true, message: \"请输入%s\", trigger: \"blur,change\" }\n" +
            "          ],";
    public static final String formEntityProps = "          %s: \"\",";
    public static final String searchElements = "          %s: {comparer: \"%s\", propValue: []}, // %s";
    public static final String searchDateElements = "          %s: {comparer: \"%s\", propValue: [Today(), Today()]}, // %s";
    public static final String tables = "            {\n" +
            "              key: \"%s\",\n" +
            "              word: \"%s\",\n" +
            "              width: \"100px\"\n" +
            "            },";
    public static final String eumnTables = "            {\n" +
            "              key: \"%s\",\n" +
            "              word: \"%s\",\n" +
            "              width: \"100px\",\n" +
            "              status: true,\n" +
            "              type: data => {\n" +
            "                return this.statusFilter(data, \"%sStatus\");\n" +
            "              }\n" +
            "            },";
    public static final String searchInput = "            {\n" +
            "              corresattr: \"%s\",\n" +
            "              type: \"text\", // 表单类型\n" +
            "              label: \"%s\", // 输入框前面的文字\n" +
            "              show: true, // 普通搜索显示\n" +
            "              value: this.searchCondition.%s.propValue[0], // 表单默认的内容\n" +
            "              cb: value => {\n" +
            "                // 表单输入之后回调函数\n" +
            "                this.searchCondition.%s.propValue[0] = value;\n" +
            "              }\n" +
            "            },";
    public static final String searchDate = "            {\n" +
            "              type: \"dateGroup\",\n" +
            "              label: \"%s\",\n" +
            "              show: true, // 普通搜索显示\n" +
            "              // lableWidth: \"80px\",\n" +
            "              options: [\n" +
            "                {\n" +
            "                  corresattr: \"%s\",\n" +
            "                  label: \"开始时间\",\n" +
            "                  defaultVlue: this.searchCondition.%s.propValue[0],\n" +
            "                  value: this.searchCondition.%s.propValue[0],\n" +
            "                  cb: value => {\n" +
            "                    this.searchCondition.%s.propValue[0] = value;\n" +
            "                  }\n" +
            "                },\n" +
            "                {\n" +
            "                  corresattr: \"%s\",\n" +
            "                  label: \"结束时间\",\n" +
            "                  value: this.searchCondition.%s.propValue[1],\n" +
            "                  cb: value => {\n" +
            "                    this.searchCondition.%s.propValue[1] = value;\n" +
            "                  }\n" +
            "                }\n" +
            "              ]\n" +
            "            },";
    public static final String searchStatus = "            {\n" +
            "              corresattr: \"%s\",\n" +
            "              type: \"select\",\n" +
            "              label: \"%s\",\n" +
            "              show: true, // 普通搜索显示\n" +
            "              defaultVlue: this.searchCondition.%s.propValue[0],\n" +
            "              value: this.searchCondition.%s.propValue[0],\n" +
            "              options: [\n" +
            "                {\n" +
            "                  value: \"\",\n" +
            "                  label: \"全部\"\n" +
            "                }" +
            "              ],\n" +
            "              cb: value => {\n" +
            "                this.searchCondition.%s.propValue[0] = value;\n" +
            "              }\n" +
            "            },";

    public static final String optionItem = "                {\n" +
            "                  value: \"%s\",\n" +
            "                  label: \"%s\"\n" +
            "                },";
}
