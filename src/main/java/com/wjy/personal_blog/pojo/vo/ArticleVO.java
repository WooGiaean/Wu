package com.wjy.personal_blog.pojo.vo;

import lombok.Data;

@Data
public class ArticleVO{
    private String userName;

    private String articleTitle;

    private String articleContent;

    private String articleSummary;

    private Integer articleViewCount;

    private Integer articleCommentCount;

    private Integer articleLikeCount;

    private String articleThumbnail;
}
