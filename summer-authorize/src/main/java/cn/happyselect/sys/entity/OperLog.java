package cn.happyselect.sys.entity;

import cn.happyselect.base.entity.BaseEntity;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Digits;
import java.util.Date;

/**
 * 操作日志实体
 *
 * @author yaodan
 * @version 1.0
 * @date 2020年08月28日
 * @since 1.8
 */
@Accessors(chain = true)
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel
@ColumnWidth(35)
@HeadRowHeight(20)
@ContentRowHeight(16)
public class OperLog extends BaseEntity {

    private static final long serialVersionUID = 2220478294719929L;

    /**
     * 用户ID
     */
    @ApiModelProperty("用户ID")
    @ColumnWidth(20)
    @ExcelProperty("用户ID")
    @Length(max = 32, message = "{operlog.userId.length}")
    private String userId;
    /**
     * 登录账号
     */
    @ApiModelProperty("登录账号")
    @ColumnWidth(20)
    @ExcelProperty("登录账号")
    @Length(max = 32, message = "{operlog.userIdentifier.length}")
    private String userIdentifier;
    /**
     * 昵称
     */
    @ApiModelProperty("昵称")
    @ColumnWidth(20)
    @ExcelProperty("昵称")
    @Length(max = 45, message = "{operlog.nickname.length}")
    private String nickname;
    /**
     * 系统类型
     */
    @ApiModelProperty("系统类型")
    @ColumnWidth(20)
    @ExcelProperty("系统类型")
    @Length(max = 32, message = "{operlog.systemType.length}")
    private String systemType;
    /**
     * 请求IP
     */
    @ApiModelProperty("请求IP")
    @ColumnWidth(20)
    @ExcelProperty("请求IP")
    @Length(max = 64, message = "{operlog.requestIp.length}")
    private String requestIp;
    /**
     * 耗时
     */
    @ApiModelProperty("耗时")
    @ColumnWidth(20)
    @ExcelProperty("耗时")
    @Digits(integer = 10, fraction = 0)
    private Integer time;
    /**
     * 跟踪ID
     */
    @ApiModelProperty("跟踪ID")
    @ColumnWidth(20)
    @ExcelProperty("跟踪ID")
    @Length(max = 64, message = "{operlog.traceId.length}")
    private String traceId;
    /**
     * 请求地址
     */
    @ApiModelProperty("请求地址")
    @ColumnWidth(20)
    @ExcelProperty("请求地址")
    @Length(max = 256, message = "{operlog.url.length}")
    private String url;
    /**
     * 请求参数
     */
    @ApiModelProperty("请求参数")
    @ColumnWidth(20)
    @ExcelProperty("请求参数")
    @Length(max = 65535, message = "{operlog.request.length}")
    private String request;
    /**
     * 响应参数
     */
    @ApiModelProperty("响应参数")
    @ColumnWidth(20)
    @ExcelProperty("响应参数")
    @Length(max = 65535, message = "{operlog.response.length}")
    private String response;
    /**
     * 异常
     */
    @ApiModelProperty("异常")
    @ColumnWidth(20)
    @ExcelProperty("异常")
    @Length(max = 65535, message = "{operlog.exception.length}")
    private String exception;

    /**
     * 乐观锁
     */
    private transient Integer optimistic;
    /**
     * 最后更新时间
     */
    private transient Date lastUpdateTime;
}
