package com.gracerun.tool.bean;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 字节包装类
 *
 * @author adc
 * @version 1.0.0
 * @date 3/30/21
 */
public class ByteData {

    private byte[] byteData;

    public ByteData() {
    }

    public ByteData(byte[] byteData) {
        this.byteData = byteData;
    }

    public byte[] getByteData() {
        return byteData;
    }

    public void setByteData(byte[] byteData) {
        this.byteData = byteData;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("byteData", byteData == null ? 0 : byteData.length)
                .toString();
    }
}
