package com.gracerun.tool.exception;

import java.net.SocketTimeoutException;

/**
 * 说明: 封装了java原生SocketTimeoutException<br/>
 * 在读取服务器响应超时时，出现
 *
 * @author sck
 * @create 2018-04-03 上午10:18
 */
public class HttpClientSocketTimeoutException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public HttpClientSocketTimeoutException(SocketTimeoutException e) {
        super(e);
    }

}
