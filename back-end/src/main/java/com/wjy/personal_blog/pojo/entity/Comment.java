package com.wjy.personal_blog.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

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

}
