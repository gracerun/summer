package com.gracerun.component.service;

import brave.baggage.BaggageFields;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.gracerun.tool.bean.ByteData;
import com.gracerun.tool.bean.ResponseBean;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.util.List;


@Slf4j
public class DingTalkReportService implements ReportService {

    @Value("${spring.profiles.active:}")
    private String profile;

    @Override
    public ResponseBean<String> sendText(String s, String s1) {
        try {
            if (!StringUtils.hasText(s1) || !StringUtils.hasText(s)) {
                return ResponseBean.success();
            }

            s1 = "TraceId : " + MDC.get(BaggageFields.SPAN_ID.name()) + "," + MDC.get(BaggageFields.TRACE_ID.name()) + "\n环境：" + this.profile + "\n" + s1;
            JSONObject msg = new JSONObject();
            msg.put("msgtype", "text");
            JSONObject text = new JSONObject();
            text.put("content", s1);
            msg.put("text", text);
            String jsonStr = msg.toJSONString();
            log.info("send ding talk text json: {}", jsonStr);
            HttpUtil.post(s, jsonStr);
        } catch (Exception var7) {
            log.error("消息发送失败:" + var7.getMessage(), var7);
        }

        return ResponseBean.success();
    }

    @Override
    public ResponseBean<String> sendText(String s, String s1, List<String> list) {
        return null;
    }

    @Override
    public ResponseBean<String> sendMarkdown(String s, String s1) {
        return null;
    }

    @Override
    public ResponseBean<String> sendImage(String s, ByteData byteData) {
        return null;
    }
}
