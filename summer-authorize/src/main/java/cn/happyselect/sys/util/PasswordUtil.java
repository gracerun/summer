package cn.happyselect.sys.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 密码加密工具
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-08-25
 */
@Slf4j
@Component
public class PasswordUtil {

    private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder();

    public static String encode(String password) {
        return ENCODER.encode(password);
    }

    public static boolean matches(CharSequence rawPassword, String encodedPassword) {
        return ENCODER.matches(rawPassword, encodedPassword);
    }

    public static void main(String[] args) {
        String s = PasswordUtil.encode("1234qwer");
        log.info("加密结果:{}", s);
    }
}

