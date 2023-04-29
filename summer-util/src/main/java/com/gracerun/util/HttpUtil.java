package com.gracerun.util;

import brave.baggage.BaggageFields;
import ch.qos.logback.classic.Level;
import com.gracerun.log.core.TracerHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.http.*;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.cloud.sleuth.CurrentTraceContext;
import org.springframework.cloud.sleuth.TraceContext;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.Map.Entry;

@Slf4j
public class HttpUtil {

    private static final ThreadLocal<Level> THREAD_LOCAL = ThreadLocal.withInitial(() -> Level.OFF);

    public static final StringResponseHandler DEFAULT_HANDLER = new StringResponseHandler();
    public static final OriginResponseHandler ORIGIN_HANDLER = new OriginResponseHandler();

    private static final String HTTP = "http";
    private static final String HTTPS = "https";
    private static SSLConnectionSocketFactory sslsf = null;
    private static PoolingHttpClientConnectionManager cm = null;
    private static SSLContextBuilder builder = null;
    private static CloseableHttpClient httpClient = null;

    private static final Set<String> EXCLUDE_HEADER_NAMES = new HashSet<>();

    static {
        EXCLUDE_HEADER_NAMES.add(BaggageFields.TRACE_ID.name());
        EXCLUDE_HEADER_NAMES.add(BaggageFields.SPAN_ID.name());
        EXCLUDE_HEADER_NAMES.add(BaggageFields.PARENT_ID.name());
        EXCLUDE_HEADER_NAMES.add(BaggageFields.SAMPLED.name());
        EXCLUDE_HEADER_NAMES.add("Date");
        EXCLUDE_HEADER_NAMES.add("Transfer-Encoding");
        EXCLUDE_HEADER_NAMES.add("Via");
        EXCLUDE_HEADER_NAMES.add("Connection");
        EXCLUDE_HEADER_NAMES.add("Cache-Control");
        EXCLUDE_HEADER_NAMES.add("Content-Language");
        EXCLUDE_HEADER_NAMES.add("Content-Language");
        EXCLUDE_HEADER_NAMES.add("Expires");
        EXCLUDE_HEADER_NAMES.add("Server");
        EXCLUDE_HEADER_NAMES.add("Vary");
    }

    static {
        try {
            builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    return true;
                }
            });
            sslsf = new SSLConnectionSocketFactory(builder.build(), new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            });
            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create().register(HTTP, new PlainConnectionSocketFactory()).register(HTTPS, sslsf).build();
            cm = new PoolingHttpClientConnectionManager(registry);
            cm.setMaxTotal(200);
            cm.setDefaultMaxPerRoute(cm.getMaxTotal());
            // 连接一段时间不用后，再次使用时，先进行连接检测
            cm.setValidateAfterInactivity(20000);
            HttpClientBuilder httpClientBuilder = HttpClients.custom();
            httpClientBuilder.setSSLSocketFactory(sslsf);

            RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(3000).setConnectTimeout(2000).setConnectionRequestTimeout(2000).build();
            httpClientBuilder.setDefaultRequestConfig(defaultRequestConfig);
            httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(0, false));
            httpClient = httpClientBuilder.setConnectionManager(cm).setConnectionManagerShared(true).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public static String httpRequest(RequestBuilder requestBuilder, Map<String, String> header, RequestConfig config) throws IOException {
        if (!CollectionUtils.isEmpty(header)) {
            for (Entry<String, String> e : header.entrySet()) {
                if (StringUtils.hasText(e.getKey())) {
                    requestBuilder.addHeader(e.getKey(), e.getValue());
                }
            }
        }

        if (config != null) {
            requestBuilder.setConfig(config);
        }

        return execute(requestBuilder, DEFAULT_HANDLER);
    }

    public static <T> T execute(RequestBuilder requestBuilder, final ResponseHandler<? extends T> responseHandler) throws IOException {
        return execute(requestBuilder, responseHandler, Level.OFF);
    }

    public static <T> T execute(RequestBuilder requestBuilder, final ResponseHandler<? extends T> responseHandler, Level headerLogLevel) throws IOException {
        try {
            THREAD_LOCAL.set(headerLogLevel);
            HttpUtil.setTracerHeader(requestBuilder);
            HttpEntity entity = requestBuilder.getEntity();

            RequestConfig config = requestBuilder.getConfig();
            if (config != null) {
                HttpHost proxy = config.getProxy();
                if (proxy != null) {
                    log.info(">> proxy host: {}", proxy);
                }
            }
            HttpUriRequest request = requestBuilder.build();

            if (entity != null) {
                if (entity.getContentType() != null && !request.containsHeader(
                        HttpHeaders.CONTENT_TYPE)) {
                    request.addHeader(entity.getContentType());
                }
                if (entity.getContentEncoding() != null && !request.containsHeader(
                        HttpHeaders.CONTENT_ENCODING)) {
                    request.addHeader(entity.getContentEncoding());
                }
            }

            log.info(">> {}", request.getRequestLine());
            printHeader(">>", request.getAllHeaders());
            log.info(">> ");
            return httpClient.execute(request, responseHandler);
        } finally {
            THREAD_LOCAL.remove();
        }
    }

    private static void printHeader(String pre, Header[] allHeaders) {
        final Level level = THREAD_LOCAL.get();
        if (Level.DEBUG.equals(level)) {
            for (Header h : allHeaders) {
                log.debug("{} {}", pre, h);
            }
            log.debug("<< ");
        } else if (Level.INFO.equals(level)) {
            for (Header h : allHeaders) {
                if (!EXCLUDE_HEADER_NAMES.contains(h.getName())) {
                    log.info("{} {}", pre, h);
                }
            }
            log.info("<< ");
        }

    }

    public static void setTracerHeader(RequestBuilder requestBuilder) {
        final Tracer tracer = TracerHolder.getTracer();
        if (tracer != null) {
            final CurrentTraceContext currentTraceContext = tracer.currentTraceContext();
            TraceContext traceContext = currentTraceContext.context();
            if (traceContext != null) {
                requestBuilder.addHeader(BaggageFields.TRACE_ID.name(), traceContext.traceId());
                requestBuilder.addHeader(BaggageFields.SPAN_ID.name(), traceContext.spanId());
                requestBuilder.addHeader(BaggageFields.PARENT_ID.name(), traceContext.parentId());
            }
        }
    }

    public static class StringResponseHandler implements ResponseHandler<String> {
        @Override
        public String handleResponse(HttpResponse response) throws IOException {
            StatusLine statusLine = response.getStatusLine();
            log.info("<< {}", statusLine);
            printHeader("<<", response.getAllHeaders());
            HttpEntity entity = response.getEntity();
            String responseBody = null;
            if (entity != null) {
                responseBody = EntityUtils.toString(entity, Consts.UTF_8);
                log.info("<< {}", responseBody);
            }

            int status = statusLine.getStatusCode();
            if (status >= 200 && status < 300) {
                return responseBody;
            } else {
                EntityUtils.consume(entity);
                throw new HttpResponseException(status, statusLine.getReasonPhrase());
            }
        }
    }

    public static class StreamResponseHandler implements ResponseHandler<String> {

        private String downloadPath;

        public StreamResponseHandler(String downloadPath) {
            this.downloadPath = downloadPath;
        }

        @Override
        public String handleResponse(HttpResponse response) throws IOException {
            StatusLine statusLine = response.getStatusLine();
            log.info("<< {}", statusLine);
            printHeader("<<", response.getAllHeaders());

            HttpEntity entity = response.getEntity();
            int status = statusLine.getStatusCode();
            if (status >= 200 && status < 300) {
                if (entity != null) {
                    FileUtils.copyInputStreamToFile(entity.getContent(), new File(downloadPath));
                    log.info("<< {}下载成功", downloadPath);
                }
                return null;
            } else {
                EntityUtils.consume(entity);
                throw new HttpResponseException(status, statusLine.getReasonPhrase());
            }
        }
    }

    public static class OriginResponse implements Serializable {
        private String body;
        private int code;
        private List<Header> headers;

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public List<Header> getHeaders() {
            return headers;
        }

        public void setHeaders(List<Header> headers) {
            this.headers = headers;
        }

        public Header[] getHeaders(String name) {
            List<Header> headersFound = null;
            for (int i = 0; i < this.headers.size(); ++i) {
                Header header = this.headers.get(i);
                if (header.getName().equalsIgnoreCase(name)) {
                    if (headersFound == null) {
                        headersFound = new ArrayList();
                    }
                    headersFound.add(header);
                }
            }
            return headersFound != null ? headersFound.toArray(new Header[headersFound.size()]) : null;
        }
    }

    public static class OriginResponseHandler implements ResponseHandler<OriginResponse> {

        @Override
        public OriginResponse handleResponse(HttpResponse response) throws IOException {
            StatusLine statusLine = response.getStatusLine();
            log.info("<< {}", statusLine);
            printHeader("<<", response.getAllHeaders());
            OriginResponse originResponse = new OriginResponse();
            originResponse.setCode(response.getStatusLine().getStatusCode());
            originResponse.setHeaders(Arrays.asList(response.getAllHeaders()));
            HttpEntity entity = response.getEntity();
            if (null != entity) {
                String responseBody = EntityUtils.toString(entity, Consts.UTF_8);
                log.info("<< {}", responseBody);
                originResponse.setBody(responseBody);
            }
            return originResponse;
        }
    }

}
