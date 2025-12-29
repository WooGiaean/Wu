package com.wjy.personal_blog.pojo.dto;

import lombok.Data;

@Data
public class UserPageQueryDTO {

    private String userName;

    //当前页码
    private int page;

    //页面大小（每页显示记录数）
    private int pageSize;
}
