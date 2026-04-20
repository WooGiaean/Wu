package com.wjy.personal_blog.pojo.dto;

import lombok.Data;

@Data
public class VerifyCodeDTO {
    private String email;
    private String code;
    private String newPassword;
}
