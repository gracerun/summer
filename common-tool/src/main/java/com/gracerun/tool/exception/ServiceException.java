package com.gracerun.tool.exception;


import com.gracerun.tool.bean.ResponseBean;

public class ServiceException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String code;

    public ServiceException() {
        super();
    }

    public ServiceException(String code, String message) {
        super(message);
        this.code = code;
    }

    public ServiceException(ResponseBean<?> responseBean) {
        super(responseBean.getMsg());
        this.code = responseBean.getCode();
    }

    public ServiceException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public ServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = ResponseBean.FAIL;
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
        this.code = ResponseBean.FAIL;
    }

    public ServiceException(String message) {
        super(message);
        this.code = ResponseBean.FAIL_98;
    }

    public ServiceException(Throwable cause) {
        super(cause);
        this.code = ResponseBean.FAIL;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
