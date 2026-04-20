package com.wjy.personal_blog.pojo.dto;

import lombok.Data;

@Data
public class CommentDTO {
    private Integer commentId;
    private Integer articleId;
    private Integer userId;
    private Integer parentId;

    private String content;

    private Boolean isAnonymous;

    private String anonymousName;

    private Integer commentStatus;
}
