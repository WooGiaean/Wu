package com.wjy.personal_blog.pojo.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Article implements Serializable {
    private static final long serialVersionUID = 5207865247400761539L;

    private Integer articleId;

    private Integer articleUserId;

    private String articleTitle;

    private Integer articleReadCount;

    private Integer articleCommentCount;

    private Integer articleLikeCount;

    private LocalDateTime articleCreateTime;

    private LocalDateTime articleUpdateTime;

    private Integer articleIsComment;

    private Integer articleStatus;


    private String articleContent;

    private String articleSummary;

    private String articleThumbnail;

    private Integer articleOrder;


    /* blogger 是博客作者的用户名，非数据库字段 */
    private String blogger;

    /* tagList 和 categoryList 不是数据库字段 */
    private List<Tag> tagList;

    private List<Category> categoryList;

}
