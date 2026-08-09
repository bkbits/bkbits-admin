package com.bkbits.dbo.enums;

/**
 * 通用状态枚举。
 */
public enum CommonStatusEnum {

    /** 启用 */
    ENABLE(1),

    /** 禁用 */
    DISABLE(0);

    private final int code;

    CommonStatusEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
