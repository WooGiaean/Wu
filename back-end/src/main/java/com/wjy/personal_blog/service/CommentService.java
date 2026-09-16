package com.wjy.personal_blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wjy.personal_blog.pojo.dto.CommentDTO;
import com.wjy.personal_blog.pojo.dto.PageQueryDTO;
import com.wjy.personal_blog.pojo.entity.Comment;
import com.wjy.personal_blog.result.PageResult;

import java.util.List;

public interface CommentService  extends IService<Comment> {
    void addComment(CommentDTO commentDTO);

    Comment getDetails(Integer commentId);

    void updateComment(Integer commentId, CommentDTO commentDTO);

    void deleteComment(Integer commentId);

    // 查询具体文章的所有的评论
    List<Comment> commentsListUnderArticle(Integer articleId);

    /*
    * 管理员查看所有的评论
    *  */
    List<Comment> commentsList();

    List<Comment> myCommentsList();

    PageResult adminCommentList(PageQueryDTO pageQueryDTO);

    int countComments();

    //获取具体文章下所有的一级评论
    List<Comment> firstLevelCommentsUnderArticle(Integer articleId);


    //获取某条评论的所有子评论
    List<Comment> childCommentsUnderComment(Integer commentId);


    List<Comment> getCommentTree(Integer articleId);
}
