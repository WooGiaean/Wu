package com.wjy.personal_blog.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    private Integer categoryId;
    private String categoryName;
    private String categoryDescription;
    private Integer categoryOrder;
    private String categoryIcon;
}
