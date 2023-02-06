package cn.happyselect.sys.bean.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 操作日志备份表dto
 *
 * @author yaodan
 * @version 1.0
 * @date 2020年09月01日
 * @since 1.8
 */
@ApiModel(value = "OperLogBackupDto", description = "操作日志备份表入参类")
@Accessors(chain = true)
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class OperLogBackupQueryDto {

    /**
     * 创建时间
     */
    @ColumnWidth(20)
    @ExcelProperty("创建时间")
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
