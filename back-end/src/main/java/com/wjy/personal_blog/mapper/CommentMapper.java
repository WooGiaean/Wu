package com.wjy.personal_blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wjy.personal_blog.pojo.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

}
