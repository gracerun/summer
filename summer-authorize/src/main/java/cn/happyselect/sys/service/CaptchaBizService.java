package cn.happyselect.sys.service;

import cn.happyselect.base.enums.ClientType;
import cn.happyselect.sys.bean.ShortMessageDtoInterface;
import cn.happyselect.sys.bean.dto.ShortMessageDto;
import cn.happyselect.sys.bean.vo.ImgCaptchaVo;
import cn.happyselect.sys.constant.RedisKeyConstant;
import cn.happyselect.sys.constant.SMSTypeConstant;
import com.gracerun.summermq.service.RedisService;
import com.gracerun.util.SpringProfilesUtil;
import com.wf.captcha.ArithmeticCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

/**
 * 验证码业务
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-08-13
 */
@Service
@Slf4j
public class CaptchaBizService {

    /**
     * 验证码超时时间
     */
    private static final Duration TIMEOUT = Duration.ofMinutes(15);

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedisService redisService;

    /**
     * 发送短信
     *
     * @return
     */
    public boolean shortMessage(ShortMessageDto dto) {
        if (!SMSTypeConstant.LOGIN.equals(dto.getMessageType())
                && !SMSTypeConstant.FORGET_PASSWORD.equals(dto.getMessageType())
                && !SMSTypeConstant.REGISTER.equals(dto.getMessageType())
                && !SMSTypeConstant.MODIFY_PASSWORD.equals(dto.getMessageType())
        ) {
            throw new RuntimeException("短信类型错误");
        }

        String lockKey = RedisKeyConstant.NAMESPACE + dto.getClientType() + dto.getPhone();
        String uuid = UUID.randomUUID().toString();
        boolean lockStatus = redisService.tryLock(lockKey, uuid, 60);
        if (!lockStatus) {
            log.info("{} lock fail", lockKey);
            return true;
        }

        try {
            String captchaKey = getShortMsgKey(dto);
            String captchaAttemptsKey = getMsgAttemptsKey(dto);
            BoundValueOperations<String, String> captchaKeyValueOps = stringRedisTemplate.boundValueOps(captchaKey);
            if (StringUtils.hasText(captchaKeyValueOps.get())) {
                throw new RuntimeException("短信还在有效期范围内,不用重新发送");
            }

            BoundValueOperations<String, String> valueOps = stringRedisTemplate.boundValueOps(captchaAttemptsKey);
            String count = valueOps.get();
            if (StringUtils.hasText(count) && Integer.parseInt(count) > 15) {
                throw new RuntimeException("超出当日短信发送次数");
            }

            boolean notExistIdentifier = userService.notExistPhone(ClientType.resolveSystemType(dto.getClientType()), dto.getPhone(), null);
            if (SMSTypeConstant.LOGIN.equals(dto.getMessageType()) && notExistIdentifier
                    || SMSTypeConstant.FORGET_PASSWORD.equals(dto.getMessageType()) && notExistIdentifier) {
                throw new RuntimeException("手机号不存在");
            } else if (SMSTypeConstant.REGISTER.equals(dto.getMessageType()) && !notExistIdentifier) {
                throw new RuntimeException("该手机号已注册");
            }

            String messageCode;
            if (SpringProfilesUtil.isProd()) {
//                messageCode = Integer.toString(RandomUtils.nextInt(100000, 999999));
                messageCode = "000000";
            } else {
                messageCode = "000000";
            }

//            ResponseBean res = ShortMessageUtil.sendMessage(dto.getPhone(), messageCode);

            long increment = valueOps.increment();
            if (increment == 1) {
                Duration timeout = Duration.ofDays(1).plusSeconds(0 - new DateTime().getSecondOfDay());
                if (timeout.getSeconds() <= 0) {
                    timeout = Duration.ofDays(1);
                }
                valueOps.expire(timeout);
            }

            captchaKeyValueOps.set(messageCode, TIMEOUT);
            log.info("phone:{},captcha:{}", dto.getPhone(), messageCode);
            return true;
        } finally {
            redisService.releaseLock(lockKey, uuid);
        }
    }

    /**
     * 获取图片验证码
     *
     * @return
     * @throws IOException
     */
    public ImgCaptchaVo image() {
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(160, 40);
        // 几位数运算，默认是两位
        captcha.setLen(2);
        // 获取运算的结果
        String result = captcha.text();
        String pcId = UUID.randomUUID().toString().replace("-", "");
        String imageCaptchaKey = getImageCaptchaKey(pcId);
        stringRedisTemplate.boundValueOps(imageCaptchaKey).set(result, TIMEOUT);
        log.info("key:{},code:{}", imageCaptchaKey, result);
        ImgCaptchaVo data = new ImgCaptchaVo();
        data.setPcId(pcId);
        data.setImage(captcha.toBase64());
        return data;
    }

    /**
     * 验证图片验证码
     *
     * @param pcId
     * @param pcCode
     */
    public void validImageCaptcha(String pcId, String pcCode) {
        String captcha = stringRedisTemplate.boundValueOps(CaptchaBizService.getImageCaptchaKey(pcId)).get();
        Assert.hasText(captcha, "请获取图片验证码或图片验证码已过期");
        try {
            stringRedisTemplate.delete(CaptchaBizService.getImageCaptchaKey(pcId));
        } catch (Exception e) {
        }
        Assert.isTrue(captcha.equalsIgnoreCase(pcCode), "图片验证码错误");
    }

    /**
     * 验证短信验证码
     *
     * @param dto
     * @return
     */
    public boolean validShortMsg(ShortMessageDtoInterface dto, String smsCode) {
        String captcha = stringRedisTemplate.boundValueOps(CaptchaBizService.getShortMsgKey(dto)).get();
        Assert.hasText(captcha, "短信验证码已过期,请重新获取");
        try {
            stringRedisTemplate.delete(CaptchaBizService.getShortMsgKey(dto));
        } catch (Exception e) {
        }
        return captcha.equalsIgnoreCase(smsCode);
    }

    private static String getShortMsgKey(ShortMessageDtoInterface dto) {
        return RedisKeyConstant.SHORT_MSG + dto.getMessageType() + dto.getClientType() + dto.getPhone();
    }

    private static String getImageCaptchaKey(String pcId) {
        return RedisKeyConstant.IMAGE_CAPTCHA + pcId;
    }

    private static String getMsgAttemptsKey(ShortMessageDtoInterface dto) {
        return RedisKeyConstant.MSG_ATTEMPTS + dto.getMessageType() + dto.getClientType() + dto.getPhone();
    }

}
