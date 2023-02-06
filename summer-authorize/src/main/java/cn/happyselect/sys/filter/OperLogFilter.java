package cn.happyselect.sys.filter;

import cn.happyselect.base.bean.dto.UserContext;
import cn.happyselect.sys.config.redis.RedisSessionConfig;
import cn.happyselect.sys.entity.OperLog;
import cn.happyselect.sys.service.OperLogServiceImpl;
import cn.happyselect.sys.util.UserHolder;
import com.gracerun.log.core.TracerHolder;
import com.gracerun.log.util.NetworkUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 操作日志过滤器
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-08-27
 */
@Slf4j
public class OperLogFilter extends OncePerRequestFilter {
    private static final int BUFFER_SIZE = 1024;
    public static final String SPACE = " ";
    public static final String COLON = ": ";
    public static final String SEMICOLON = "; ";
    public static final String ANY = "****";
    public static final String EQ = "=";
    public static final String COOKIE = "cookie";
    public static final String SESSION_NAME = "SESSION";

    private OperLogServiceImpl operLogService;

    protected AntPathMatcher pathMatcher = new AntPathMatcher();
    protected UrlPathHelper urlPathHelper = new UrlPathHelper();

    protected volatile Set<String> exclusions;

    public void setExclusions(String urls) {
        Set<String> tmpExclusions = new HashSet<>();
        if (StringUtils.hasText(urls)) {
            String[] split = urls.split(",");
            for (String url : split) {
                tmpExclusions.add(url);
            }
        }
        exclusions = tmpExclusions;
    }

    public OperLogFilter(OperLogServiceImpl operLogService) {
        this.operLogService = operLogService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String url = urlPathHelper.getPathWithinApplication(request);
        for (String pattern : exclusions) {
            log.trace("Attempting to match pattern '{}' with current url '{}'...", pattern, url);
            if (pathMatcher.match(pattern, url)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        SaveRequestWrapper saveRequestWrapper = new SaveRequestWrapper(request);
        SaveResponseWrapper saveResponseWrapper = new SaveResponseWrapper(response);

        String req = getRequestContent(saveRequestWrapper);

        long start = System.currentTimeMillis();
        filterChain.doFilter(saveRequestWrapper, saveResponseWrapper);
        long responseTime = System.currentTimeMillis() - start;

        String resp = getResponseContent(request, response, saveResponseWrapper);

        saveOperLog(NetworkUtil.getIpAddress(request), url, req, resp, responseTime);
    }

    private String getResponseContent(HttpServletRequest request, HttpServletResponse response, SaveResponseWrapper saveResponseWrapper) {
        StringBuilder resp = new StringBuilder(BUFFER_SIZE);
        resp.append(request.getProtocol()).append(SPACE).append(response.getStatus()).append(System.lineSeparator());
        saveResponseWrapper.getHeader().forEach((key, value) ->
                resp.append(key).append(COLON).append(value).append(System.lineSeparator())
        );
        resp.append(System.lineSeparator());
        resp.append(saveResponseWrapper.getBody());
        return resp.toString();
    }

    private String getRequestContent(SaveRequestWrapper saveRequestWrapper) {
        StringBuilder req = new StringBuilder(BUFFER_SIZE);
        req.append(saveRequestWrapper.getMethod()).append(SPACE).append(saveRequestWrapper.getRequestURI()).append(SPACE)
                .append(saveRequestWrapper.getProtocol()).append(System.lineSeparator());
        Enumeration<String> headerNames = saveRequestWrapper.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String s = headerNames.nextElement();
            //处理cookie
            if (COOKIE.equalsIgnoreCase(s)) {
                Cookie[] cookies = saveRequestWrapper.getCookies();
                if (cookies != null) {
                    StringBuilder ck = new StringBuilder(128);
                    for (Cookie cookie : cookies) {
                        if (SESSION_NAME.equals(cookie.getName())) {
                            ck.append(SESSION_NAME).append(EQ).append(ANY).append(SEMICOLON);
                        } else {
                            ck.append(cookie.getName()).append(EQ).append(cookie.getValue()).append(SEMICOLON);
                        }
                    }
                    req.append(s).append(COLON).append(ck.substring(0, ck.length() - 2)).append(System.lineSeparator());
                }
            } else if (RedisSessionConfig.HEADER_X_AUTH_TOKEN.equalsIgnoreCase(s)) {
                req.append(s).append(COLON).append(ANY).append(System.lineSeparator());
            } else {
                req.append(s).append(COLON).append(saveRequestWrapper.getHeader(s)).append(System.lineSeparator());
            }
        }
        req.append(System.lineSeparator());

        String body = saveRequestWrapper.getBody();
        if (Objects.nonNull(body)) {
            req.append(body).append(System.lineSeparator());
        }
        return req.toString();
    }

    private void saveOperLog(String requestIp, String requestURI, String reqString, String respString, long responseTime) {
        UserContext currentUserContext = UserHolder.getCurrentUser();
        OperLog operLog = new OperLog();
        operLog.setRequestIp(requestIp);
        operLog.setRequest(reqString);
        operLog.setTraceId(TracerHolder.getSanToString());
        operLog.setUrl(requestURI);
        if (currentUserContext != null) {
            operLog.setUserId(currentUserContext.getUserId());
            operLog.setUserIdentifier(currentUserContext.getIdentifier());
            operLog.setNickname(currentUserContext.getNickname());
            operLog.setSystemType(currentUserContext.getSystemType().name());
        }

        operLog.setTime(new Long(responseTime).intValue());
        operLog.setResponse(respString);
        try {
            operLogService.saveOperLog(operLog);
        } catch (Exception e) {
            log.error("{}", operLog);
            log.error(e.getMessage(), e);
        }
    }

}


