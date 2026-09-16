package com.wjy.personal_blog.pojo.dto;

import lombok.Data;

@Data
public class UserStatisticsDTO {
    private Integer articleCount;
    private Integer noteCount;
    private Integer commentCount;
}
