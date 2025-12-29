package com.wjy.personal_blog.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArticleVO{

    private Integer articleId;

    private String articleTitle;

    private String blogger;

   /* private String articleSummary;

    private Integer articleViewCount;

    private Integer articleCommentCount;

    private Integer articleLikeCount;*/

    private LocalDateTime articleUpdateTime;

    private Integer articleStatus;

    private String articleThumbnail;
}
