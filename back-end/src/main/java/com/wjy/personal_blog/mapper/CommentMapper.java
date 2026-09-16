package com.wjy.personal_blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wjy.personal_blog.pojo.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    /**
    * 查询具体文章的所有的一级评论
    * */

    @Select("select * from comment where article_id=#{articleId} and parent_id = 0")
    List<Comment> selectAllFirstLevelComments(Integer articleId);

    /**
     * 查询某条评论下的所有子评论
     * */
    @Select("SELECT * FROM comment " +
            "WHERE parent_id = #{commentId} AND comment_status = 1 " +
            "ORDER BY create_time ASC")
    List<Comment> selectChildComments(Integer commentId);

    /**
     * 查询评论及其子评论（递归查询）
     * */
   // List<Comment> selectCommentTree(Integer articleId);
}
