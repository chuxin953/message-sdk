package com.xiangxi.message.client.enums;

/**
 * @author 初心
 * Create by on 2025/9/21 10:55
 */
public enum HttpContentType {
    JSON("application/json; charset=utf-8"),
    XML("application/xml; charset=utf-8"),
    FORM_URLENCODED("application/x-www-form-urlencoded; charset=utf-8"),
    MULTIPART("multipart/form-data"),
    TEXT("text/plain; charset=utf-8"),
    HTML("text/html; charset=utf-8"),
    OCTET_STREAM("application/octet-stream");
    private final String value;

    HttpContentType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
