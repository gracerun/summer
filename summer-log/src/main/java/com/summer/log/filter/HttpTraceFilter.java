package com.summer.log.filter;

import com.summer.log.util.NetworkUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 请求日志
 *
 * @author Tom
 * @version 1.0.0
 * @date 4/27/22
 */
@Slf4j
public class HttpTraceFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        long start = System.currentTimeMillis();
        final String ipAddress = NetworkUtil.getIpAddress(request);
        final String method = request.getMethod();
        final String requestURI = request.getRequestURI();
        final String protocol = request.getProtocol();
        log.info("{} {} {} {} ", ipAddress, method, requestURI, protocol);
        filterChain.doFilter(request, response);
        long end = System.currentTimeMillis();
        log.info("{} {} {} {} {} {}", ipAddress, method, requestURI, protocol, response.getStatus(), (end - start));
    }

}
