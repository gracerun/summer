package com.gracerun.tool.exception;

import java.io.IOException;

/**
 * 包装IOException异常
 *
 * @author adc
 * @version 1.0.0
 * @date 5/19/21
 */
public class HttpClientIOException extends RuntimeException {

    public HttpClientIOException(IOException e) {
        super(e);
    }

}
