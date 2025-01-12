package com.gracerun.tool.exception;

import org.apache.http.conn.ConnectTimeoutException;

/**
 * 说明:
 * 封装了httpClient的ConnectTimeoutExcepton<br/>
 * 在请求服务超时时，出现
 *
 * @author sck
 * @create 2018-04-03 上午10:14
 */
public class HttpClientConnectTimeoutException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public HttpClientConnectTimeoutException(ConnectTimeoutException e) {
        super(e);
    }

}
