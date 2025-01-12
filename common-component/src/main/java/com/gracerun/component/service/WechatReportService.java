package com.gracerun.component.service;

import brave.baggage.BaggageFields;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gracerun.component.util.HttpBuilder;
import com.gracerun.tool.bean.ByteData;
import com.gracerun.tool.bean.ResponseBean;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.client.config.RequestConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Base64Utils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Copyright (C), 2010-2020, china payment. Co., Ltd.
 * <p>
 * 使用企业微信发送报警消息
 *
 * @author lowrie
 * @version 1.0.0
 * @date 2020/7/7
 */
public class WechatReportService implements ReportService {

    @Value("${spring.profiles.active:}")
    private String profile;

    private static final Logger LOGGER = LoggerFactory.getLogger(WechatReportService.class);

    private static final RequestConfig REQ_CONFIG = RequestConfig.custom().setConnectTimeout(30000).setSocketTimeout(30000).setConnectionRequestTimeout(2000).build();

    @Override
    public ResponseBean<String> sendText(String url, String content) {
        return sendText(url, content, null);
    }

    @Override
    public ResponseBean<String> sendText(String url, String content, List<String> atPhones) {
        try {
            if (!StringUtils.hasText(content) || !StringUtils.hasText(url)) {
                return ResponseBean.success();
            }
            content = "TraceId : " + MDC.get(BaggageFields.SPAN_ID.name()) + "," + MDC.get(BaggageFields.TRACE_ID.name()) + "\n" +
                    "环境：" + this.profile + "\n" +
                    "" + content;
            JSONObject msg = new JSONObject();
            msg.put("msgtype", "text");
            JSONObject text = new JSONObject();
            text.put("content", content);
            if (!CollectionUtils.isEmpty(atPhones)) {
                JSONArray atPhonesJson = new JSONArray();
                if (!CollectionUtils.isEmpty(atPhones)) {
                    atPhonesJson.addAll(atPhones);
                }
                text.put("mentioned_mobile_list", atPhonesJson);
            }
            msg.put("text", text);

            String jsonStr = msg.toJSONString();
            LOGGER.info("send ding talk text json: {}", jsonStr);
            HttpUtil.post(url, jsonStr);
        } catch (Exception e) {
            LOGGER.error("消息发送失败:" + e.getMessage(), e);
        }
        return ResponseBean.success();
    }

    @Override
    public ResponseBean<String> sendMarkdown(String url, String content) {
        try {
            if (!StringUtils.hasText(url) || !StringUtils.hasText(content)) {
                return ResponseBean.success();
            }
            JSONObject msg = new JSONObject();
            msg.put("msgtype", "markdown");
            JSONObject text = new JSONObject();
            text.put("content", content);
            msg.put("markdown", text);
            String jsonStr = msg.toJSONString();
            LOGGER.info("send ding talk text json: {}", jsonStr);
            HttpBuilder.post(url).setJsonParam(jsonStr).setConfig(REQ_CONFIG).execute();
        } catch (Exception e) {
            LOGGER.error("消息发送失败:" + e.getMessage(), e);
        }
        return ResponseBean.success();
    }

    @Override
    public ResponseBean<String> sendImage(String url, ByteData data) {
        try {
            if (!StringUtils.hasText(url) || ArrayUtils.isEmpty(data.getByteData())) {
                return ResponseBean.success();
            }

            JSONObject msg = new JSONObject();
            msg.put("msgtype", "image");
            JSONObject text = new JSONObject();
            text.put("base64", Base64Utils.encodeToString(data.getByteData()));
            text.put("md5", DigestUtils.md5Hex(data.getByteData()));
            msg.put("image", text);
            String jsonStr = msg.toJSONString();
            HttpBuilder.post(url).setJsonParam(jsonStr).setConfig(REQ_CONFIG).execute();
        } catch (Exception e) {
            LOGGER.error("消息发送失败:" + e.getMessage(), e);
        }
        return ResponseBean.success();
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
