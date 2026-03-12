package com.wjy.personal_blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wjy.personal_blog.mapper.ArticleMapper;
import com.wjy.personal_blog.mapper.CategoryMapper;
import com.wjy.personal_blog.pojo.entity.Category;
import com.wjy.personal_blog.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {


    @Autowired
   private ArticleMapper articleMapper;


    /**
    * 获取所有分类
    * */
    @Override
    public Page<Category> getAllCategories() {
        Page<Category> result = PageHelper.startPage(1, 10);
        this.list();
        log.info("成功获取所有分类");
        return result;
    }



    /**
     * 获取分类下的文章数量
     * */
    @Override
    public List<Map<String, Object>> getCategoriesWithArticleCount() {
        // 获取所有分类
        List<Category> categories = this.list();

        // 获取分类ID列表
        List<Integer> categoryIds = categories.stream()
                .map(Category::getCategoryId)
                .collect(Collectors.toList());

        // 查询每个分类下的文章数量
        List<Map<String, Object>> categoryCount = articleMapper.getArticleCountByCategories(categoryIds);

        // 将分类名称与数量合并
        Map<Integer, Integer> countMap = new HashMap<>();
        for (Map<String, Object> map : categoryCount) {
            Integer categoryId = (Integer) map.get("categoryId");
            Integer count = ((Long) map.get("articleCount")).intValue();
            countMap.put(categoryId, count);
        }

        // 构建结果
        List<Map<String, Object>> result = new ArrayList<>();
        for (Category category : categories) {
            Map<String, Object> item = new HashMap<>();
            item.put("categoryId", category.getCategoryId());
            item.put("categoryName", category.getCategoryName());
            item.put("articleCount", countMap.getOrDefault(category.getCategoryId(), 0));
            result.add(item);
        }

        return result;
    }


}
