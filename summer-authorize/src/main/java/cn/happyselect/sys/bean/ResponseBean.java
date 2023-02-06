package cn.happyselect.sys.bean;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * 响应bean
 *
 * @author xiaojie.zhang
 * @version V1.0.0
 * @since 2018/9/11
 */
@Getter
@Setter
@EqualsAndHashCode
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel
public class ResponseBean<T> implements ResponseCode, Serializable {

    /**
     * 响应码
     */
    @ApiModelProperty("响应码")
    private String code;

    /**
     * 响应描述
     */
    @ApiModelProperty("响应描述")
    private String msg;

    /**
     * 响应数据
     */
    @ApiModelProperty("响应数据")
    private T data;

    public ResponseBean() {
    }

    public ResponseBean(T data) {
        this.data = data;
        this.code = SUCCESS;
        this.msg = SUCCESS_CN;
    }

    public ResponseBean(String code, T data) {
        this.code = code;
        this.data = data;
    }

    public ResponseBean(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResponseBean(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    @JsonIgnore
    public boolean isSuccess() {
        return Objects.equals(code, SUCCESS);
    }

    public static ResponseBean successBean() {
        return new ResponseBean()
                .setCode(SUCCESS)
                .setMsg(SUCCESS_CN);
    }

    public static ResponseBean successBean(Object data) {
        return new ResponseBean()
                .setCode(SUCCESS)
                .setMsg(SUCCESS_CN)
                .setData(data);
    }

    public static ResponseBean failedBean() {
        return new ResponseBean()
                .setCode(FAILED)
                .setMsg(FAILED_CN);
    }

    public static ResponseBean failedBean(String msg) {
        return new ResponseBean()
                .setCode(FAILED)
                .setMsg(msg);
    }

    public static ResponseBean repeatBean(String msg) {
        return new ResponseBean()
                .setCode(REPEAT)
                .setMsg(msg);
    }

    public static ResponseBean paramErrBean(String msg) {
        return new ResponseBean()
                .setCode(PARAM_ERR)
                .setMsg(msg);
    }

    public static ResponseBean paramErrBean() {
        return new ResponseBean()
                .setCode(PARAM_ERR)
                .setMsg(PARAM_ERR_CN);
    }

    public static ResponseBean bizErrBean() {
        return new ResponseBean()
                .setCode(BIZ_ERR)
                .setMsg(BIZ_ERR_CN);
    }

    public static ResponseBean bizErrBean(String msg) {
        return new ResponseBean()
                .setCode(BIZ_ERR)
                .setMsg(msg);
    }

    public static ResponseBean unknownErrBean() {
        return new ResponseBean()
                .setCode(UNKNOWN_ERR)
                .setMsg(UNKNOWN_ERR_CN);
    }

    public static ResponseBean dbErrBean() {
        return new ResponseBean()
                .setCode(DB_ERR)
                .setMsg(DB_ERR_CN);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ResponseBean.class.getSimpleName() + "[", "]")
                .add("code='" + code + "'")
                .add("msg='" + msg + "'")
                .add("data=" + data)
                .toString();
    }
}
