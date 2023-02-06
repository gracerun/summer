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

import javax.validation.constraints.NotBlank;

/**
 * 消息表实体
 *
 * @author yaodan
 * @version 1.0
 * @date 2020年10月22日
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
public class Message extends BaseEntity {

    private static final long serialVersionUID = 4988365017896392L;

    /**
     * 消息状态
     */
    @ApiModelProperty("消息状态")
    @ColumnWidth(20)
    @ExcelProperty("消息状态")
    @NotBlank
    @Length(max = 32, message = "{message.status.length}")
    private String status;
    /**
     * 消息ID
     */
    @ApiModelProperty("消息ID")
    @ColumnWidth(20)
    @ExcelProperty("消息ID")
    @NotBlank
    @Length(max = 32, message = "{message.msgId.length}")
    private String msgId;
    /**
     * 接收方用户ID
     */
    @ApiModelProperty("接收方用户ID")
    @ColumnWidth(20)
    @ExcelProperty("接收方用户ID")
    @NotBlank
    @Length(max = 32, message = "{message.toUserId.length}")
    private String toUserId;
    /**
     * 发送方用户ID
     */
    @ApiModelProperty("发送方用户ID")
    @ColumnWidth(20)
    @ExcelProperty("发送方用户ID")
    @Length(max = 32, message = "{message.fromUserId.length}")
    private String fromUserId;
    /**
     * 标题
     */
    @ApiModelProperty("标题")
    @ColumnWidth(20)
    @ExcelProperty("标题")
    @Length(max = 128, message = "{message.title.length}")
    private String title;
    /**
     * 消息类型
     */
    @ApiModelProperty("消息类型")
    @ColumnWidth(20)
    @ExcelProperty("消息类型")
    @NotBlank
    @Length(max = 32, message = "{message.msgType.length}")
    private String msgType;
    /**
     * 消息内容
     */
    @ApiModelProperty("消息内容")
    @ColumnWidth(20)
    @ExcelProperty("消息内容")
    @NotBlank
    @Length(max = 65535, message = "{message.content.length}")
    private String content;
    /**
     * 目的地
     */
    @ApiModelProperty("目的地")
    @ColumnWidth(20)
    @ExcelProperty("目的地")
    @NotBlank
    @Length(max = 64, message = "{message.destination.length}")
    private String destination;
    /**
     * 读取状态
     */
    @ApiModelProperty("读取状态")
    @ColumnWidth(20)
    @ExcelProperty("读取状态")
    @Length(max = 32, message = "{message.readStatus.length}")
    private String readStatus;
    /**
     * 消息链接
     */
    @ApiModelProperty("消息链接")
    @ColumnWidth(20)
    @ExcelProperty("消息链接")
    @Length(max = 512, message = "{message.url.length}")
    private String url;
    /**
     * 消息描述
     */
    @ApiModelProperty("消息描述")
    @ColumnWidth(20)
    @ExcelProperty("消息描述")
    @Length(max = 256, message = "{message.description.length}")
    private String description;

}
