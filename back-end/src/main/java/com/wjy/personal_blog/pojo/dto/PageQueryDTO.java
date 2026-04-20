package com.wjy.personal_blog.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageQueryDTO {

    //当前页码
    private Integer page;

    //页面大小（每页显示记录数）
    private Integer pageSize;
}
