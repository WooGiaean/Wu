package com.wjy.personal_blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wjy.personal_blog.mapper.CategoryMapper;
import com.wjy.personal_blog.pojo.entity.Category;
import com.wjy.personal_blog.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

   /* @Autowired
    private CategoryMapper categoryMapper;
*/


    /*
    * 获取所有分类
    * */
    @Override
    public Page<Category> getAllCategories() {
        //Page<Category> categories = categoryMapper.categoryList();
        Page<Category> result = PageHelper.startPage(1, 10);
        this.list();
        log.info("成功获取所有分类");
        return result;
    }



}
