package com.gracerun.tool.constant;

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
     * 账号未绑定
     */
    String NOT_BIND = "01";
    String NOT_BIND_CN = "账号未绑定";

    /**
     * 失败
     */
    String FAIL = "99";
    String FAIL_CN = "失败";

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
     * redis操作失败
     */
    String RDB_ERR = "90";
    String RDB_ERR_CN = "REDIS操作失败";

    /**
     * redis异常
     */
    String FAIL_90 = "90";

    /**
     * 加锁失败
     */
    String LOCK_FAIL = "91";
    String LOCK_FAIL_CN = "加锁失败";

    /**
     * 查询数据不存在
     */
    String FAIL_91 = "91";

    /**
     * 键重复
     */
    String DUPLICATE_KEY = "92";
    String DUPLICATE_KEY_CN = "键重复";

    /**
     * 主键重复或唯一约束键重复
     */
    String FAIL_92 = "92";

    /**
     * 乐观锁异常
     */
    String FAIL_93 = "93";

    /**
     * 数据库操作失败
     */
    String DB_ERR = "94";
    String DB_ERR_CN = "数据库操作失败";

    /**
     * 参数不完整
     */
    String FAIL_94 = "94";

    /**
     * 重复提交
     */
    String FAIL_95 = "95";
    String FAIL_95_CN = "重复提交";

    /**
     * 验签失败
     */
    String FAIL_96 = "96";

    /**
     * 参数错误
     */
    String PARAM_ERR = "97";
    String PARAM_ERR_CN = "参数错误";

    /**
     * http请求超时、请求未响应、请求失败
     */
    String FAIL_97 = "97";

    /**
     * 业务异常码 例如：数据错误、或调用接口方返回错误码
     */
    String FAIL_98 = "98";

    /**
     * 业务错误
     */
    String BIZ_ERR = "98";
    String BIZ_ERR_CN = "业务错误";


    /**
     * 未授权
     */
    String FAIL_401 = "401";
}
