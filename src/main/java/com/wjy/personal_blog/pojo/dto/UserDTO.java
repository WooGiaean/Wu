package com.wjy.personal_blog.pojo.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Integer userId;

    private String userName;

    private String userPassword;

    private String userNickname;

    private String userEmail;

    private Integer userStatus;

}
