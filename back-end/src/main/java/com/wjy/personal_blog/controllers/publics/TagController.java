package com.wjy.personal_blog.controllers.publics;

import com.wjy.personal_blog.pojo.entity.Tag;
import com.wjy.personal_blog.result.PageResult;
import com.wjy.personal_blog.result.Result;
import com.wjy.personal_blog.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("publicTagController")
@RequestMapping("/tags")
@Slf4j
public class TagController {
    @Autowired
    private TagService tagService;


    // 查询所有标签
    @GetMapping("/list")
    public Result<PageResult> getAllTags() {
        try {
            List<Tag> tags = tagService.getAllTags();
            PageResult pageResult = new PageResult(tags.size(), tags);
            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.error("服务器错误: " + e.getMessage());
        }
    }

}
