package com.wjy.personal_blog.pojo.dto;

import lombok.Data;

@Data
public class LoginDTO {
    private String username;
    private String password;
    private Boolean remember; // 添加"记住我"字段
}
