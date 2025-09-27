package com.xiangxi.message.email.model;

import java.util.Objects;

/**
 * 邮件附件
 * 
 * <p>封装邮件附件的相关信息。</p>
 * 
 * @author 初心
 * @since 1.0.0
 */
public class EmailAttachment {
    
    /**
     * 附件名称
     */
    private final String fileName;
    
    /**
     * 附件内容（Base64编码）
     */
    private final String content;
    
    /**
     * 附件类型（MIME类型）
     */
    private final String contentType;
    
    /**
     * 附件大小（字节）
     */
    private final long size;
    
    /**
     * 构造函数
     * 
     * @param fileName 附件名称
     * @param content 附件内容（Base64编码）
     * @param contentType 附件类型
     * @param size 附件大小
     */
    public EmailAttachment(String fileName, String content, String contentType, long size) {
        this.fileName = fileName;
        this.content = content;
        this.contentType = contentType;
        this.size = size;
    }
    
    /**
     * 创建文本附件
     * 
     * @param fileName 附件名称
     * @param content 附件内容
     * @return 文本附件
     */
    public static EmailAttachment text(String fileName, String content) {
        return new EmailAttachment(fileName, content, "text/plain", content.getBytes().length);
    }
    
    /**
     * 创建HTML附件
     * 
     * @param fileName 附件名称
     * @param content 附件内容
     * @return HTML附件
     */
    public static EmailAttachment html(String fileName, String content) {
        return new EmailAttachment(fileName, content, "text/html", content.getBytes().length);
    }
    
    /**
     * 创建图片附件
     * 
     * @param fileName 附件名称
     * @param content 附件内容（Base64编码）
     * @param contentType 图片类型
     * @return 图片附件
     */
    public static EmailAttachment image(String fileName, String content, String contentType) {
        return new EmailAttachment(fileName, content, contentType, 0);
    }
    
    /**
     * 获取附件名称
     * 
     * @return 附件名称
     */
    public String getFileName() {
        return fileName;
    }
    
    /**
     * 获取附件内容
     * 
     * @return 附件内容（Base64编码）
     */
    public String getContent() {
        return content;
    }
    
    /**
     * 获取附件类型
     * 
     * @return 附件类型（MIME类型）
     */
    public String getContentType() {
        return contentType;
    }
    
    /**
     * 获取附件大小
     * 
     * @return 附件大小（字节）
     */
    public long getSize() {
        return size;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmailAttachment that = (EmailAttachment) o;
        return size == that.size &&
                Objects.equals(fileName, that.fileName) &&
                Objects.equals(content, that.content) &&
                Objects.equals(contentType, that.contentType);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(fileName, content, contentType, size);
    }
    
    @Override
    public String toString() {
        return String.format("EmailAttachment{fileName='%s', contentType='%s', size=%d}", 
                fileName, contentType, size);
    }
}
