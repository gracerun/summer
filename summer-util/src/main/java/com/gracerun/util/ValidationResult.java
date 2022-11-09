package com.gracerun.util;

import java.util.Map;

/**
 * Copyright (C), 2010-2020, emi99 Payment. Co., Ltd.
 * <p>
 * 校验结果
 *
 * @author lowrie
 * @version 1.0.0
 * @date 2020-06-30
 */
public class ValidationResult {

    /**
     * 校验结果是否有错
     */
    private boolean hasErrors;

    /**
     * 校验错误信息
     */
    private Map<String, String> errorMsg;

    public boolean isHasErrors() {
        return hasErrors;
    }

    public void setHasErrors(boolean hasErrors) {
        this.hasErrors = hasErrors;
    }

    public Map<String, String> getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(Map<String, String> errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
        return "ValidationResult [hasErrors=" + hasErrors + ", errorMsg="
                + errorMsg + "]";
    }
}
