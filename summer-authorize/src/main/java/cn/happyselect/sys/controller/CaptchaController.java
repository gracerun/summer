package cn.happyselect.sys.controller;

import cn.happyselect.sys.bean.dto.ShortMessageDto;
import cn.happyselect.sys.bean.vo.ImgCaptchaVo;
import cn.happyselect.sys.service.CaptchaBizService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 验证码控制器
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-08-13
 */
@Slf4j
@Api(value = "验证码接口", tags = "验证码接口")
@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    @Resource
    private CaptchaBizService captchaBizService;

    /**
     * 发送短信验证码
     *
     * @param shortMessageDto
     * @return
     */
    @ApiOperation(value = "发送短信验证码", notes = "发送短信验证码", httpMethod = "POST", produces = "application/json")
    @PostMapping("/short/message")
    public boolean shortMessage(@Valid @RequestBody ShortMessageDto shortMessageDto) {
        return captchaBizService.shortMessage(shortMessageDto);
    }

    /**
     * 图片验证码
     *
     * @return
     */
    @ApiOperation(value = "图片验证码", notes = "图片验证码", httpMethod = "POST", produces = "application/json")
    @PostMapping("/image")
    public ImgCaptchaVo image() {
        return captchaBizService.image();
    }

}
