package com.wjy.personal_blog.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("article_collect")
public class ArticleCollect implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "collect_id", type = IdType.AUTO)
    private Integer collectId;

    @TableField("article_id")
    private Integer articleId;

    @TableField("user_id")
    private Integer userId;

    @TableField("create_time")
    private LocalDateTime createTime;
}
