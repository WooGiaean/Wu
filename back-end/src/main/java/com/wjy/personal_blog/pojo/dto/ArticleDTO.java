package com.wjy.personal_blog.pojo.dto;

import lombok.Data;

import java.util.List;

@Data
public class ArticleDTO {
    private Integer articleId;

    private String articleTitle;

    private String articleContent;

    private Integer articleReadCount;

    private Integer articleCommentCount;

    private Integer articleLikeCount;

   private Integer articleIsComment;

    private Integer articleStatus;

    private String articleSummary;

    private String articleThumbnail;

    // 新增：文章所属分类ID列表
    private List<Integer> categoryIds;
}
