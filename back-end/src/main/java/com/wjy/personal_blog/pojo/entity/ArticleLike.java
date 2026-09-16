package com.wjy.personal_blog.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("article_like")
public class ArticleLike implements Serializable {
    @TableId(value = "like_id", type = IdType.AUTO)
    private Integer likeId;

    @TableField("article_id")
    private Integer articleId;

    @TableField("user_id")
    private Integer userId;

    @TableField("create_time")
    private LocalDateTime createTime;
}
