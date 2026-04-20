package com.wjy.personal_blog.controllers.user;


import com.wjy.personal_blog.pojo.dto.CommentDTO;
import com.wjy.personal_blog.pojo.entity.Comment;
import com.wjy.personal_blog.result.PageResult;
import com.wjy.personal_blog.result.Result;
import com.wjy.personal_blog.service.CommentService;
import com.wjy.personal_blog.utils.SecurityUtil;
import jakarta.validation.Valid;
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
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
    *  查看自己的所有评论（用户查看）
     * */
    @GetMapping("")
    public Result<PageResult> myComments(){
        List<Comment> list = commentService.myCommentsList();
        PageResult pageResult=new PageResult(list.size(),list);
        return  Result.success(pageResult);
    }

    /**
    * 添加评论
    * */
    @PostMapping("")
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
    public Result<CommentDTO> updateComment(@PathVariable("id")Integer id,@RequestBody @Valid CommentDTO commentDTO){
        commentService.updateComment(id,commentDTO);
        return Result.success();
    }

    /**
     * 删除评论（单条）
     * */
    @DeleteMapping("/{id}")
    public Result deleteComment (@PathVariable("id")Integer id){
        commentService.deleteComment(id);
        return Result.success();
    }
}
