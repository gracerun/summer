package com.gracerun.summermq.util;

import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 过滤Null属性
 *
 * @author Tom
 * @version 1.0.0
 * @date 4/13/22
 */
public class NotNullStringStyle extends ToStringStyle {

    private static final NotNullStringStyle ins = new NotNullStringStyle();
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 警告:请不要new我
     */
    private NotNullStringStyle() {
        super();
        this.setUseShortClassName(true);
        this.setUseIdentityHashCode(false);
    }

    @Override
    public void append(final StringBuffer buffer, final String fieldName, final Object value, final Boolean fullDetail) {
        if (value != null) {
            appendFieldStart(buffer, fieldName);
            appendInternal(buffer, fieldName, value, isFullDetail(fullDetail));
            appendFieldEnd(buffer, fieldName);
        }
    }

    public static NotNullStringStyle getSytle() {
        return ins;
    }

}
