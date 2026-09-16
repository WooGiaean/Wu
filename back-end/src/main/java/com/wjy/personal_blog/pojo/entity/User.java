package com.wjy.personal_blog.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class User implements Serializable {

    public static final String USER_ROLE_ADMIN = "admin";
    public static final String USER_ROLE_USER = "user";

    private static final long serialVersionUID = -4415517704211731385L;
    private Integer userId;

    private String userName;

    private String userPassword;

    private String userNickname;

    private String userEmail;

    private String userUrl;

    private String userAvatar;

    private String userLastLoginIp;

    private LocalDateTime userRegisterTime;

    private LocalDateTime userLastLoginTime;

    private Integer userStatus;

    /**
     * 用户角色：admin/user
     */
    private String userRole;

    /**
     * 文章数量（不是数据库字段）
     */
    @TableField(exist = false)
    private Integer articleCount;

    /**
    * 笔记数量（非数据库字段）
    * */ @TableField(exist = false)
    private Integer noteCount;

    /**
     * 评论数量（非数据库字段）
     *
     * */
    @TableField(exist = false)
    private Integer commentCount;
}
