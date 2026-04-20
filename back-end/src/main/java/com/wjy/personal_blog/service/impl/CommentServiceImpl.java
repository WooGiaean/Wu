package com.wjy.personal_blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjy.personal_blog.exceptions.BusinessException;
import com.wjy.personal_blog.mapper.ArticleMapper;
import com.wjy.personal_blog.mapper.CommentMapper;
import com.wjy.personal_blog.pojo.dto.CommentDTO;
import com.wjy.personal_blog.pojo.entity.Article;
import com.wjy.personal_blog.pojo.entity.Comment;
import com.wjy.personal_blog.service.CommentService;
import com.wjy.personal_blog.service.custom.CustomerUserDetails;
import com.wjy.personal_blog.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
        implements CommentService {

    @Autowired
    ArticleMapper articleMapper;


    /*
    设置匿名用户名字
    * */
    private String setAnonymousName() {
        // 使用时间戳的后4位
        long timestamp = System.currentTimeMillis();
        int timestampSuffix = (int) (timestamp % 10000);
        String anonymousName = "匿名用户" + timestampSuffix;
        return anonymousName;
    }

    @Override
    public void addComment(CommentDTO commentDTO) {
        log.info("开始添加评论。。。：{}",commentDTO.getContent());
        // 验证文章是否存在
        Article article = articleMapper.specificArticle(commentDTO.getArticleId());
        if(article==null){
            throw new BusinessException("文章不存在");
        }
        if(article.getArticleStatus()!=1){
            throw new BusinessException("文章未发布");
        }
        if(article.getArticleIsComment()!=1){
            throw new BusinessException("文章未开启评论功能");
        }


        // 设置评论用户ID
        //Integer userId = securityUtil.getCurrentUserId();
        Integer userId = SecurityUtil.getCurrentUserId();
        log.info("用户id：{}",userId);
        if(userId==null){
            commentDTO.setIsAnonymous(true);
            commentDTO.setAnonymousName(setAnonymousName());
            commentDTO.setUserId(null); //设置默认值
        }else{
            commentDTO.setIsAnonymous(false);
            commentDTO.setUserId(userId);
        }

        // 设置默认状态
        if(commentDTO.getCommentStatus() == null) {
            commentDTO.setCommentStatus(0);
        }

        Comment comment=new Comment();
        BeanUtils.copyProperties(commentDTO,comment);
        log.info("用户id：{} 添加了一条评论：{}",comment.getUserId(),comment.getContent());
        this.save(comment);

        //设置文章评论数量
        articleMapper.updateArticleCommentCount(article.getArticleId(),1);
    }

    @Override
    public Comment getDetails(Integer commentId) {
        Comment comment = this.getById(commentId);
        if(comment == null) {
            throw new BusinessException("评论不存在");
        }
        return comment;
    }

    @Override
    public void updateComment(Integer commentId, CommentDTO commentDTO) {
        Comment comment2Update = this.getById(commentId);
        if(comment2Update==null){
            throw new BusinessException("评论不存在");
        }

        // 权限检查：只有评论作者或管理员可以修改评论
        //Integer currentUserId = securityUtil.getCurrentUserId();
        CustomerUserDetails currentUser = SecurityUtil.getCurrentUser();
        Integer currentUserId = currentUser.getUserId();
        String userRole = currentUser.getUserRole();
        if(!comment2Update.getUserId().equals(currentUserId) && !userRole.equals("admin")) {
            throw new BusinessException("无权限修改此评论");
        }

        UpdateWrapper<Comment> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("comment_id", commentId); //修改条件
        updateWrapper.set("content", commentDTO.getContent());  //修改内容
        updateWrapper.set("is_anonymous", commentDTO.getIsAnonymous());
        updateWrapper.set("anonymous_name", commentDTO.getAnonymousName());
        updateWrapper.set("comment_status", commentDTO.getCommentStatus());
        this.update(updateWrapper);
    }

    @Override
    public void deleteComment(Integer commentId) {
        Comment comment = this.getById(commentId);
        if(comment == null) {
            throw new BusinessException("评论不存在");
        }
        // 权限检查：只有评论作者或管理员可以删除评论
        CustomerUserDetails currentUser = SecurityUtil.getCurrentUser();
        Integer currentUserId = currentUser.getUserId();
        String userRole = currentUser.getUserRole();
        if(!comment.getUserId().equals(currentUserId) && !userRole.equals("admin")) {
            throw new BusinessException("无权限删除此评论");
        }

        this.removeById(commentId);
        articleMapper.updateArticleCommentCount(comment.getArticleId(),-1);
    }

    @Override
    public List<Comment> commentsListUnderArticle(Integer articleId) {
        // 验证文章是否存在
        Article article = articleMapper.specificArticle(articleId);
        if(article==null){
            throw new BusinessException("文章不存在");
        }

        QueryWrapper<Comment> wrapper=new QueryWrapper<>();
        wrapper.eq("article_id", articleId);
        wrapper.orderByDesc("create_time"); // 按创建时间降序排列
        return this.list(wrapper);
    }

    /*
    * 所有评论
    * */
    @Override
    public List<Comment> commentsList() {
        Integer currentUserId = SecurityUtil.getCurrentUserId();
        String userRole = SecurityUtil.getCurrentUser().getUserRole();
        if(currentUserId==null||!userRole.equals("admin")){
            throw new BusinessException("无此权限");
            //return null;
        }
        return this.list();
    }

    @Override
    public List<Comment> myCommentsList() {
        Integer currentUserId = SecurityUtil.getCurrentUserId();
        if(currentUserId==null){
            throw new BusinessException("用户未登录");
        }
        QueryWrapper<Comment> wrapper=new QueryWrapper<>();
        wrapper.eq("user_id", currentUserId);
        wrapper.orderByDesc("create_time");
        return this.list(wrapper);
    }
}
