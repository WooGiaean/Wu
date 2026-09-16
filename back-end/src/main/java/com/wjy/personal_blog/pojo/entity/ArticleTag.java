package com.wjy.personal_blog.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleTag {
    private static final long serialVersionUID = 1L;
    private Integer articleId;
    private Integer tagId;
}
