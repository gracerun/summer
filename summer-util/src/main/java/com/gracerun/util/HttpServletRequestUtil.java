package com.gracerun.util;

import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpServletRequestUtil {
    public static final int BUFFER_SIZE = 2 * 1024;

    public static ContentType parseContentType(HttpServletRequest request) {
        if (!StringUtils.hasText(request.getContentType())) {
            return ContentType.DEFAULT_TEXT.withCharset(Consts.UTF_8);
        }
        ContentType contentType = ContentType.parse(request.getContentType());

        if (contentType != null) {
            if (contentType.getCharset() == null) {
                contentType = contentType.withCharset(Consts.UTF_8);
            }
        } else {
            contentType = ContentType.DEFAULT_TEXT.withCharset(Consts.UTF_8);
        }
        return contentType;
    }

    public static HttpEntity readRequestBody(HttpServletRequest request, ContentType contentType) throws IOException {
        HttpEntity entity;
        if (ContentType.APPLICATION_FORM_URLENCODED.getMimeType().equals(contentType.getMimeType())) {
            ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
            Map<String, String[]> parameterMap = request.getParameterMap();
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                String[] value = entry.getValue();
                if (!ObjectUtils.isEmpty(value)) {
                    for (int i = 0; i < value.length; i++) {
                        list.add(new BasicNameValuePair(entry.getKey(), value[i]));
                    }
                } else {
                    list.add(new BasicNameValuePair(entry.getKey(), null));
                }
            }
            entity = new UrlEncodedFormEntity(list, contentType.getCharset());
        } else {
            StringBuilder httpBody = new StringBuilder(BUFFER_SIZE);
            ServletInputStream inputStream = request.getInputStream();
            List<String> lines = IOUtils.readLines(inputStream, contentType.getCharset());
            for (String s : lines) {
                if (httpBody.length() > 0) {
                    httpBody.append(System.lineSeparator());
                }
                httpBody.append(s);
            }
            entity = new StringEntity(httpBody.toString(), contentType);
        }
        return entity;
    }
}
