package com.wjy.personal_blog.rabbitmq;

import lombok.Data;

/**
 * 邮件消息模型
 */
@Data
public class EmailMessage {
    /**
     * 收件人邮箱
     */
    private String toEmail;

    /**
     * 邮件主题
     */
    private String subject;

    /**
     * 邮件内容
     */
    private String content;

    /**
     * 验证码（用于验证码邮件）
     */
    private String verifyCode;

    /**
     * 邮件类型：verify_code（验证码）、notification（通知）等
     */
    private String emailType;

}
