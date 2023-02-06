package cn.happyselect.sys.config.redis;

import org.springframework.session.web.http.HttpSessionIdResolver;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 聚合sessionId解析器(支持从请求头获取token与cookie任意一个)
 *
 * @author adc
 * @version 1.0.0
 * @date 6/1/21
 */

public class MultiHttpSessionIdResolver implements HttpSessionIdResolver {

    private static final List<HttpSessionIdResolver> RESOLVERS = new ArrayList<>();

    public void add(HttpSessionIdResolver resolver) {
        if (Objects.nonNull(resolver)) {
            RESOLVERS.add(resolver);
        }
    }

    @Override
    public List<String> resolveSessionIds(HttpServletRequest request) {
        if (CollectionUtils.isEmpty(RESOLVERS)) {
            return Collections.emptyList();
        }

        for (HttpSessionIdResolver e : RESOLVERS) {
            List<String> ids = e.resolveSessionIds(request);
            if (!CollectionUtils.isEmpty(ids)) {
                return ids;
            }
        }
        return Collections.emptyList();
    }

    @Override
    public void setSessionId(HttpServletRequest request, HttpServletResponse response, String sessionId) {
        if (CollectionUtils.isEmpty(RESOLVERS)) {
            return;
        }
        for (HttpSessionIdResolver e : RESOLVERS) {
            e.setSessionId(request, response, sessionId);
        }
    }

    @Override
    public void expireSession(HttpServletRequest request, HttpServletResponse response) {
        if (CollectionUtils.isEmpty(RESOLVERS)) {
            return;
        }
        for (HttpSessionIdResolver e : RESOLVERS) {
            e.expireSession(request, response);
        }
    }
}
