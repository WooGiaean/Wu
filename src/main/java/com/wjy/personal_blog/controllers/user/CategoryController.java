package com.wjy.personal_blog.controllers.user;

import com.github.pagehelper.Page;
import com.wjy.personal_blog.pojo.entity.Category;
import com.wjy.personal_blog.result.PageResult;
import com.wjy.personal_blog.result.Result;
import com.wjy.personal_blog.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {


    @Autowired
    private CategoryService categoryService;

    /*
    * 展示分类列表
    * */
    @GetMapping("/list")
    public Result<PageResult> getCategoryList()
    {
        log.info("获取分类列表");
        Page<Category> allCategories = categoryService.getAllCategories();
        PageResult pageResult=new PageResult(allCategories.getTotal(),allCategories.getResult());
        return Result.success(pageResult);
    }


    /*
    * 根据分类查询文章
    * */

    @PostMapping("/articles")
    public Result<PageResult> getArticlesByCategory(Integer categoryId)
    {
        log.info("根据分类查询文章");
        return null;
    }

}
