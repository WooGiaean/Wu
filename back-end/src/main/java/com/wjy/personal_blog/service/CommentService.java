package com.wjy.personal_blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wjy.personal_blog.pojo.dto.CommentDTO;
import com.wjy.personal_blog.pojo.entity.Comment;
import com.wjy.personal_blog.result.PageResult;

import java.util.List;

public interface CommentService  extends IService<Comment> {
    void addComment(CommentDTO commentDTO);

    Comment getDetails(Integer commentId);

    void updateComment(Integer commentId, CommentDTO commentDTO);

    void deleteComment(Integer commentId);

    List<Comment> commentsListUnderArticle(Integer articleId);

    List<Comment> commentsList();

    List<Comment> myCommentsList();
}
