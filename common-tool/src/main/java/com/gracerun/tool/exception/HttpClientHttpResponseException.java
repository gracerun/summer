package com.gracerun.tool.exception;

import org.apache.http.client.HttpResponseException;

/**
 * http异常：返回的协议不是http2.xx
 *
 * @author adc
 */
public class HttpClientHttpResponseException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private HttpResponseException httpResponseException;

    public HttpClientHttpResponseException(HttpResponseException httpResponseException) {
        super(httpResponseException);
        this.httpResponseException = httpResponseException;
    }

    public HttpResponseException getHttpResponseException() {
        return httpResponseException;
    }

    public void setHttpResponseException(HttpResponseException httpResponseException) {
        this.httpResponseException = httpResponseException;
    }

}
