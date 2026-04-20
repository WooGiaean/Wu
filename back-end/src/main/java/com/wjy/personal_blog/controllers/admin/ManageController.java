package com.wjy.personal_blog.controllers.admin;


import com.wjy.personal_blog.pojo.dto.UserDTO;
import com.wjy.personal_blog.pojo.dto.PageQueryDTO;
import com.wjy.personal_blog.pojo.entity.User;
import com.wjy.personal_blog.result.PageResult;
import com.wjy.personal_blog.result.Result;
import com.wjy.personal_blog.service.ArticleService;
import com.wjy.personal_blog.service.NoteService;
import com.wjy.personal_blog.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/*
* 这个控制层用于管理员管理用户信息、文章信息等功能
* */



@RestController
@RequestMapping("/admin/manage")
@Tag(name = "管理员管理", description = "管理员管理用户、文章、笔记等功能的接口")
@Slf4j
public class ManageController {

    //用户管理
    @Autowired
    private UserService userService;

    //文章管理
    @Autowired
    private ArticleService articleService;

    //笔记管理
    @Autowired
    private NoteService noteService;

    /**
     * 查询所有用户
     * @param pageQueryDTO 分页查询参数
     * @return 分页结果
     */
    //@PreAuthorize("hasRole('admin')") SpringSecurity中进行的权限校验，直接使用注解进行权限校验
    @GetMapping("/users")
    @Operation(summary = "查询所有用户", description = "查询所有用户信息，分页查询")
    public Result<PageResult> allUsers(
             PageQueryDTO pageQueryDTO
    ){

        log.info("开始查询所有用户信息，分页参数：page={}, pageSize={}", pageQueryDTO.getPage(), pageQueryDTO.getPageSize());
        PageResult allUsers = userService.getAllUsers(pageQueryDTO);
        log.info("查询所有用户信息成功，共{}条记录", allUsers.getTotal());
        return Result.success(allUsers);
    }

    /**
     * 查询某个用户
     * @param userDTO 用户查询参数
     * @return 分页结果
     */
    @PostMapping("/search/users")
    @Operation(summary = "查询某个用户", description = "根据用户名查询用户信息，分页查询")
    public Result<PageResult> specificUser(@RequestBody UserDTO userDTO){
        log.info("开始查询用户：{}", userDTO.getUserName());
        PageResult users = userService.findSpecificUser(userDTO);
        log.info("查询用户成功，共{}条记录", users.getTotal());
        return Result.success(users);
    }

    /**
     * 编辑用户信息
     * @param userDTO 用户信息
     * @return 操作结果
     */
    @PutMapping("/update/user")
    @Operation(summary = "编辑用户信息", description = "根据用户ID编辑用户信息")
    public Result updateUser( @RequestBody UserDTO userDTO){
        log.info("开始更新用户信息：{}", userDTO);
        userService.updateUser(userDTO);
        log.info("更新用户信息成功");
        return Result.success();
    }

    /**
     * 删除用户
     * @param userId 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/delete/user")
    @Operation(summary = "删除用户", description = "根据用户ID删除用户")
    public Result deleteUser(@RequestParam("id") Integer userId){
        log.info("开始删除用户，用户ID：{}", userId);
        userService.deleteUser(userId);
        log.info("删除用户成功");
        return Result.success();
    }

    /**
     * 批量删除用户
     * @param ids 用户ID列表
     * @return 操作结果
     */
    @DeleteMapping("/delete/users")
    @Operation(summary = "批量删除用户", description = "根据用户ID列表批量删除用户")
    public Result batchDeleteUsers(@RequestParam("ids") String ids){
        log.info("开始批量删除用户，用户ID列表：{}", ids);
        String[] idArray = ids.split(",");
        Integer[] userIds = new Integer[idArray.length];
        for (int i = 0; i < idArray.length; i++) {
            userIds[i] = Integer.parseInt(idArray[i]);
        }
        userService.batchDeleteUsers(userIds);
        log.info("批量删除用户成功");
        return Result.success();
    }

    /**
     * 添加用户
     * @param userDTO 用户信息
     * @return 操作结果
     */
    @PostMapping("/add/user")
    @Operation(summary = "添加用户", description = "添加用户")
    public Result addUser(@RequestBody UserDTO userDTO){
        log.info("开始添加用户：{}", userDTO.getUserName());
        userService.addUserByAdmin(userDTO);
        log.info("添加用户成功");
        return Result.success();
    }

    /**
     * 修改用户状态
     * @param userDTO 用户状态参数
     * @return 操作结果
     */
    /*@PutMapping("/update/user/status")
    public Result updateUserStatus(@RequestBody UserDTO userDTO){
        log.info("开始修改用户状态：{}", userDTO);
        userService.updateUser(userDTO);
        log.info("修改用户状态成功");
        return Result.success();
    }*/

    /**
     * 查看所有文章
     * @param pageQueryDTO 分页查询参数
     * @return 分页结果
     */
    @GetMapping("/articles")
    @Operation(summary = "查看所有文章", description = "管理员查看所有用户的文章，分页查询")
    public Result<PageResult> allArticles(PageQueryDTO pageQueryDTO)
    {

        log.info("管理员查看所有用户的文章，分页参数：page={}, pageSize={}", pageQueryDTO.getPage(), pageQueryDTO.getPageSize());
        PageResult pageResult = articleService.listByAdmin(pageQueryDTO);
        log.info("查看所有文章成功，共{}条记录", pageResult.getTotal());
        return Result.success(pageResult);
    }

    /**
     * 查看所有笔记
     * @param pageQueryDTO 分页查询参数
     * @return 分页结果
     */
    @GetMapping("/notes")
    @Operation(summary = "查看所有笔记", description = "管理员查看所有用户的笔记，分页查询")
    public Result<PageResult> allNotes(PageQueryDTO pageQueryDTO)
    {

        log.info("管理员查看所有用户的笔记，分页参数：page={}, pageSize={}", pageQueryDTO.getPage(), pageQueryDTO.getPageSize());
        PageResult pageResult = noteService.adminNoteList(pageQueryDTO);
        log.info("查看所有笔记成功，共{}条记录", pageResult.getTotal());
        return Result.success(pageResult);
    }

}
