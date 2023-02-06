package cn.happyselect.sys.config.security;

import cn.happyselect.base.bean.dto.UserContext;
import cn.happyselect.base.enums.ClientType;
import cn.happyselect.base.enums.SystemType;
import cn.happyselect.base.enums.UserType;
import cn.happyselect.sys.bean.dto.LoginToken;
import cn.happyselect.sys.constant.LoginTypeConstant;
import cn.happyselect.sys.constant.RedisKeyConstant;
import cn.happyselect.sys.constant.UserStatusConstant;
import cn.happyselect.sys.entity.User;
import cn.happyselect.sys.mapper.UserMapper;
import cn.happyselect.sys.mapper.UserRoleMapper;
import cn.happyselect.sys.service.CaptchaBizService;
import cn.happyselect.sys.service.UserContextInterface;
import cn.happyselect.sys.service.UserServiceImpl;
import cn.happyselect.sys.util.PasswordUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 自定义登录验证
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-10-15
 */
@Slf4j
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private CaptchaBizService captchaBizService;

    @Autowired(required = false)
    private List<UserContextInterface> userContextInterfaceList;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        LoginToken loginToken = (LoginToken) authentication;
        UserContext userContext = selectLoginUser(loginToken);

        boolean matches;
        if (LoginTypeConstant.PASSWORD.equals(loginToken.getLoginType())) {
            matches = PasswordUtil.matches(loginToken.getPassword(), userContext.getPassword());
        } else if (LoginTypeConstant.SMS_CODE.equals(loginToken.getLoginType())) {
            matches = captchaBizService.validShortMsg(loginToken, loginToken.getSmsCode());
        } else {
            throw new AuthenticationServiceException("登录类型错误");
        }

        if (!matches) {
            incrementLoginFailAttempts(userContext.getUserId());
            if (LoginTypeConstant.PASSWORD.equals(loginToken.getLoginType())) {
                throw new BadCredentialsException("账号或密码错误");
            } else if (LoginTypeConstant.SMS_CODE.equals(loginToken.getLoginType())) {
                throw new BadCredentialsException("短信验证码错误");
            }
        } else {
            userContext.setAuthenticated(true);
            userService.clearCache(userContext.getUserId());
        }
        return userContext;
    }

    /**
     * 增加登录错误次数
     *
     * @param userId
     */
    protected void incrementLoginFailAttempts(String userId) {
        BoundValueOperations<String, String> valueOps = stringRedisTemplate.boundValueOps(RedisKeyConstant.LOGIN_FAIL_ATTEMPTS + userId);
        long increment = valueOps.increment();
        if (increment <= 1) {
            valueOps.expire(12, TimeUnit.HOURS);
        }
        boolean locked = lockedUserCheck(userId, increment);
        if (locked) {
            assertUserUnLocked(userId);
        }
    }

    /**
     * 规则：
     * 连续错误6次锁定1分钟,
     * 连续错误7次锁定5分钟,
     * 连续错误8次锁定15分钟,
     * 连续错误9次锁定60分钟,
     * 连续错误10次永久锁定
     *
     * @author adc
     * @date 2020-08-14
     * @version 1.0.0
     */
    public boolean lockedUserCheck(String userId, long count) {
        log.info("user [{}] password error count [{}]", userId, count);
        BoundValueOperations<String, String> valueOps = stringRedisTemplate.boundValueOps(RedisKeyConstant.USER_LOCKED + userId);
        boolean locked = false;
        if (count == 6) {
            valueOps.set(UserStatusConstant.LOCKED, Duration.ofMinutes(1));
            locked = true;
        } else if (count == 7) {
            valueOps.set(UserStatusConstant.LOCKED, Duration.ofMinutes(5));
            locked = true;
        } else if (count == 8) {
            valueOps.set(UserStatusConstant.LOCKED, Duration.ofMinutes(15));
            locked = true;
        } else if (count == 9) {
            valueOps.set(UserStatusConstant.LOCKED, Duration.ofMinutes(60));
            locked = true;
        } else if (count >= 10) {
            User t = new User();
            t.setStatus(UserStatusConstant.LOCKED);
            LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
            query.eq(User::getUserId, userId);
            userMapper.update(t, query);
            userService.clearCache(userId);
            locked = true;
        }
        return locked;
    }

    protected void assertUserUnLocked(String userId) {
        BoundValueOperations<String, String> valueOps = stringRedisTemplate.boundValueOps(RedisKeyConstant.USER_LOCKED + userId);

        String status = valueOps.get();
        if (UserStatusConstant.LOCKED.equals(status)) {
            Long ttl = valueOps.getExpire();
            if (Objects.nonNull(ttl) && ttl.longValue() > 0) {
                String timeMsg;
                if (ttl / 60 > 0) {
                    timeMsg = String.format("，请%s分钟后再输入一次", (ttl + (60 - 1)) / 60);
                } else {
                    timeMsg = String.format("，请%s秒后再输入一次", ttl);
                }
                throw new LockedException("账户已被锁定" + timeMsg);
            } else {
                throw new LockedException("账户已被锁定");
            }
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return LoginToken.class.isAssignableFrom(authentication);
    }

    public UserContext selectLoginUser(LoginToken loginToken) {
        ClientType clientType = ClientType.resolve(loginToken.getClientType());
        SystemType systemType = ClientType.resolveSystemType(loginToken.getClientType());
        Assert.notNull(systemType, "系统类型错误");
        String identifierType = userService.findIdentifierType(loginToken.getUsername());
        User t = userService.findByIdentifier(systemType, loginToken.getUsername());
        if (t == null) {
            throw new UsernameNotFoundException("账号或密码错误");
        }

        assertUserUnLocked(t.getUserId());

        if (UserStatusConstant.LOCKED.equals(t.getStatus())) {
            throw new LockedException("账号已被锁定");
        } else if (UserStatusConstant.FALSE.equals(t.getStatus())) {
            throw new DisabledException("账号已停用");
        }

        List<String> roleCodes = userRoleMapper.selectByUserId(t.getUserId());

        List<SimpleGrantedAuthority> authorities = null;
        if (!CollectionUtils.isEmpty(roleCodes)) {
            authorities = roleCodes.stream().map(role ->
                    new SimpleGrantedAuthority(role)).collect(Collectors.toList());
        }

        UserContext userContext = new UserContext(authorities);
        BeanUtil.copyProperties(t, userContext, CopyOptions.create().ignoreNullValue());

        userContext.setSystemType(SystemType.resolve(t.getSystemType()));
        userContext.setUserType(UserType.resolve(t.getUserType()));
        userContext.setLoginType(loginToken.getLoginType());
        userContext.setIdentityType(identifierType);
        userContext.setIdentifier(loginToken.getUsername());
        userContext.setRoles(roleCodes);
        userContext.setClientType(clientType);

        if (LoginTypeConstant.PASSWORD.equals(loginToken.getLoginType())
                && new Date().after(t.getExpireTime())) {
            userContext.setExpireStatus(true);
        } else {
            userContext.setExpireStatus(false);
        }

        if (UserType.EMPLOYEE.name().equalsIgnoreCase(t.getUserType())) {
            User parentUser = userService.selectByUserId(t.getParentUserId());
            userContext.setBusinessNo(parentUser.getBusinessNo());
        }

        if (!CollectionUtils.isEmpty(userContextInterfaceList)) {
            for (UserContextInterface e : userContextInterfaceList) {
                e.load(userContext);
            }
        }

        return userContext;
    }
}
