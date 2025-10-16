package com.xiangxi.message.common.enums;

/**
 * 通用消息返回码（含描述）
 */
public enum MessageCode {
    SUCCESS("SUCCESS", "全部成功"),
    FAILED("FAILED", "全部失败"),
    PARTIAL_SUCCESS("PARTIAL_SUCCESS", "部分成功，部分失败");

    private final String code;
    private final String description;

    MessageCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return code;
    }

    public static MessageCode fromCode(String code) {
        if (code == null) return null;
        for (MessageCode mc : values()) {
            if (mc.code.equalsIgnoreCase(code)) {
                return mc;
            }
        }
        return null;
    }
}


