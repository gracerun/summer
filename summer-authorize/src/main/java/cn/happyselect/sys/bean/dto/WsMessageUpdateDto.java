package cn.happyselect.sys.bean.dto;

import cn.happyselect.sys.entity.Message;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Accessors(chain = true)
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class WsMessageUpdateDto extends Message {

    /**
     * 消息状态
     */
    @ApiModelProperty("消息状态")
    @ColumnWidth(20)
    @ExcelProperty("消息状态")
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
     * 读取状态
     */
    @ApiModelProperty("读取状态")
    @ColumnWidth(20)
    @ExcelProperty("读取状态")
    @Length(max = 32, message = "{message.readStatus.length}")
    private String readStatus;

}
