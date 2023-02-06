package cn.happyselect.sys.bean;

/**
 * 响应码
 *
 * @author xiaojie.zhang
 * @version V1.0.0
 * @since 2020/8/7
 */
public interface ResponseCode {

    /**
     * 成功
     */
    String SUCCESS = "00";
    String SUCCESS_CN = "成功";

    /**
     * 失败
     */
    String FAILED = "01";
    String FAILED_CN = "失败";


    /**
     * 任务重试
     */
    String REPEAT = "02";
    String REPEAT_CN = "重试";

    /**
     * 被迫退出
     */
    String EXPIRED_LOGIN = "03";
    String EXPIRED_LOGIN_CN = "您的账号已在其它地方登录，若不是本人操作，请注意账号安全！";

    /**
     * 未登录
     */
    String NOT_LOGIN = "04";
    String NOT_LOGIN_CN = "您还未登录,请登录后操作!";

    /**
     * 无权限
     */
    String UN_AUTHORIZED = "05";
    String UN_AUTHORIZED_CN = "无操作权限,请联系管理员!";

    /**
     * 密码已过期
     */
    String AUTH_EXPIRED = "06";
    String AUTH_EXPIRED_CN = "密码已过期,请修改密码";

    /**
     * 验签异常
     */
    String VERIFY_ERR = "07";
    String VERIFY_ERR_CN = "验证签名失败";

    /**
     * 签名异常
     */
    String SIGN_ERR = "08";
    String SIGN_ERR_CN = "平台响应签名失败";

    /**
     * 订单信息不存在
     */
    String ORDER_NOT_EXITS = "09";
    String ORDER_NOT_EXITS_CN = "订单信息不存在";

    /**
     * 未实名认证或实名认证未通过
     */
    String VERIFIED = "10";
    String VERIFIED_CN = "未实名认证或实名认证未通过";

    /**
     * redis操作失败
     */
    String RDB_ERR = "90";
    String RDB_ERR_CN = "REDIS操作失败";

    /**
     * 加锁失败
     */
    String LOCK_FAIL = "91";
    String LOCK_FAIL_CN = "加锁失败";

    /**
     * 键重复
     */
    String DUPLICATE_KEY = "92";
    String DUPLICATE_KEY_CN = "键重复";

    /**
     * 数据库操作失败
     */
    String DB_ERR = "94";
    String DB_ERR_CN = "数据库操作失败";

    /**
     * 参数错误
     */
    String PARAM_ERR = "97";
    String PARAM_ERR_CN = "参数错误";

    /**
     * 业务错误
     */
    String BIZ_ERR = "98";
    String BIZ_ERR_CN = "业务错误";

    /**
     * 未知错误
     */
    String UNKNOWN_ERR = "99";
    String UNKNOWN_ERR_CN = "未知错误";
}
