package com.xiangxi.message.client;

public record HttpResponse(int statusCode, String body) {

    public boolean isOk() {
        return statusCode >= 200 && statusCode < 300;
    }
}
