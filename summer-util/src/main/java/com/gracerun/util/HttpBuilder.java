package com.gracerun.util;

import ch.qos.logback.classic.Level;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class HttpBuilder {

    private static XmlMapper XML_MAPPER = new XmlMapper();

    static {
        XML_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
        XML_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        XML_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static Builder create(final String method) {
        return new Builder(method);
    }

    public static Builder post(String url) {
        return new Builder(HttpPost.METHOD_NAME, url);
    }

    public static Builder get(String url) {
        return new Builder(HttpGet.METHOD_NAME, url);
    }

    public static Builder put(String url) {
        return new Builder(HttpPut.METHOD_NAME, url);
    }

    public static Builder delete(String url) {
        return new Builder(HttpDelete.METHOD_NAME, url);
    }

    public static Builder head(String url) {
        return new Builder(HttpHead.METHOD_NAME, url);
    }

    public static Builder options(String url) {
        return new Builder(HttpOptions.METHOD_NAME, url);
    }

    public static Builder patch(String url) {
        return new Builder(HttpPatch.METHOD_NAME, url);
    }

    public static Builder trace(String url) {
        return new Builder(HttpTrace.METHOD_NAME, url);
    }

    public static class Builder {

        private String method;

        private String url;

        private Level level;

        private String downloadPath;

        private RequestConfig config;

        private Map<String, String> header;

        private HttpEntity entity;

        Builder() {
            super();
        }

        Builder(final String method) {
            super();
            this.method = method;
        }

        Builder(final String method, final String url) {
            super();
            this.method = method;
            this.url = url;
        }

        public Builder setFormParam(final Map<String, String> param) {
            if (!CollectionUtils.isEmpty(param)) {
                ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
                param.forEach((key, value) -> {
                    list.add(new BasicNameValuePair(key, value));
                });
                this.entity = new UrlEncodedFormEntity(list, Consts.UTF_8);
            }
            return this;
        }

        public Builder setJsonParam(final Object param) {
            if (param != null) {
                String jsonStr;
                if (param instanceof String) {
                    jsonStr = (String) param;
                } else {
                    jsonStr = JSONObject.toJSONString(param);
                }
                this.entity = new StringEntity(jsonStr, ContentType.APPLICATION_JSON);
            }
            return this;
        }

        public Builder setXmlParam(final Object param) throws JsonProcessingException {
            if (param != null) {
                String xmlStr;
                if (param instanceof String) {
                    xmlStr = (String) param;
                } else {
                    xmlStr = XML_MAPPER.writeValueAsString(param);
                }
                this.entity = new StringEntity(xmlStr, ContentType.create("application/xml", Consts.UTF_8));
            }
            return this;
        }

        public Builder setFileParam(final Map<String, String> param, final Map<String, String> fileParam) {
            MultipartEntityBuilder multipartBuilder = MultipartEntityBuilder.create();

            if (!CollectionUtils.isEmpty(param)) {
                param.forEach((key, value) -> {
                    multipartBuilder.addTextBody(key, value, ContentType.create("text/plain", Consts.UTF_8));
                });
            }
            if (!CollectionUtils.isEmpty(fileParam)) {
                fileParam.forEach((key, value) -> {
                    multipartBuilder.addBinaryBody(key, new File(value));
                });
            }
            multipartBuilder.setMode(HttpMultipartMode.RFC6532);
            this.entity = multipartBuilder.build();
            return this;
        }

        public Builder setDownloadPath(final String downloadPath) {
            Assert.hasText(downloadPath, "下载路径不能为空");
            this.downloadPath = downloadPath;
            return this;
        }

        public Builder setMethod(final String method) {
            this.method = method;
            return this;
        }

        public Builder setUrl(final String url) {
            this.url = url;
            return this;
        }

        public Builder setLevel(final Level level) {
            this.level = level;
            return this;
        }

        public Builder setConfig(final RequestConfig config) {
            this.config = config;
            return this;
        }

        public Builder setHeader(final Map<String, String> header) {
            this.header = header;
            return this;
        }

        public String execute() throws IOException {
            if (StringUtils.hasText(downloadPath)) {
                return execute(new HttpUtil.StreamResponseHandler(downloadPath));
            } else {
                return execute(HttpUtil.DEFAULT_HANDLER);
            }
        }

        public <T> T execute(final ResponseHandler<? extends T> responseHandler) throws IOException {
            RequestBuilder requestBuilder = RequestBuilder.create(method);
            Assert.hasText(url, "url may not be empty");
            requestBuilder.setUri(url);
            if (!CollectionUtils.isEmpty(header)) {
                header.forEach((key, value) -> {
                    if (StringUtils.hasText(key)) {
                        requestBuilder.addHeader(key, value);
                    }
                });
            }

            if (config != null) {
                requestBuilder.setConfig(config);
            }

            if (entity != null) {
                if (HttpGet.METHOD_NAME.equalsIgnoreCase(method)) {
                    String paramStr = EntityUtils.toString(entity);

                    if (url.contains("?")) {
                        requestBuilder.setUri(url + "&" + paramStr);
                    } else {
                        requestBuilder.setUri(url + "?" + paramStr);
                    }
                } else {
                    requestBuilder.setEntity(entity);
                }
            }

            return HttpUtil.execute(requestBuilder, responseHandler, level);
        }

    }
}
