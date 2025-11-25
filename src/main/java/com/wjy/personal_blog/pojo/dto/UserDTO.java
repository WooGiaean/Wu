package com.wjy.personal_blog.pojo.dto;

import lombok.Data;

@Data
public class UserDTO {

    private String userName;

    private String userPassword;

    private String userNickname;

    private String userEmail;

    private String userUrl;

    private String userAvatar;

    private Integer userStatus;

    private String userRole;
}
