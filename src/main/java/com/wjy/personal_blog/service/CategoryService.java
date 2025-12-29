package com.wjy.personal_blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.Page;
import com.wjy.personal_blog.pojo.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {

    Page<Category> getAllCategories();
}
