package com.xiangxi.message.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author 初心
 * Create by on 2025/10/16 15:09 29
 */
public class GsonUtil {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .setPrettyPrinting() // 可选：美化输出
            .create();

    public static Gson getGson() {
        return GSON;
    }

    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    static class LocalDateTimeAdapter extends com.google.gson.TypeAdapter<LocalDateTime> {
        private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public void write(com.google.gson.stream.JsonWriter out, LocalDateTime value)
                throws java.io.IOException {
            out.value(value != null ? formatter.format(value) : null);
        }

        @Override
        public LocalDateTime read(com.google.gson.stream.JsonReader in)
                throws java.io.IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            String str = in.nextString();
            return str != null ? LocalDateTime.parse(str, formatter) : null;
        }
    }
}
