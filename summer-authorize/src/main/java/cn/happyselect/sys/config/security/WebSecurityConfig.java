package cn.happyselect.sys.config.security;

import cn.happyselect.sys.bean.ResponseBean;
import cn.happyselect.sys.bean.ResponseCode;
import cn.happyselect.sys.config.security.filter.ExpirePasswordFilter;
import com.alibaba.fastjson.JSON;
import com.gracerun.util.SpringProfilesUtil;
import org.apache.http.Consts;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class WebSecurityConfig<S extends Session> extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;

    @Autowired
    private CustomSecurityMetadataSource customSecurityMetadataSource;

    @Autowired
    private FindByIndexNameSessionRepository<S> sessionRepository;

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .antMatchers("/favicon.ico")
                .antMatchers("/error")
                .antMatchers("/websocket.html")
                .antMatchers("/captcha/**")
                .antMatchers("/iforgot/**")
                .antMatchers("/agent/register")
                .antMatchers("/app/findVersion")
                .antMatchers("/static/template/**");
        web.ignoring().antMatchers("/channel/notify");
        web.ignoring().antMatchers("/product/findByPage");

        web.ignoring()
                .antMatchers("/doc.html")
                .antMatchers("/webjars/**")
                .antMatchers("/swagger-resources")
                .antMatchers("/product/consumer/detail/**")
                .antMatchers("/payOrder/order")
                .antMatchers("/v2/**");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(customAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
//                .csrf().ignoringAntMatchers("/login").and()
                .csrf().disable()
                .cors().disable()
                .headers().disable()
                .exceptionHandling((exceptionHandling) -> exceptionHandling
                        .authenticationEntryPoint(new AuthenticationErrorHandler())
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                writeJson(response, ResponseCode.UN_AUTHORIZED, ResponseCode.UN_AUTHORIZED_CN)
                        ))
                .sessionManagement((sessionManagement) -> sessionManagement
                        .sessionFixation()
                        .newSession()
                        .withObjectPostProcessor(new ObjectPostProcessor<CompositeSessionAuthenticationStrategy>() {
                            @Override
                            public <O extends CompositeSessionAuthenticationStrategy> O postProcess(O o) {
                                return (O) sessionAuthenticationStrategy();
                            }
                        })
                        .maximumSessions(1)
                        .sessionRegistry(sessionRegistry())
                        .expiredSessionStrategy((event) ->
                                writeJson(event.getResponse(), ResponseCode.EXPIRED_LOGIN, ResponseCode.EXPIRED_LOGIN_CN)
                        ))
                .logout((logout) -> logout
                        .logoutSuccessHandler((request, response, authentication) ->
                                writeJson(response, ResponseCode.SUCCESS, ResponseCode.SUCCESS_CN)
                        ))
                .authorizeRequests((authorizeRequests) -> authorizeRequests
                        .anyRequest()
                        .authenticated()
                        .accessDecisionManager(accessDecisionManager())
                        .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                            @Override
                            public <O extends FilterSecurityInterceptor> O postProcess(O o) {
                                customSecurityMetadataSource.setSuperMetadataSource(o.getSecurityMetadataSource());
                                o.setSecurityMetadataSource(customSecurityMetadataSource);
                                return o;
                            }
                        }))
                .addFilterAt(customAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(new ExpirePasswordFilter()
                                .antMatchers("/current/user/update/password")
                                .antMatchers("/current/user/menu")
                                .antMatchers("/current/user/function")
                        , LoginTokenAuthenticationFilter.class);
//                .addFilterAfter(new VerifiedFilter()
//                                .antMatchers("/agent/verified")
//                                .antMatchers("/businessImg/self/uploadImage")
//                                .antMatchers("/cardBin/getCardBinBank")
//                                .antMatchers("/businessImg/uploadImage")
//                                .antMatchers("/branchBank/findByPage")
//                                .antMatchers("/current/user/info")
//                                .antMatchers("/agent/queryInfo")
//                                .antMatchers("/logout")
//                        , LoginTokenAuthenticationFilter.class);

    }

    class AuthenticationErrorHandler implements AuthenticationEntryPoint {

        protected AntPathRequestMatcher pathMatcher = new AntPathRequestMatcher("/websocket/**", null, true, new UrlPathHelper());

        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
            if (pathMatcher.matches(request)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
            writeJson(response, ResponseCode.NOT_LOGIN, ResponseCode.NOT_LOGIN_CN);
        }
    }

    private AccessDecisionManager accessDecisionManager() {
        List<AccessDecisionVoter<?>> decisionVoters = new ArrayList<>();
        decisionVoters.add(new WebExpressionVoter());
        RoleVoter roleVoter = new RoleVoter();
        roleVoter.setRolePrefix(grantedAuthorityDefaults().getRolePrefix());
        decisionVoters.add(roleVoter);
        return new AffirmativeBased(decisionVoters);
    }

    @Bean
    public LoginTokenAuthenticationFilter customAuthenticationFilter() throws Exception {
        LoginTokenAuthenticationFilter filter = new LoginTokenAuthenticationFilter();
        filter.setAuthenticationSuccessHandler((request, response, authentication) -> {
                    CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
                    if (csrfToken != null) {
                        response.setHeader(csrfToken.getHeaderName(), csrfToken.getToken());
                    }
                    writeJson(response, ResponseCode.SUCCESS, ResponseCode.SUCCESS_CN, null);
                    HttpSession session = request.getSession(false);
                    if (session == null) {
                        return;
                    }
                    session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
                }
        );
        filter.setAuthenticationFailureHandler((request, response, exception) ->
                writeJson(response, ResponseCode.BIZ_ERR, exception.getMessage(), null)
        );
        filter.setAuthenticationManager(authenticationManagerBean());
        filter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy());
        return filter;
    }

    @Bean
    public CompositeSessionAuthenticationStrategy sessionAuthenticationStrategy() {
        List<SessionAuthenticationStrategy> delegateStrategies = new ArrayList<>();
        SessionFixationProtectionStrategy sessionFixationProtectionStrategy = new SessionFixationProtectionStrategy();
        sessionFixationProtectionStrategy.setMigrateSessionAttributes(false);

        RegisterSessionAuthenticationStrategy registerSessionStrategy = new RegisterSessionAuthenticationStrategy(sessionRegistry());

        delegateStrategies.addAll(Arrays.asList(concurrentSessionControlStrategy(),
                sessionFixationProtectionStrategy, registerSessionStrategy));

        return new CompositeSessionAuthenticationStrategy(delegateStrategies);
    }

    @Bean
    public CustomConcurrentSessionControlAuthenticationStrategy concurrentSessionControlStrategy() {
        CustomConcurrentSessionControlAuthenticationStrategy concurrentSessionControlStrategy = new CustomConcurrentSessionControlAuthenticationStrategy(sessionRegistry());

        if (SpringProfilesUtil.isDev()
                || SpringProfilesUtil.isTest()) {
            concurrentSessionControlStrategy.setMaximumSessions(-1);
        } else {
            concurrentSessionControlStrategy.setMaximumSessions(1);
        }

        concurrentSessionControlStrategy.setExceptionIfMaximumExceeded(false);
        return concurrentSessionControlStrategy;
    }

    @Bean
    public SpringSessionBackedSessionRegistry<S> sessionRegistry() {
        return new SpringSessionBackedSessionRegistry<>(this.sessionRepository);
    }

    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }

    @Bean
    public RequestCache requestCache() {
        HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
        requestCache.setCreateSessionAllowed(false);
        return requestCache;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    public static void writeJson(ServletResponse response, String code, String msg) throws IOException {
        writeJson(response, code, msg, null);
    }

    public static void writeJson(ServletResponse response, String code, String msg, String data)
            throws IOException {
        response.setContentType(ContentType.APPLICATION_JSON.toString());
        ResponseBean<String> responseBean = new ResponseBean<>();
        responseBean.setCode(code);
        responseBean.setMsg(msg);
        responseBean.setData(data);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(response.getOutputStream(),
                Consts.UTF_8);
        outputStreamWriter.write(JSON.toJSONString(responseBean));
        outputStreamWriter.flush();
    }
}
