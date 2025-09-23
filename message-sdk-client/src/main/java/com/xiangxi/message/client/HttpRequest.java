package com.xiangxi.message.client;

import com.xiangxi.message.client.enums.HttpContentType;
import com.xiangxi.message.client.enums.HttpMethod;

import java.io.File;
import java.time.Duration;
import java.util.*;

/**
 * 统一的 HTTP 请求模型。
 *
 * 用于上层构建与网关交互的请求数据，配合适配器转换为底层客户端请求。
 */
public class HttpRequest {
    // 请求地址（可以是绝对URL或相对路径）
    private final String url;
    // 请求方式
    private final HttpMethod method;
    // 请求头
    private final Map<String, String> headers;
    // Content-Type
    private final String contentType;
    // 请求体（当 method=POST 等有体时使用）
    private final String body;
    // 表单字段（application/x-www-form-urlencoded）
    private final Map<String, String> form;
    // 文件字段（multipart/form-data）
    private final Map<String, File> files;
    // 查询参数
    private final Map<String, String> query;
    // 重试次数（null 表示使用客户端默认）
    private final Integer retries;
    // 重试退避时间（null 表示使用客户端默认）
    private final Duration retryBackoff;

    /**
     * 私有构造函数，只能通过建造者创建
     *
     * @param builder 建造者对象
     */
    private HttpRequest(Builder builder) {
        this.url = builder.url;
        this.method = builder.method;
        this.contentType = builder.contentType;
        this.body = builder.body;
        this.retries = builder.retries;
        this.retryBackoff = builder.retryBackoff;

        // 创建不可变副本
        this.headers = Collections.unmodifiableMap(
                builder.headers != null ? new HashMap<>(builder.headers) : new HashMap<>()
        );
        this.form = Collections.unmodifiableMap(
                builder.form != null ? new HashMap<>(builder.form) : new HashMap<>()
        );
        this.files = Collections.unmodifiableMap(
                builder.files != null ? new HashMap<>(builder.files) : new HashMap<>()
        );
        this.query = Collections.unmodifiableMap(
                builder.query != null ? new LinkedHashMap<>(builder.query) : new LinkedHashMap<>()
        );
    }

    /**
     * 创建建造者实例
     *
     * @return 建造者对象
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 创建建造者实例并设置URL
     *
     * @param url 请求URL
     * @return 建造者对象
     */
    public static Builder builder(String url) {
        return new Builder().url(url);
    }

    // ========== Getter 方法 ==========

    public String getUrl() {
        return url;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getContentType() {
        return contentType;
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getForm() {
        return form;
    }

    public Map<String, File> getFiles() {
        return files;
    }

    public Map<String, String> getQuery() {
        return query;
    }

    public Integer getRetries() {
        return retries;
    }

    public Duration getRetryBackoff() {
        return retryBackoff;
    }

    /**
     * 检查是否有请求体
     *
     * @return 如果有请求体返回true
     */
    public boolean hasBody() {
        return body != null && !body.isEmpty();
    }

    /**
     * 检查是否有表单数据
     *
     * @return 如果有表单数据返回true
     */
    public boolean hasFormData() {
        return !form.isEmpty();
    }

    /**
     * 检查是否有文件上传
     *
     * @return 如果有文件上传返回true
     */
    public boolean hasFiles() {
        return !files.isEmpty();
    }

    /**
     * 检查是否有查询参数
     *
     * @return 如果有查询参数返回true
     */
    public boolean hasQueryParams() {
        return !query.isEmpty();
    }

    /**
     * 获取指定请求头的值
     *
     * @param name 请求头名称
     * @return 请求头值，如果不存在返回null
     */
    public String getHeader(String name) {
        return headers.get(name);
    }

    /**
     * 检查是否包含指定请求头
     *
     * @param name 请求头名称
     * @return 如果包含返回true
     */
    public boolean hasHeader(String name) {
        return headers.containsKey(name);
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "url='" + url + '\'' +
                ", method=" + method +
                ", contentType='" + contentType + '\'' +
                ", hasBody=" + hasBody() +
                ", hasFormData=" + hasFormData() +
                ", hasFiles=" + hasFiles() +
                ", hasQueryParams=" + hasQueryParams() +
                ", headersCount=" + headers.size() +
                ", retries=" + retries +
                ", retryBackoff=" + retryBackoff +
                '}';
    }

    /**
     * 建造者类
     */
    public static class Builder {
        private String url;
        private HttpMethod method = HttpMethod.GET;
        private Map<String, String> headers;
        private String contentType;
        private String body;
        private Map<String, String> form;
        private Map<String, File> files;
        private Map<String, String> query;
        private Integer retries;
        private Duration retryBackoff;

        /**
         * 设置请求URL
         *
         * @param url 请求URL
         * @return 建造者实例
         * @throws IllegalArgumentException 如果URL为空
         */
        public Builder url(String url) {
            this.url = Objects.requireNonNull(url, "URL不能为空");
            if (url.trim().isEmpty()) {
                throw new IllegalArgumentException("URL不能为空字符串");
            }
            return this;
        }

        /**
         * 设置请求方法
         *
         * @param method HTTP方法
         * @return 建造者实例
         * @throws IllegalArgumentException 如果方法为空
         */
        public Builder method(HttpMethod method) {
            this.method = Objects.requireNonNull(method, "HTTP方法不能为空");
            return this;
        }

        /**
         * 设置Content-Type
         *
         * @param contentType Content-Type
         * @return 建造者实例
         */
        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        /**
         * 设置Content-Type（使用枚举）
         *
         * @param contentType Content-Type枚举
         * @return 建造者实例
         */
        public Builder contentType(HttpContentType contentType) {
            this.contentType = contentType != null ? contentType.value() : null;
            return this;
        }

        /**
         * 设置请求体
         *
         * @param body 请求体内容
         * @return 建造者实例
         */
        public Builder body(String body) {
            this.body = body;
            return this;
        }

        /**
         * 添加请求头
         *
         * @param name 头名称
         * @param value 头值
         * @return 建造者实例
         * @throws IllegalArgumentException 如果名称为空
         */
        public Builder header(String name, String value) {
            Objects.requireNonNull(name, "请求头名称不能为空");
            if (name.trim().isEmpty()) {
                throw new IllegalArgumentException("请求头名称不能为空字符串");
            }

            if (this.headers == null) {
                this.headers = new HashMap<>();
            }
            this.headers.put(name, value);
            return this;
        }

        /**
         * 批量添加请求头
         *
         * @param headers 请求头Map
         * @return 建造者实例
         */
        public Builder headers(Map<String, String> headers) {
            if (headers != null && !headers.isEmpty()) {
                if (this.headers == null) {
                    this.headers = new HashMap<>();
                }
                this.headers.putAll(headers);
            }
            return this;
        }

        /**
         * 添加表单字段
         *
         * @param name 字段名
         * @param value 字段值
         * @return 建造者实例
         * @throws IllegalArgumentException 如果字段名为空
         */
        public Builder formField(String name, String value) {
            Objects.requireNonNull(name, "表单字段名不能为空");
            if (name.trim().isEmpty()) {
                throw new IllegalArgumentException("表单字段名不能为空字符串");
            }

            if (this.form == null) {
                this.form = new HashMap<>();
            }
            this.form.put(name, value);
            return this;
        }

        /**
         * 批量添加表单字段
         *
         * @param fields 表单字段Map
         * @return 建造者实例
         */
        public Builder formFields(Map<String, String> fields) {
            if (fields != null && !fields.isEmpty()) {
                if (this.form == null) {
                    this.form = new HashMap<>();
                }
                this.form.putAll(fields);
            }
            return this;
        }

        /**
         * 添加文件字段
         *
         * @param field 字段名
         * @param file 文件
         * @return 建造者实例
         * @throws IllegalArgumentException 如果字段名为空或文件为空
         */
        public Builder file(String field, File file) {
            Objects.requireNonNull(field, "文件字段名不能为空");
            Objects.requireNonNull(file, "文件不能为空");

            if (field.trim().isEmpty()) {
                throw new IllegalArgumentException("文件字段名不能为空字符串");
            }

            if (this.files == null) {
                this.files = new HashMap<>();
            }
            this.files.put(field, file);
            return this;
        }

        /**
         * 添加查询参数
         *
         * @param name 参数名
         * @param value 参数值
         * @return 建造者实例
         * @throws IllegalArgumentException 如果参数名为空
         */
        public Builder queryParam(String name, String value) {
            Objects.requireNonNull(name, "查询参数名不能为空");
            if (name.trim().isEmpty()) {
                throw new IllegalArgumentException("查询参数名不能为空字符串");
            }

            if (this.query == null) {
                this.query = new LinkedHashMap<>();
            }
            this.query.put(name, value);
            return this;
        }

        /**
         * 批量添加查询参数
         *
         * @param params 查询参数Map
         * @return 建造者实例
         */
        public Builder queryParams(Map<String, String> params) {
            if (params != null && !params.isEmpty()) {
                if (this.query == null) {
                    this.query = new LinkedHashMap<>();
                }
                this.query.putAll(params);
            }
            return this;
        }

        /**
         * 设置重试次数
         *
         * @param retries 重试次数
         * @return 建造者实例
         * @throws IllegalArgumentException 如果重试次数为负数
         */
        public Builder retries(Integer retries) {
            if (retries != null && retries < 0) {
                throw new IllegalArgumentException("重试次数不能为负数");
            }
            this.retries = retries;
            return this;
        }

        /**
         * 设置重试退避时间
         *
         * @param retryBackoff 重试退避时间
         * @return 建造者实例
         * @throws IllegalArgumentException 如果退避时间为负数
         */
        public Builder retryBackoff(Duration retryBackoff) {
            if (retryBackoff != null && retryBackoff.isNegative()) {
                throw new IllegalArgumentException("重试退避时间不能为负数");
            }
            this.retryBackoff = retryBackoff;
            return this;
        }

        /**
         * 构建HttpRequest实例
         *
         * @return HttpRequest实例
         * @throws IllegalStateException 如果必填参数缺失
         */
        public HttpRequest build() {
            if (url == null || url.trim().isEmpty()) {
                throw new IllegalStateException("URL是必填参数，不能为空");
            }

            return new HttpRequest(this);
        }
    }
}
