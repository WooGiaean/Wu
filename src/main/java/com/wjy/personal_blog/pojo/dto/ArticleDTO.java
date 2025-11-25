package com.wjy.personal_blog.pojo.dto;

import lombok.Data;

@Data
public class ArticleDTO {
    private Integer articleId;

    private String articleTitle;

    private String articleContent;

   /* private Integer articleViewCount;

    private Integer articleCommentCount;

    private Integer articleLikeCount;*/

   private Integer articleIsComment;

    private Integer articleStatus;

    private String articleSummary;

    private String articleThumbnail;
}
