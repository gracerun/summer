package com.gracerun.log.filter;

import com.gracerun.log.util.NetworkUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 请求地址与响应码日志
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
        filterChain.doFilter(request, response);
        long end = System.currentTimeMillis();
        log.info("{} {} {} {} {} {}", NetworkUtil.getIpAddress(request),
                request.getMethod(), request.getRequestURI(), request.getProtocol(),
                response.getStatus(), (end - start));
    }

}
