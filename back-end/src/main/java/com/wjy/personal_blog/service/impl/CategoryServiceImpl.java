package com.wjy.personal_blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wjy.personal_blog.exceptions.BusinessException;
import com.wjy.personal_blog.mapper.ArticleCategoryMapper;
import com.wjy.personal_blog.mapper.ArticleMapper;
import com.wjy.personal_blog.mapper.CategoryMapper;
import com.wjy.personal_blog.pojo.entity.ArticleCategory;
import com.wjy.personal_blog.pojo.entity.Category;
import com.wjy.personal_blog.result.Result;
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


    @Autowired
    private CategoryMapper categoryMapper;


    @Autowired
    private ArticleCategoryMapper articleCategoryMapper;

    /**
     * 获取所有分类
     */
    @Override
    public List<Category> getAllCategories() {
        PageHelper.startPage(1, 10);
        List<Category> categories = categoryMapper.selectList(null);
        for (Category category : categories) {
            int articleCount = articleCategoryMapper.countByCategoryId(category.getCategoryId());
            category.setLinkArticleCount(articleCount);
            log.info("分类ID：{}，文章数量：{}", category.getCategoryId(), articleCount);
        }


        return categories;
    }


    /**
     * 获取分类下的文章数量
     */
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

    @Override
    public void addCategory(Category category) {
        // 设置默认值
        if (category.getCategoryOrder() == null) {
            category.setCategoryOrder(1);
        }
        if (category.getCategoryParentId() == null) {
            category.setCategoryParentId(0); // 0 表示顶级分类
        }
        categoryMapper.insert(category);
        log.info("成功添加分类: {}", category.getCategoryName());
    }

    @Override
    public void updateCategory(Category category) {
        if (category.getCategoryId() == null) {
            throw new BusinessException("分类ID不能为空");
        }

        Category ifExist = categoryMapper.selectById(category.getCategoryId());
        if (ifExist == null) {
            throw new BusinessException("分类不存在");
        }

        // 更新非空字段
        if (category.getCategoryName() != null) {
            ifExist.setCategoryName(category.getCategoryName());
        }
        if (category.getCategoryDescription() != null) {
            ifExist.setCategoryDescription(category.getCategoryDescription());
        }
        if (category.getCategoryIcon() != null) {
            ifExist.setCategoryIcon(category.getCategoryIcon());
        }
        if (category.getCategoryOrder() != null) {
            ifExist.setCategoryOrder(category.getCategoryOrder());
        }
        if (category.getCategoryParentId() != null) {
            // 不能设置自己为父分类
            if (!category.getCategoryId().equals(category.getCategoryParentId())) {
                ifExist.setCategoryParentId(category.getCategoryParentId());
            }
        }

        categoryMapper.updateById(ifExist);
        log.info("成功更新分类: {}", category.getCategoryName());


    }

    /*
     * 判断分类下是否有文章
     * */
    @Override
    public int countArticlesByCategory(Integer categoryId) {
        return articleCategoryMapper.countByCategoryId(categoryId);

    }

    /*
     * 判断分类下是否有子分类
     * */
    @Override
    public int countChildCategories(Integer categoryId) {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category_parent_id", categoryId);
        return categoryMapper.selectCount(queryWrapper).intValue();
    }

    @Override
    public void deleteCategoryWithChildren(Integer categoryId) {
        log.info("级联删除分类及其子分类，ID：{}", categoryId);

        // 获取所有子分类（递归删除）
        List<Category> children = getChildCategories(categoryId);

        // 删除子分类
        for (Category child : children) {
            deleteCategoryWithChildren(child.getCategoryId());
        }

        // 删除当前分类
        categoryMapper.deleteById(categoryId);
        log.info("级联删除分类成功，ID：{}", categoryId);

    }

    private List<Category> getChildCategories(Integer parentId) {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category_parent_id", parentId);
        return categoryMapper.selectList(queryWrapper);
    }


    @Override
    public void deleteCategory(Integer categoryId) {
        log.info("删除分类，ID：{}", categoryId);
        categoryMapper.deleteById(categoryId);
        log.info("删除分类成功，ID：{}", categoryId);
    }

    @Override
    public Category getCategoryById(Integer categoryId) {
        Category byId = this.getById(categoryId);
        return byId == null ? null : byId;
    }


}
