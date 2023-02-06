package cn.happyselect.sys.service;

import cn.happyselect.base.constants.StatusConstant;
import cn.happyselect.base.enums.SystemType;
import cn.happyselect.sys.bean.ShortMessageDtoInterface;
import cn.happyselect.sys.bean.dto.*;
import cn.happyselect.sys.bean.vo.ValidUsernameVo;
import cn.happyselect.sys.constant.IdentityTypeConstant;
import cn.happyselect.sys.constant.IforgotProcessConstant;
import cn.happyselect.sys.constant.RedisKeyConstant;
import cn.happyselect.sys.constant.SMSTypeConstant;
import cn.happyselect.sys.entity.User;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.Duration;

/**
 * 密码找回服务
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-08-24
 */
@Service
@Slf4j
public class IforgotBizService {

    /**
     * 找回密码超时时间
     */
    private static final Duration TIMEOUT = Duration.ofMinutes(15);

    @Autowired
    private UserServiceImpl userBizService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private CaptchaBizService captchaBizService;

    public ValidUsernameVo validUsername(ValidUsernameDto validUsernameDto) {
        //验证图片验证码
        captchaBizService.validImageCaptcha(validUsernameDto.getPcId(), validUsernameDto.getPcCode());
        String identifierType = userService.findIdentifierType(validUsernameDto.getIdentifier());
        User t = userService.findByIdentifier(SystemType.resolve(validUsernameDto.getClientType()), validUsernameDto.getIdentifier());
        Assert.notNull(t, "账号不存在");

        ValidUsernameVo validUsernameVo = new ValidUsernameVo();
        String serialNo = IdWorker.getIdStr();

        validUsernameVo.setSerialNo(serialNo);

        IforgotProcessDto iforgotProcessDto = new IforgotProcessDto();
        iforgotProcessDto.setClientType(validUsernameDto.getClientType());
        iforgotProcessDto.setUserId(t.getUserId());
        iforgotProcessDto.setUsernameStatus(StatusConstant.TRUE);

        if (IdentityTypeConstant.PHONE.equals(identifierType)) {
            validUsernameVo.setPhone(t.getPhone());
            validUsernameVo.setNextStep(IforgotProcessConstant.VALID_SHORT_MSG);
            iforgotProcessDto.setDbPhone(t.getPhone());
            iforgotProcessDto.setPhoneStatus(StatusConstant.TRUE);
        } else {
            //查询是否有绑定的手机号
            Assert.hasText(t.getPhone(), "该账号未绑定手机号,请使用其它方式找回");
            String identifier = t.getPhone();
            String phone = identifier.substring(0, 3) + "******" + identifier.substring(identifier.length() - 2);
            validUsernameVo.setPhone(phone);
            validUsernameVo.setNextStep(IforgotProcessConstant.VALID_PHONE);
            iforgotProcessDto.setPhoneStatus(StatusConstant.FALSE);
            iforgotProcessDto.setDbPhone(t.getPhone());
        }
        stringRedisTemplate.boundValueOps(getIforgetKey(serialNo)).set(JSON.toJSONString(iforgotProcessDto), TIMEOUT);
        return validUsernameVo;
    }

    public boolean validPhone(ValidPhoneDto validPhoneDto) {
        BoundValueOperations<String, String> valueOps = stringRedisTemplate.boundValueOps(getIforgetKey(validPhoneDto.getSerialNo()));
        String json = valueOps.get();
        Assert.hasText(json, "请先验证用户名是否正确");
        IforgotProcessDto iforgotDto = JSON.parseObject(json, IforgotProcessDto.class);
        if (iforgotDto.getDbPhone().equals(validPhoneDto.getPhone())) {
            iforgotDto.setPhoneStatus(StatusConstant.TRUE);
            valueOps.set(JSON.toJSONString(iforgotDto), TIMEOUT);
            return true;
        } else {
            throw new RuntimeException("手机号错误");
        }
    }

    public boolean validShortMsg(ValidSmsDto validSmsDto) {
        String json = stringRedisTemplate.boundValueOps(getIforgetKey(validSmsDto.getSerialNo())).get();
        Assert.hasText(json, "请先验证用户名是否正确");
        IforgotProcessDto iforgotDto = JSON.parseObject(json, IforgotProcessDto.class);
        Assert.isTrue(StatusConstant.TRUE.equals(iforgotDto.getPhoneStatus()), "请先验证手机号是否正确");

        boolean matches = captchaBizService.validShortMsg(new ShortMessageDtoInterface() {
            @Override
            public String getPhone() {
                return iforgotDto.getDbPhone();
            }

            @Override
            public String getMessageType() {
                return SMSTypeConstant.FORGET_PASSWORD;
            }

            @Override
            public String getClientType() {
                return iforgotDto.getClientType();
            }
        }, validSmsDto.getSmsCode());

        Assert.isTrue(matches, "短信验证码错误");

        iforgotDto.setSmsCodeStatus(StatusConstant.TRUE);
        stringRedisTemplate.boundValueOps(getIforgetKey(validSmsDto.getSerialNo()))
                .set(JSON.toJSONString(iforgotDto)
                        , TIMEOUT);
        return true;
    }

    public boolean resetPassword(ResetPasswordDto resetPasswordDto) {
        String json = stringRedisTemplate.boundValueOps(getIforgetKey(resetPasswordDto.getSerialNo())).get();
        Assert.hasText(json, "请先验证用户名是否正确");
        IforgotProcessDto iforgotDto = JSON.parseObject(json, IforgotProcessDto.class);
        Assert.isTrue(StatusConstant.TRUE.equals(iforgotDto.getSmsCodeStatus()), "请先验证短信验证码是否正确");
        userBizService.resetPassword(iforgotDto.getUserId(), resetPasswordDto.getPassword());
        //清除用户锁定状态
        userBizService.clearCache(iforgotDto.getUserId());
        return true;
    }

    public static String getIforgetKey(String serialNo) {
        return RedisKeyConstant.IFORGOT + serialNo;
    }
}
