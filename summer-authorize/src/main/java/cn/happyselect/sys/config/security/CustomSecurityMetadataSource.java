package cn.happyselect.sys.config.security;

import cn.happyselect.sys.service.RolePrivilegeQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 自定义动态权限
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-10-15
 */
@Slf4j
@Component
public class CustomSecurityMetadataSource implements FilterInvocationSecurityMetadataSource, MessageListener, ApplicationRunner {

    private FilterInvocationSecurityMetadataSource superMetadataSource;

    @Autowired
    private RolePrivilegeQueryService rolePrivilegeQueryService;

    private volatile Map<RequestMatcher, Collection<ConfigAttribute>> requestMap;

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        final HttpServletRequest request = ((FilterInvocation) object).getRequest();
        for (Map.Entry<RequestMatcher, Collection<ConfigAttribute>> entry : requestMap.entrySet()) {
            if (entry.getKey().matches(request)) {
                return entry.getValue();
            }
        }
        return superMetadataSource.getAttributes(object);
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        Set<ConfigAttribute> allAttributes = new HashSet<>();
        for (Map.Entry<RequestMatcher, Collection<ConfigAttribute>> entry : requestMap.entrySet()) {
            allAttributes.addAll(entry.getValue());
        }
        return allAttributes;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return FilterInvocation.class.isAssignableFrom(aClass);
    }

    public void setSuperMetadataSource(FilterInvocationSecurityMetadataSource superMetadataSource) {
        this.superMetadataSource = superMetadataSource;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        reloadRequestMap();
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("handleMessage:{}", message);
        reloadRequestMap();
    }

    public void reloadRequestMap() {
        Map<RequestMatcher, Collection<ConfigAttribute>> newRequestMap = new HashMap<>(128);
        Map<String, List<String>> allRoleUrlPrivilege = rolePrivilegeQueryService.findAllRoleUrlPrivilege();
        for (Map.Entry<String, List<String>> entry : allRoleUrlPrivilege.entrySet()) {
            newRequestMap.put(new AntPathRequestMatcher(entry.getKey(), null, true, new UrlPathHelper()),
                    entry.getValue().stream().map(s -> new SecurityConfig(s)).collect(Collectors.toList()));
        }
        requestMap = newRequestMap;
    }
}
