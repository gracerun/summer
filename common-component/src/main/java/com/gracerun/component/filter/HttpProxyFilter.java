package com.gracerun.component.filter;

import com.gracerun.component.util.HttpUtil;
import com.gracerun.tool.constant.CharsetConstant;
import com.gracerun.tool.constant.HttpProxyConstant;
import com.gracerun.tool.exception.HttpClientConnectTimeoutException;
import com.gracerun.tool.exception.HttpClientSocketTimeoutException;
import com.gracerun.tool.util.HttpServletRequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.StatusLine;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * http代理
 *
 * @author adc
 */
@Slf4j
public class HttpProxyFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String PROXY_HEADER_NAME = "Proxy-Connection";
    public static final String KEEP_ALIVE = "Keep-Alive";
    public static final String QUESTION_MARK = "?";

    private static final RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(60000)
            .setConnectTimeout(2000).setConnectionRequestTimeout(2000).build();

    /**
     * 排除请求头
     */
    private static final Set<String> EXCLUDE_REQUEST_HEADER_NAMES = new HashSet<>();

    /**
     * 排除响应头
     */
    private static final Set<String> EXCLUDE_RESPONSE_HEADER_NAMES = new HashSet<>();

    static {
        EXCLUDE_REQUEST_HEADER_NAMES.add(HttpHeaders.CONTENT_LENGTH.toLowerCase());
        EXCLUDE_REQUEST_HEADER_NAMES.add(HttpHeaders.HOST.toLowerCase());
        EXCLUDE_REQUEST_HEADER_NAMES.add(HttpHeaders.CONNECTION.toLowerCase());
        EXCLUDE_REQUEST_HEADER_NAMES.add(PROXY_HEADER_NAME.toLowerCase());
        EXCLUDE_REQUEST_HEADER_NAMES.add(HttpProxyConstant.ORIGIN_SERVER_URI.toLowerCase());

        EXCLUDE_RESPONSE_HEADER_NAMES.add(HttpHeaders.CONTENT_LENGTH.toLowerCase());
        EXCLUDE_RESPONSE_HEADER_NAMES.add(HttpHeaders.TRANSFER_ENCODING.toLowerCase());
        EXCLUDE_RESPONSE_HEADER_NAMES.add(HttpHeaders.CONNECTION.toLowerCase());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (!isProxyRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        logger.info("------------accept proxy request begin-----------");
        String protocol = request.getProtocol();
        StringBuffer requestURL = request.getRequestURL();
        String method = request.getMethod();
        logger.info("{} {} {}", method, requestURL, protocol);
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String s = headerNames.nextElement();
            logger.info("{}: {}", s, request.getHeader(s));
        }
        logger.info("");

        String targetUrl = getTargetUrl(request, requestURL);
        ContentType contentType = HttpServletRequestUtil.parseContentType(request);
        HttpEntity httpEntity = HttpServletRequestUtil.readRequestBody(request, contentType);
        if (httpEntity != null) {
            logger.info("{}", EntityUtils.toString(httpEntity, contentType.getCharset()));
        }
        logger.info("------------accept proxy request end------------");
        logger.info("");
        sendTargetRequest(request, response, method, targetUrl, httpEntity);
    }

    private String getTargetUrl(HttpServletRequest request, StringBuffer requestURL) {
        String targetUrl;
        String originServerUri = request.getHeader(HttpProxyConstant.ORIGIN_SERVER_URI);
        if (StringUtils.hasText(originServerUri)) {
            targetUrl = originServerUri;
        } else {
            String queryString = request.getQueryString();
            if (StringUtils.hasText(queryString)) {
                targetUrl = requestURL.append(QUESTION_MARK).append(queryString).toString();
            } else {
                targetUrl = requestURL.toString();
            }
        }
        return targetUrl;
    }

    /**
     * 判断是否是代理请求
     *
     * @param request
     * @return
     */
    private boolean isProxyRequest(HttpServletRequest request) {
        String header = request.getHeader(PROXY_HEADER_NAME);
        String originServerUri = request.getHeader(HttpProxyConstant.ORIGIN_SERVER_URI);
        return KEEP_ALIVE.equals(header) || StringUtils.hasText(originServerUri);
    }

    private void sendTargetRequest(HttpServletRequest request, HttpServletResponse response, String method, String targetUrl, HttpEntity httpEntity) throws IOException {
        RequestBuilder requestBuilder = RequestBuilder.create(method);
        requestBuilder.setUri(targetUrl);
        if (httpEntity != null) {
            requestBuilder.setEntity(httpEntity);
        }

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String s = headerNames.nextElement();
            if (!EXCLUDE_REQUEST_HEADER_NAMES.contains(s.toLowerCase())) {
                requestBuilder.addHeader(s, request.getHeader(s));
            }
        }

        requestBuilder.setConfig(defaultRequestConfig);
        ResponseHandler<String> responseHandler = httpResponse -> {
            StatusLine statusLine = httpResponse.getStatusLine();
            logger.info("<< {}", statusLine);
            Header[] resAllHeaders = httpResponse.getAllHeaders();
            for (Header h : resAllHeaders) {
                if (!EXCLUDE_RESPONSE_HEADER_NAMES.contains(h.getName().toLowerCase())) {
                    logger.info("<< {}", h);
                    response.setHeader(h.getName(), h.getValue());
                }
            }

            //将响应数据返回给客户端
            response.setStatus(statusLine.getStatusCode());
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {
                long contentLength = entity.getContentLength();

                ByteArrayOutputStream out = null;
                if (contentLength > 0) {
                    out = new ByteArrayOutputStream((int) contentLength);
                } else {
                    out = new ByteArrayOutputStream();
                }
                entity.writeTo(out);
                byte[] bytes = out.toByteArray();
                logger.info("<< ");
                logger.info("<< {}", new String(bytes, CharsetConstant.UTF_8));

                ServletOutputStream outputStream = response.getOutputStream();
                outputStream.write(bytes);
                outputStream.flush();
            }
            return null;
        };

        try {
            HttpUtil.execute(requestBuilder, responseHandler);
        } catch (HttpClientConnectTimeoutException e) {
            logger.error(e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_BAD_GATEWAY, e.toString());
        } catch (HttpClientSocketTimeoutException e) {
            logger.error(e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_GATEWAY_TIMEOUT, e.toString());
        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_BAD_GATEWAY, e.toString());
        }

    }

}


