package com.wjy.personal_blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.wjy.personal_blog.pojo.entity.Article;
import com.wjy.personal_blog.pojo.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    @Select("select * from category where category_id=#{categoryId}")
    Category getCategoryById(Integer categoryId);


}
