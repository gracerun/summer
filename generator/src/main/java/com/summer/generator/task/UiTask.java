package com.summer.generator.task;

import com.summer.generator.framework.AbstractApplicationTask;
import com.summer.generator.framework.context.ApplicationContext;
import com.summer.generator.handler.BaseHandler;
import com.summer.generator.handler.impl.UiHandler;
import com.summer.generator.model.UiInfo;
import com.summer.generator.utils.PropertyUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;

/**
 * UI-任务
 *
 * @author fudian
 * @version V1.0.0
 * @since 2017/3/6
 */
@Slf4j
public class UiTask extends AbstractApplicationTask {

    private static String UI_FTL = "template/vue.ftl";

    private List<UiInfo> uiInfoList;

    @Override
    protected boolean doInternal(ApplicationContext context) throws Exception {
        log.info("开始生成ui");

        // 获取实体信息
        uiInfoList = (List<UiInfo>) context.getAttribute("uiInfos");

        BaseHandler<UiInfo> handler;
        for (UiInfo uiInfo : uiInfoList) {
            handler = new UiHandler(UI_FTL, uiInfo);
            handler.execute(context);
        }
        log.info("生成ui完成");
        return false;
    }

    @Override
    protected void doAfter(ApplicationContext context) throws Exception {
        super.doAfter(context);

        for (UiInfo uiInfo : uiInfoList) {
			uiInfo = new UiInfo();
        }
    }

    public static void main(String[] args) {
        File file = new File(
                "/D:\\devsoftware\\workspace\\winit-java-generator\\target\\classes\\template\\Ui.ftl");
        System.out.println(UiTask.class.getClassLoader().getResource("").getPath());
        System.out.println(file.exists());

        PropertyUtils.setProperty("name", "qyk1");
        PropertyUtils.setProperty("NAME", "qyk22");
    }

}
