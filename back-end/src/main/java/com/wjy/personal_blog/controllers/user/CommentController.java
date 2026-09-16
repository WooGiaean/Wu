package com.wjy.personal_blog.controllers.user;


import com.wjy.personal_blog.pojo.dto.CommentDTO;
import com.wjy.personal_blog.pojo.entity.Comment;
import com.wjy.personal_blog.result.PageResult;
import com.wjy.personal_blog.result.Result;
import com.wjy.personal_blog.service.CommentService;
import com.wjy.personal_blog.utils.SecurityUtil;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//import javax.validation.Valid;
import java.util.List;

/*
* 评论功能控制层
* */
@RestController("userCommentController")
@RequestMapping("/user/comments")
@Slf4j
@Tag(name = "评论管理", description = "用户评论相关接口")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
    *  查看自己的所有评论（用户查看）
     * */
    @GetMapping("")
    @Operation(summary = "查看我的评论", description = "查看当前用户的所有评论")
    public Result<PageResult> myComments(){
        List<Comment> list = commentService.myCommentsList();
        PageResult pageResult=new PageResult(list.size(),list);
        return  Result.success(pageResult);
    }

    /**
    * 添加评论
    * */
    @PostMapping("")
    @Operation(summary = "添加评论", description = "添加新评论")
    public Result<CommentDTO> addComment(@RequestBody CommentDTO commentDTO){
        log.info("来到添加评论接口");
        commentService.addComment(commentDTO);
        return Result.success(commentDTO);
       }

    /**
    *  获取评论详情
     *  @param id 评论id
     * @return comment实体（DTO）
     * */
    @GetMapping("/{id}")
    @Operation(summary = "获取评论详情", description = "根据评论ID获取评论详情")
    public Result<Comment> getCommentDetails(@PathVariable("id")Integer id){
        Comment comment = commentService.getDetails(id);
        return Result.success(comment);
    }

    /**
     * 修改评论
     @params 评论id
     @return 更改后的comment实体
     * */
    @PutMapping("/{id}")
    @Operation(summary = "修改评论", description = "修改指定评论内容")
    public Result<CommentDTO> updateComment(@PathVariable("id")Integer id,@RequestBody @Valid CommentDTO commentDTO){
        commentService.updateComment(id,commentDTO);
        return Result.success();
    }

    /**
     * 删除评论（单条）
     * */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除评论", description = "删除指定评论")
    public Result deleteComment (@PathVariable("id")Integer id){
        commentService.deleteComment(id);
        return Result.success();
    }


    /**
    * 获取评论列表
    * */
    @GetMapping("/article/{articleId}/comments")
    @Operation(summary = "获取文章评论", description = "根据文章ID获取评论列表")
    public Result<PageResult> getArticleComments(@PathVariable Integer articleId) {
        List<Comment> comments = commentService.getCommentTree(articleId);
        PageResult pageResult = new PageResult(comments.size(), comments);
        return Result.success(pageResult);
    }


    /**
    * 获取评论的子评论列表
    * */
    @GetMapping("/comments/{commentId}/children")
    @Operation(summary = "获取子评论", description = "获取指定评论的子评论列表")
    public Result<List<Comment>> getChildComments(@PathVariable Integer commentId) {
        List<Comment> children = commentService.childCommentsUnderComment(commentId);
        return Result.success(children);
    }
}
