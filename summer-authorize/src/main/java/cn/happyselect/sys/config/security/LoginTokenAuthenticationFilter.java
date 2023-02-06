package cn.happyselect.sys.config.security;

import cn.happyselect.base.enums.ClientType;
import cn.happyselect.sys.bean.dto.LoginToken;
import cn.happyselect.sys.service.CaptchaBizService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gracerun.util.ValidationResult;
import com.gracerun.util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * 自定义json请求登录
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-09-10
 */
@Slf4j
public class LoginTokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private boolean postOnly = true;
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private CaptchaBizService captchaBizService;

    public LoginTokenAuthenticationFilter() {
        super(new AntPathRequestMatcher("/login", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        try (InputStream is = request.getInputStream()) {
            LoginToken authRequest = mapper.readValue(is, LoginToken.class);
            validateParam(authRequest);
            if (!ClientType.AGENT_APP.name().equals(authRequest.getClientType())) {
                captchaBizService.validImageCaptcha(authRequest.getPcId(), authRequest.getPcCode());
            }
            return this.getAuthenticationManager().authenticate(authRequest);
        } catch (IllegalArgumentException e) {
            throw new AuthenticationServiceException(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new AuthenticationServiceException("登录参数错误");
        }
    }

    /**
     * 校验参数
     *
     * @param request
     */
    private <S> void validateParam(S request) {
        ValidationResult result = ValidationUtil.validateEntity(request);
        if (result.isHasErrors()) {
            StringBuilder sbd = new StringBuilder();
            result.getErrorMsg().forEach((k, v) -> sbd.append(k).append(v).append("&"));
            throw new AuthenticationServiceException(sbd.deleteCharAt(sbd.length() - 1).toString());
        }
    }

    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

}
