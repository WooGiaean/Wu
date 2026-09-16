package com.wjy.personal_blog.controllers.user;

import com.github.pagehelper.Page;
import com.wjy.personal_blog.pojo.dto.PageQueryDTO;
import com.wjy.personal_blog.pojo.entity.Category;
import com.wjy.personal_blog.result.PageResult;
import com.wjy.personal_blog.result.Result;
import com.wjy.personal_blog.service.ArticleService;
import com.wjy.personal_blog.service.CategoryService;
import com.wjy.personal_blog.utils.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController("userCategoryController")
@RequestMapping("/user/category")
@Slf4j
@Tag(name = "分类管理", description = "用户分类相关接口")
public class CategoryController {


    @Autowired
    private CategoryService categoryService;


    @Autowired
    private ArticleService articleService;



    /**
    * 根据分类查询文章
    * */

    @GetMapping("/articles")
    @Operation(summary = "根据分类查询文章", description = "根据分类ID查询文章列表")
    public Result<PageResult> getArticlesByCategory(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("根据分类查询文章，分类ID：{}，页码：{}，每页数量：{}", categoryId, page, pageSize);
        PageQueryDTO pageQueryDTO = new PageQueryDTO();
        pageQueryDTO.setPage(page);
        pageQueryDTO.setPageSize(pageSize);
        Integer currentUserId = SecurityUtil.getCurrentUserId();
        PageResult pageResult = articleService.listArticlesByCategory(currentUserId,categoryId, pageQueryDTO);
        return Result.success(pageResult);
    }


    /**
     * 展示分类列表
     * */
    @GetMapping("/list")
    @Operation(summary = "获取分类列表", description = "获取所有分类列表")
    public Result<PageResult> getCategoryList()
    {
        log.info("获取分类列表");
        List<Category> allCategories = categoryService.getAllCategories();
        PageResult pageResult=new PageResult(allCategories.size(),allCategories);
        return Result.success(pageResult);
    }


}
