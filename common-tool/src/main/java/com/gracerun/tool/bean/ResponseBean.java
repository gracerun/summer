package com.gracerun.tool.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gracerun.tool.constant.ResponseCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseBean<T> implements ResponseCode, Serializable {
    private static final long serialVersionUID = 1L;

    protected String code;
    protected String msg;
    protected T data;

    /*@JsonIgnore
    @JSONField(serialize = false)*/
    public boolean isSuccess() {
        return ResponseBean.SUCCESS.equals(getCode());
    }

    public ResponseBean() {
        super();
    }

    public ResponseBean(T data) {
        this.data = data;
        this.code = SUCCESS;
        this.msg = SUCCESS_CN;
    }

    public ResponseBean(String code, String msg) {
        super();
        this.code = code;
        this.msg = msg;
    }

    public ResponseBean(String code, String msg, T data) {
        super();
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static ResponseBean success() {
        return success(null);
    }

    public static ResponseBean successBean() {
        return success(null);
    }

    public static ResponseBean successBean(Object data) {
        return success(data);
    }

    public static ResponseBean success(Object data) {
        ResponseBean<Object> responseBean = new ResponseBean<Object>();
        responseBean.setCode(SUCCESS);
        responseBean.setMsg(SUCCESS_CN);
        responseBean.setData(data);
        return responseBean;
    }

    public static ResponseBean failedBean() {
        ResponseBean responseBean = new ResponseBean();
        responseBean.setCode(FAIL);
        responseBean.setMsg(FAIL_CN);
        return responseBean;
    }

    public static ResponseBean fail(String code, String msg) {
        ResponseBean responseBean = new ResponseBean();
        responseBean.setCode(code);
        responseBean.setMsg(msg);
        return responseBean;
    }

    public static ResponseBean paramErrBean(String msg) {
        ResponseBean responseBean = new ResponseBean();
        responseBean.setCode(PARAM_ERR);
        responseBean.setMsg(msg);
        return responseBean;
    }

    public static ResponseBean paramErrBean() {
        ResponseBean responseBean = new ResponseBean();
        responseBean.setCode(PARAM_ERR);
        responseBean.setMsg(PARAM_ERR_CN);
        return responseBean;
    }

    public static ResponseBean bizErrBean() {
        ResponseBean responseBean = new ResponseBean();
        responseBean.setCode(BIZ_ERR);
        responseBean.setMsg(BIZ_ERR_CN);
        return responseBean;
    }

    public static ResponseBean bizErrBean(String msg) {
        ResponseBean responseBean = new ResponseBean();
        responseBean.setCode(BIZ_ERR);
        responseBean.setMsg(msg);
        return responseBean;
    }
}
