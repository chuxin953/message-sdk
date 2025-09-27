package com.xiangxi.message.email.model;

import com.xiangxi.message.common.annotation.Required;
import com.xiangxi.message.common.util.MessageValidator;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 邮件发送请求
 * 
 * <p>封装邮件发送的请求参数。</p>
 * 
 * @author 初心
 * @since 1.0.0
 */
public class EmailRequest {
    
    /**
     * 收件人邮箱地址
     */
    @Required(message = "收件人邮箱地址不能为空")
    private final String toEmail;
    
    /**
     * 邮件主题
     */
    @Required(message = "邮件主题不能为空")
    private final String subject;
    
    /**
     * 邮件内容
     */
    @Required(message = "邮件内容不能为空")
    private final String content;
    
    /**
     * 抄送邮箱地址列表
     */
    private final List<String> ccEmails;
    
    /**
     * 密送邮箱地址列表
     */
    private final List<String> bccEmails;
    
    /**
     * 发件人邮箱地址
     */
    private final String fromEmail;
    
    /**
     * 发件人姓名
     */
    private final String fromName;
    
    /**
     * 邮件类型（HTML或TEXT）
     */
    private final EmailType emailType;
    
    /**
     * 附件列表
     */
    private final List<EmailAttachment> attachments;
    
    /**
     * 扩展参数
     */
    private final Map<String, Object> properties;
    
    /**
     * 邮件类型枚举
     */
    public enum EmailType {
        HTML("HTML", "HTML格式"),
        TEXT("TEXT", "纯文本格式");
        
        private final String code;
        private final String description;
        
        EmailType(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 构造函数
     */
    private EmailRequest(Builder builder) {
        this.toEmail = builder.toEmail;
        this.subject = builder.subject;
        this.content = builder.content;
        this.ccEmails = builder.ccEmails;
        this.bccEmails = builder.bccEmails;
        this.fromEmail = builder.fromEmail;
        this.fromName = builder.fromName;
        this.emailType = builder.emailType;
        this.attachments = builder.attachments;
        this.properties = builder.properties;
    }
    
    /**
     * 创建简单邮件请求
     * 
     * @param toEmail 收件人邮箱
     * @param subject 主题
     * @param content 内容
     * @return 邮件请求
     */
    public static EmailRequest of(String toEmail, String subject, String content) {
        return new Builder()
                .toEmail(toEmail)
                .subject(subject)
                .content(content)
                .emailType(EmailType.HTML)
                .build();
    }
    
    /**
     * 创建纯文本邮件请求
     * 
     * @param toEmail 收件人邮箱
     * @param subject 主题
     * @param content 内容
     * @return 邮件请求
     */
    public static EmailRequest text(String toEmail, String subject, String content) {
        return new Builder()
                .toEmail(toEmail)
                .subject(subject)
                .content(content)
                .emailType(EmailType.TEXT)
                .build();
    }
    
    /**
     * 创建HTML邮件请求
     * 
     * @param toEmail 收件人邮箱
     * @param subject 主题
     * @param content 内容
     * @return 邮件请求
     */
    public static EmailRequest html(String toEmail, String subject, String content) {
        return new Builder()
                .toEmail(toEmail)
                .subject(subject)
                .content(content)
                .emailType(EmailType.HTML)
                .build();
    }
    
    // Getters
    public String getToEmail() {
        return toEmail;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public String getContent() {
        return content;
    }
    
    public List<String> getCcEmails() {
        return ccEmails;
    }
    
    public List<String> getBccEmails() {
        return bccEmails;
    }
    
    public String getFromEmail() {
        return fromEmail;
    }
    
    public String getFromName() {
        return fromName;
    }
    
    public EmailType getEmailType() {
        return emailType;
    }
    
    public List<EmailAttachment> getAttachments() {
        return attachments;
    }
    
    public Map<String, Object> getProperties() {
        return properties;
    }
    
    /**
     * 获取扩展参数值
     * 
     * @param key 参数键
     * @return 参数值
     */
    public Object getProperty(String key) {
        return properties != null ? properties.get(key) : null;
    }
    
    /**
     * 获取字符串类型的扩展参数
     * 
     * @param key 参数键
     * @return 参数值
     */
    public String getStringProperty(String key) {
        Object value = getProperty(key);
        return value != null ? value.toString() : null;
    }
    
    /**
     * 验证请求参数
     * 
     * @throws IllegalArgumentException 如果参数无效
     */
    public void validate() {
        MessageValidator.validateNotEmpty(toEmail, "收件人邮箱地址");
        MessageValidator.validateNotEmpty(subject, "邮件主题");
        MessageValidator.validateNotEmpty(content, "邮件内容");
        
        if (!MessageValidator.isValidEmail(toEmail)) {
            throw new IllegalArgumentException("收件人邮箱地址格式不正确: " + toEmail);
        }
        
        if (fromEmail != null && !MessageValidator.isValidEmail(fromEmail)) {
            throw new IllegalArgumentException("发件人邮箱地址格式不正确: " + fromEmail);
        }
        
        if (ccEmails != null) {
            for (String ccEmail : ccEmails) {
                if (!MessageValidator.isValidEmail(ccEmail)) {
                    throw new IllegalArgumentException("抄送邮箱地址格式不正确: " + ccEmail);
                }
            }
        }
        
        if (bccEmails != null) {
            for (String bccEmail : bccEmails) {
                if (!MessageValidator.isValidEmail(bccEmail)) {
                    throw new IllegalArgumentException("密送邮箱地址格式不正确: " + bccEmail);
                }
            }
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmailRequest that = (EmailRequest) o;
        return Objects.equals(toEmail, that.toEmail) &&
                Objects.equals(subject, that.subject) &&
                Objects.equals(content, that.content) &&
                Objects.equals(ccEmails, that.ccEmails) &&
                Objects.equals(bccEmails, that.bccEmails) &&
                Objects.equals(fromEmail, that.fromEmail) &&
                Objects.equals(fromName, that.fromName) &&
                emailType == that.emailType &&
                Objects.equals(attachments, that.attachments) &&
                Objects.equals(properties, that.properties);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(toEmail, subject, content, ccEmails, bccEmails, fromEmail, fromName, emailType, attachments, properties);
    }
    
    @Override
    public String toString() {
        return String.format("EmailRequest{toEmail='%s', subject='%s', fromEmail='%s', emailType=%s}", 
                toEmail, subject, fromEmail, emailType);
    }
    
    /**
     * 构建器
     */
    public static class Builder {
        private String toEmail;
        private String subject;
        private String content;
        private List<String> ccEmails;
        private List<String> bccEmails;
        private String fromEmail;
        private String fromName;
        private EmailType emailType = EmailType.HTML;
        private List<EmailAttachment> attachments;
        private Map<String, Object> properties;
        
        public Builder toEmail(String toEmail) {
            this.toEmail = toEmail;
            return this;
        }
        
        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }
        
        public Builder content(String content) {
            this.content = content;
            return this;
        }
        
        public Builder ccEmails(List<String> ccEmails) {
            this.ccEmails = ccEmails;
            return this;
        }
        
        public Builder bccEmails(List<String> bccEmails) {
            this.bccEmails = bccEmails;
            return this;
        }
        
        public Builder fromEmail(String fromEmail) {
            this.fromEmail = fromEmail;
            return this;
        }
        
        public Builder fromName(String fromName) {
            this.fromName = fromName;
            return this;
        }
        
        public Builder emailType(EmailType emailType) {
            this.emailType = emailType;
            return this;
        }
        
        public Builder attachments(List<EmailAttachment> attachments) {
            this.attachments = attachments;
            return this;
        }
        
        public Builder properties(Map<String, Object> properties) {
            this.properties = properties;
            return this;
        }
        
        public EmailRequest build() {
            return new EmailRequest(this);
        }
    }
}
