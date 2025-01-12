package com.gracerun.component.service;

import com.gracerun.tool.bean.ByteData;
import com.gracerun.tool.bean.ResponseBean;

import java.util.List;

/**
 * Copyright (C), 2010-2020, china payment. Co., Ltd.
 * <p>
 * 报警Service
 *
 * @author lowrie
 * @version 1.0.0
 * @date 2020/7/7
 */
public interface ReportService {

    /**
     * 发送text格式报警消息
     *
     * @param url     地址
     * @param content 内容
     * @return
     */
    ResponseBean<String> sendText(String url, String content);


    /**
     * 发送text格式报警信息@用户
     *
     * @param url
     * @param content
     * @param atPhones
     * @return
     */
    ResponseBean<String> sendText(String url, String content, List<String> atPhones);

    /**
     * 发送markdown格式报警消息
     *
     * @param url
     * @param content
     * @return
     */
    ResponseBean<String> sendMarkdown(String url, String content);

    /**
     * 发送图片格式
     *
     * @param url
     * @param data
     * @return
     */
    ResponseBean<String> sendImage(String url, ByteData data);


}
