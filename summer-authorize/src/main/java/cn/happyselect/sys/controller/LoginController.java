package cn.happyselect.sys.controller;

import cn.happyselect.sys.bean.dto.LoginToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 登录与退出接口
 *
 * @author adc
 * @version 1.0.0
 * @date 5/20/21
 */
@Slf4j
@Api(value = "登录与退出接口", tags = "登录与退出接口")
@RestController

public class LoginController {

    /**
     * 用户登录
     *
     * @param loginToken
     * @return
     */
    @ApiOperation(value = "用户登录", notes = "用户登录", httpMethod = "POST", produces = "application/json")
    @PostMapping("/login")
    public void login(@Valid @RequestBody LoginToken loginToken) {

    }

    /**
     * 用户退出
     *
     * @return
     */
    @ApiOperation(value = "用户退出", notes = "用户退出", httpMethod = "POST", produces = "application/json")
    @PostMapping("/logout")
    public boolean logout() {
        return true;
    }

}
