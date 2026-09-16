package com.wjy.personal_blog.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wjy.personal_blog.pojo.dto.UserDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("comment")
public class Comment {
    @TableId(type = IdType.AUTO)
    private Integer commentId;

    @TableField("article_id")
    private Integer articleId;

    @TableField("user_id")
    private Integer userId;

    @TableField("parent_id")
    private Integer parentId;

    @TableField("content")
    private String content;

    @TableField("is_anonymous")
    private Boolean isAnonymous;

    @TableField("anonymous_name")
    private String anonymousName;

    @TableField("comment_status")
    private Integer commentStatus;

    @TableField(value = "create_time",fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time",fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


    /*非数据库字段*/
    /**
     * 评论用户信息（非数据库字段，用于前端展示）
     */
    @TableField(exist = false)
    private UserDTO user;

    /**
     * 回复的用户信息（非数据库字段，用于前端展示）
     */
    @TableField(exist = false)
    private UserDTO replyToUser;

    /**
     * 子评论列表（非数据库字段，用于构建树形结构）
     */
    @TableField(exist = false)
    private List<Comment> children;

}
