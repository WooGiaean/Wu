package com.wjy.personal_blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.wjy.personal_blog.exceptions.BusinessException;
import com.wjy.personal_blog.mapper.ArticleMapper;
import com.wjy.personal_blog.mapper.CommentMapper;
import com.wjy.personal_blog.mapper.UserMapper;
import com.wjy.personal_blog.pojo.dto.CommentDTO;
import com.wjy.personal_blog.pojo.dto.PageQueryDTO;
import com.wjy.personal_blog.pojo.dto.UserDTO;
import com.wjy.personal_blog.pojo.entity.Article;
import com.wjy.personal_blog.pojo.entity.Comment;
import com.wjy.personal_blog.pojo.entity.User;
import com.wjy.personal_blog.result.PageResult;
import com.wjy.personal_blog.service.CommentService;
import com.wjy.personal_blog.service.custom.CustomerUserDetails;
import com.wjy.personal_blog.service.custom.UserStatisticsService;
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


    @Autowired
    private UserStatisticsService userStatisticsService;


    @Autowired
    private CommentMapper commentMapper;


    @Autowired
    private UserMapper userMapper;

    /**
    * 设置匿名用户名字
    * */
    private String setAnonymousName() {
        // 使用时间戳的后4位
        long timestamp = System.currentTimeMillis();
        int timestampSuffix = (int) (timestamp % 10000);
        String anonymousName = "匿名用户" + timestampSuffix;
        return anonymousName;
    }

    /*
    * 添加评论
    * */
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

        Integer parentId = commentDTO.getParentId();

        //有父级评论，是回复评论。
        if(parentId!=null&&parentId>0){
            Comment parentComment = commentMapper.selectById(parentId);
            if(parentComment==null){
                throw new BusinessException("父评论不存在");
            }
            //判断是否在同一篇文章
            if (!parentComment.getArticleId().equals(commentDTO.getArticleId())) {
                throw new BusinessException("无法回复其他文章的评论");
            }

            commentDTO.setParentId(parentId);
        }else{
            //新增一级评论
            commentDTO.setParentId(null);
        }

        // 设置评论用户ID
        Integer userId = SecurityUtil.getCurrentUserId();
        log.info("用户id：{}",userId);
        // 匿名用户（未登录/游客）
        if(userId==null){
            commentDTO.setIsAnonymous(true);
            commentDTO.setAnonymousName(setAnonymousName());
            commentDTO.setUserId(null); //设置默认值
        }else{
            // 已登录
            commentDTO.setIsAnonymous(false);
            commentDTO.setUserId(userId);
        }

        // 设置默认状态
        if(commentDTO.getCommentStatus() == null) {
            commentDTO.setCommentStatus(1);
        }

        Comment comment=new Comment();
        BeanUtils.copyProperties(commentDTO,comment);
        log.info("用户id：{} 添加了一条评论：{}",comment.getUserId(),comment.getContent());
        this.save(comment);
        //更新统计数据
        if(userId!=null){
            userStatisticsService.updateCommentCount(userId);
        }
        //更新文章评论数量
        articleMapper.updateArticleCommentCount(article.getArticleId(),1);
    }

    /*
    * 查询评论详情
       * */
    @Override
    public Comment getDetails(Integer commentId) {
        Comment comment = this.getById(commentId);
        if(comment == null) {
            throw new BusinessException("评论不存在");
        }
        return comment;
    }

    /*
    * 更新评论
    * */
    @Override
    public void updateComment(Integer commentId, CommentDTO commentDTO) {
        //1.获取评论对象
        Comment comment2Update = this.getById(commentId);
        if(comment2Update==null){
            throw new BusinessException("评论不存在");
        }

        // 权限检查：只有评论作者或管理员可以修改评论
        Integer currentUserId = SecurityUtil.getCurrentUserId();
        if(currentUserId==null){
            throw new BusinessException("请先登录");
        }
        CustomerUserDetails currentUser = SecurityUtil.getCurrentUser();
        String userRole = currentUser.getUserRole();

        //判断是否是作者
        boolean isAuthor = comment2Update.getUserId().equals(currentUserId);
        boolean isAdmin = userRole.equals("admin");

        if(!isAuthor && !isAdmin) {
            throw new BusinessException("无权限修改此评论");
        }

        //构建更新条件（只修改内容和状态）
        UpdateWrapper<Comment> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("comment_id", commentId); //修改条件

        if(commentDTO.getContent()!=null &&
                !commentDTO.getContent().trim().isEmpty()){
            updateWrapper.set("content", commentDTO.getContent());  //修改内容
        }

        if (commentDTO.getCommentStatus() != null) {
            updateWrapper.set("comment_status", commentDTO.getCommentStatus());
        }

       /* updateWrapper.set("is_anonymous", commentDTO.getIsAnonymous());
        updateWrapper.set("anonymous_name", commentDTO.getAnonymousName());
        updateWrapper.set("comment_status", commentDTO.getCommentStatus());*/
        this.update(updateWrapper);
        log.info("用户id：{} 修改了一条评论：{}",currentUserId,comment2Update.getContent());
    }

    /*
    * 删除评论
    * */
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

    /*
    * 查询具体文章的所有的一级评论
    * */
    @Override
    public List<Comment> commentsListUnderArticle(Integer articleId) {
        // 验证文章是否存在
        Article article = articleMapper.specificArticle(articleId);
        if(article==null){
            throw new BusinessException("文章不存在");
        }

        QueryWrapper<Comment> wrapper=new QueryWrapper<>();
        wrapper.eq("article_id", articleId);
        //wrapper.eq("parent_id", 0);
        wrapper.orderByDesc("create_time"); // 按创建时间降序排列
        return this.list(wrapper);
    }

    /*
    * 查询所有评论（管理员权限）
    * */
    @Override
    public List<Comment> commentsList() {
        Integer currentUserId = SecurityUtil.getCurrentUserId();
        String userRole = SecurityUtil.getCurrentUser().getUserRole();
        if(currentUserId==null||!userRole.equals("admin")){
            throw new BusinessException("无此权限");
        }
        return this.list();
    }

    /*
    * 查询用户自己发表的评论
    * */
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


    /*
    * 管理员查询所有评论
    * */
    @Override
    public PageResult adminCommentList(PageQueryDTO pageQueryDTO) {
        int page = pageQueryDTO.getPage() != null ? pageQueryDTO.getPage() : 1;
        int pageSize = pageQueryDTO.getPageSize() != null ? pageQueryDTO.getPageSize() : 10;

        PageHelper.startPage(page, pageSize);
        log.info("管理员查询所有评论");
        List<Comment> list = this.list();
        if(!list.isEmpty()){
            log.info("管理员查询所有评论，结果：{}", list);
            PageResult pageResult=new PageResult(list.size(),list);
            return pageResult;
        }else{
            log.info("管理员查询所有评论，结果为空");
            return new PageResult(0,null);
        }
    }

    /*
    * 统计所有评论的数量
    * */
    @Override
    public int countComments() {
        int count = commentMapper.selectCount(null).intValue();
        return count;
    }

    /*
    * 获取具体文章下所有的一级评论
    * */
    @Override
    public List<Comment> firstLevelCommentsUnderArticle(Integer articleId) {
        // 验证文章是否存在
        Article article = articleMapper.specificArticle(articleId);
        if(article==null){
            throw new BusinessException("文章不存在");
        }
        return commentMapper.selectAllFirstLevelComments(articleId);
    }

    /*
    * 获取某条评论的所有子评论
    * */
    @Override
    public List<Comment> childCommentsUnderComment(Integer commentId) {
        List<Comment> children = commentMapper.selectChildComments(commentId);
        for (Comment child : children) {
            fillCommentInfo(child);
        }
        return children;
    }

    /*
    * 查询评论树（包含一级评论和所有子评论）
    * */
    @Override
    public List<Comment> getCommentTree(Integer articleId) {

        List<Comment> comments = commentMapper.selectAllFirstLevelComments(articleId);

        // 递归填充子评论和用户信息
        for (Comment comment : comments) {
            fillCommentInfo(comment);
        }

        return comments;
    }


    /**
    * 递归填充评论信息（用户信息 + 子评论）
    * */
    private void fillCommentInfo(Comment comment) {
        // 填充评论用户信息
        Integer userId = comment.getUserId();
        if (userId != null) {
            User user = userMapper.getUserById(userId);
            if (user != null) {
                UserDTO userDTO = new UserDTO();
                userDTO.setUserId(user.getUserId());
                userDTO.setUserName(user.getUserName());
                userDTO.setUserNickname(user.getUserNickname());
                userDTO.setUserAvatar(user.getUserAvatar());
                comment.setUser(userDTO);
            }
        }

        // 填充被回复用户信息（如果是回复）
        Integer parentId = comment.getParentId();
        if (parentId != null && parentId > 0) {
            Comment parentComment = commentMapper.selectById(parentId);
            if (parentComment != null) {
                User replyToUser = userMapper.getUserById(parentComment.getUserId());
                if (replyToUser != null) {
                    UserDTO replyToUserDTO = new UserDTO();
                    replyToUserDTO.setUserId(replyToUser.getUserId());
                    replyToUserDTO.setUserName(replyToUser.getUserName());
                    replyToUserDTO.setUserNickname(replyToUser.getUserNickname());
                    comment.setReplyToUser(replyToUserDTO);
                }
            }
        }

        // 递归填充子评论信息
        List<Comment> children = commentMapper.selectChildComments(comment.getCommentId());
        if (children != null && !children.isEmpty()) {
            comment.setChildren(children);
            for (Comment child : children) {
                fillCommentInfo(child);
            }
        }
    }

}
