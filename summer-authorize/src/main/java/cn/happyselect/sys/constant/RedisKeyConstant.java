package cn.happyselect.sys.constant;

/**
 * redis常量
 */
public interface RedisKeyConstant {

    String NAMESPACE = "sys:";

    /**
     * 短信常量
     */
    String SHORT_MSG = NAMESPACE + "shortMsg:";

    /**
     * 忘记密码常量
     */
    String IFORGOT = NAMESPACE + "iforgot:";

    /**
     * 短信发送次数
     */
    String MSG_ATTEMPTS = NAMESPACE + "msgAttempts:";

    /**
     * 图片验证码常量
     */
    String IMAGE_CAPTCHA = NAMESPACE + "imageCaptcha:";

    /**
     * 登录失败尝试次数常量
     */
    String LOGIN_FAIL_ATTEMPTS = NAMESPACE + "loginFailAttempts:";

    /**
     * 用户锁定状态
     */
    String USER_LOCKED = NAMESPACE + "userLocked:";

    /**
     * 操作日志备份锁
     */
    String OPERLOG_BACKUP_LOCKED = NAMESPACE + "operLogBackupLocked";

    /**
     * 空占位符
     */
    String EMPTY_PLACE_HOLDER = "0";

    /**
     * 消息队列
     */
    String MSG_QUEUE = NAMESPACE + "msgQueue:";

    /**
     * 消息数据加载锁
     */
    String MSG_LOADDB_LOCK = NAMESPACE + "msgLoaddbLock";

    /**
     * 在线用户数量
     */
    String ONLINE_USER_COUNT = NAMESPACE + "onlineUserCount";

    /**
     * 用户websocketSessionIndex
     */
    String HTTP_SESSION_ID_WS_IP_MAP = "admin.ws.session:httpSessionIdWsIpMap:";

}
