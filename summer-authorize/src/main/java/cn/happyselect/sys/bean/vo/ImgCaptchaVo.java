package cn.happyselect.sys.bean.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 图片验证码
 *
 * @author adc
 * @version 1.0.0
 * @date 2020-08-13
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class ImgCaptchaVo {

    /**
     * 图片Base64字符串
     */
    @ApiModelProperty("图片Base64字符串")
    private String image;

    /**
     * 图片ID
     */
    @ApiModelProperty("图片ID")
    private String pcId;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("imageSize", image == null ? 0 : image.length())
                .append("pcId", pcId)
                .toString();
    }
}
