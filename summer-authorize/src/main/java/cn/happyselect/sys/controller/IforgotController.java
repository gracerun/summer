package cn.happyselect.sys.controller;

import cn.happyselect.sys.bean.dto.ResetPasswordDto;
import cn.happyselect.sys.bean.dto.ValidPhoneDto;
import cn.happyselect.sys.bean.dto.ValidSmsDto;
import cn.happyselect.sys.bean.dto.ValidUsernameDto;
import cn.happyselect.sys.bean.vo.ValidUsernameVo;
import cn.happyselect.sys.service.IforgotBizService;
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
 * 密码找回
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-08-24
 */
@Slf4j
@Api(value = "密码找回", tags = "密码找回")
@RestController

@RequestMapping(("/iforgot"))
public class IforgotController {

    @Resource
    private IforgotBizService iforgotBizService;

    /**
     * 验证登录账号
     *
     * @param loginNameDto
     * @return
     */
    @ApiOperation(value = "验证登录账号", notes = "验证登录账号", httpMethod = "POST", produces = "application/json")
    @PostMapping("/validUsername")
    public ValidUsernameVo validUsername(@Valid @RequestBody ValidUsernameDto loginNameDto) {
        return iforgotBizService.validUsername(loginNameDto);
    }

    /**
     * 验证手机号
     *
     * @param validPhoneDto
     * @return
     */
    @ApiOperation(value = "验证手机号", notes = "验证手机号", httpMethod = "POST", produces = "application/json")
    @PostMapping("/validPhone")
    public boolean validPhone(@Valid @RequestBody ValidPhoneDto validPhoneDto) {
        return iforgotBizService.validPhone(validPhoneDto);
    }

    /**
     * 验证短信验证码
     *
     * @param validSmsDto
     * @return
     */
    @ApiOperation(value = "验证短信验证码", notes = "验证短信验证码", httpMethod = "POST", produces = "application/json")
    @PostMapping("/validShortMsg")
    public boolean validShortMsg(@Valid @RequestBody ValidSmsDto validSmsDto) {
        return iforgotBizService.validShortMsg(validSmsDto);
    }

    /**
     * 重置密码
     *
     * @param resetPasswordDto
     * @return
     */
    @ApiOperation(value = "重置密码", notes = "重置密码", httpMethod = "POST", produces = "application/json")
    @PostMapping("/resetPassword")
    public boolean resetPassword(@Valid @RequestBody ResetPasswordDto resetPasswordDto) {
        return iforgotBizService.resetPassword(resetPasswordDto);
    }

}
