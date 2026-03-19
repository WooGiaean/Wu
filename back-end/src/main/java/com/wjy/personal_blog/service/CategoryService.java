package com.wjy.personal_blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.Page;
import com.wjy.personal_blog.pojo.entity.Category;

import java.util.List;
import java.util.Map;

public interface CategoryService extends IService<Category> {

    /**
    * 获取所有分类
    * */
    Page<Category> getAllCategories();

    /**
    * 计算分类下的文章数量
    * */
    List<Map<String, Object>> getCategoriesWithArticleCount();
}
