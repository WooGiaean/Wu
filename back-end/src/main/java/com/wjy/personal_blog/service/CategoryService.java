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
    List<Category> getAllCategories();

    /**
    * 计算分类下的文章数量
    * */
    List<Map<String, Object>> getCategoriesWithArticleCount();

    /**
    * 添加分类
    * */
    void addCategory(Category category);

    void updateCategory(Category category);

    int countArticlesByCategory(Integer categoryId);

    int countChildCategories(Integer categoryId);

    void deleteCategoryWithChildren(Integer categoryId);

    void deleteCategory(Integer categoryId);

    Category getCategoryById(Integer categoryId);
}
