package com.wjy.personal_blog.pojo.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Integer userId;

    private String userName;

    private String userPassword;

    private String userNickname;

    private String userEmail;

    private String userUrl;

    private String userAvatar;

    private Integer userStatus; // 0-禁用，1-正常

    private String userRole;

    private String captchaCode; // 验证码

}
