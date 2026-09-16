package com.wjy.personal_blog.controllers.admin;


import com.wjy.personal_blog.pojo.dto.UserDTO;
import com.wjy.personal_blog.pojo.dto.PageQueryDTO;
import com.wjy.personal_blog.pojo.entity.Article;
import com.wjy.personal_blog.pojo.entity.Category;
import com.wjy.personal_blog.pojo.entity.Notes;
import com.wjy.personal_blog.pojo.entity.User;
import com.wjy.personal_blog.result.PageResult;
import com.wjy.personal_blog.result.Result;
import com.wjy.personal_blog.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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


    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CommentService commentService;


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

        List<Integer> userIdList = new ArrayList<>();
        for (int i = 0; i < idArray.length; i++) {

            userIdList.add(Integer.parseInt(idArray[i]));
        }
        userService.batchDeleteUsers(userIdList);
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
     * 查询文章列表（按置顶排序，时间倒序）
     * @return 文章列表
     **/
    @GetMapping("/articles/top")
    @Operation(summary = "查询文章列表（按置顶排序，时间倒序）", description = "管理员查询所有用户的文章，按置顶排序，时间倒序")
    public Result<PageResult> topArticles()
    {
        log.info("管理员查询所有用户的文章，按置顶排序，时间倒序");
        List<Article> articles = articleService.listArticlesOrderByTop();
        PageResult pageResult = new PageResult(articles.size(), articles);
        return Result.success(pageResult);
    }


    /**
     * 设置/取消文章置顶
     * */
    @PostMapping("/article/{id}/top")
    @Operation(summary = "设置文章置顶状态")
    public Result<String> setArticleTop(
            @PathVariable("id") Integer articleId,
            @RequestParam boolean isTop) {

        log.info("管理员设置文章 {} 置顶状态为 {}", articleId, isTop);

        articleService.setArticleTop(articleId, isTop);

        return Result.success(isTop ? "文章已置顶" : "文章已取消置顶");
    }


    /**
     * 批量更新文章排序
     * */
    @PostMapping("/articles/order")
    @Operation(summary = "批量更新文章排序")
    public Result<String> batchUpdateArticleOrder(@RequestBody List<Integer> articleIds) {

        log.info("批量更新文章排序：{}", articleIds);

        articleService.batchSetArticleOrder(articleIds);

        return Result.success("排序更新成功");
    }



    /**
     * 管理员查看具体文章
     * @param articleId 文章ID
     * @return 文章详情
     * */
    @GetMapping("/article")
    @Operation(summary = "查看所有文章", description = "管理员查看具体的文章")
    public Result<Article> allArticles(@RequestParam("id")Integer articleId)
    {

        log.info("管理员查询单条文章，ID为 {}", articleId);
        Article article = articleService.specificArticle(articleId);
        if(article == null){
            log.warn("文章不存在，ID：{}", articleId);
            return Result.error("文章不存在");
        }
       return Result.success(article);
    }

    /**
     * 删除单篇文章
     * @param articleId 文章ID
     * @return 操作结果
     */
    @DeleteMapping("/delete/article")
    @Operation(summary = "删除文章", description = "管理员删除指定文章")
    public Result deleteArticle(@RequestParam("id") Integer articleId) {
        log.info("管理员删除文章，ID：{}", articleId);
        articleService.deleteArticleByAdmin(articleId);
        log.info("管理员删除文章成功");
        return Result.success();
    }


    /**
     * 批量删除文章
     * @param ids 文章ID列表（逗号分隔）
     * @return 操作结果
     */
    @DeleteMapping("/delete/articles")
    @Operation(summary = "批量删除文章", description = "管理员批量删除文章")
    public Result batchDeleteArticles(@RequestParam("ids") String ids) {
        log.info("管理员批量删除文章，ID列表：{}", ids);
        String[] idArray = ids.split(",");
        //Integer[] articleIds = new Integer[idArray.length];
        List<Integer> articleIdList = new ArrayList<>();
        for (int i = 0; i < idArray.length; i++) {
            articleIdList.add(Integer.parseInt(idArray[i].trim()));
        }
        articleService.batchDeleteArticlesByAdmin(articleIdList);
        log.info("管理员批量删除文章成功，共删除 {} 篇", articleIdList.size());
        return Result.success();
    }

    /**
     * 管理员搜索文章
     */
    @PostMapping("/search/articles")
    @Operation(summary = "搜索文章", description = "管理员根据关键字搜索文章（使用全文索引）")
    public Result<PageResult> searchArticles(@RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int pageSize,
                                             @RequestParam(required = false) String keyword) {
        log.info("管理员搜索文章，关键字：{}，页码：{}，每页数量：{}", keyword, page, pageSize);
        List<Article> articles = articleService.searchArticles(keyword, page, pageSize);
        PageResult pageResult = new PageResult(articles.size(), articles);
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

    /**
     * 查看单条笔记
     * @param noteId 笔记ID
     * @return 笔记详情
     */
    @GetMapping("/note")
    @Operation(summary = "查看单条笔记", description = "管理员查看指定笔记的详细信息")
    public Result<Notes> getNote(@RequestParam("id") Integer noteId) {
        log.info("管理员查看笔记，ID：{}", noteId);
        Notes note = noteService.getNoteByAdmin(noteId);
        if (note == null) {
            log.warn("笔记不存在，ID：{}", noteId);
            return Result.error("笔记不存在");
        }
        return Result.success(note);
    }

    /**
     * 删除笔记
     * @param noteId 笔记ID
     * @return 操作结果
     */
    @DeleteMapping("/delete/note")
    @Operation(summary = "删除笔记", description = "管理员删除指定笔记")
    public Result deleteNote(@RequestParam("id") Integer noteId) {
        log.info("管理员删除笔记，ID：{}", noteId);
        noteService.deleteNoteByAdmin(noteId);
        log.info("管理员删除笔记成功");
        return Result.success();
    }

    /**
     * 批量删除笔记
     * @param ids 笔记ID列表（逗号分隔）
     * @return 操作结果
     */
    @DeleteMapping("/delete/notes")
    @Operation(summary = "批量删除笔记", description = "管理员批量删除笔记")
    public Result batchDeleteNotes(@RequestParam("ids") String ids) {
        log.info("管理员批量删除笔记，ID列表：{}", ids);
        String[] idArray = ids.split(",");
        List<Integer> noteIdList = new ArrayList<>();
        for (int i = 0; i < idArray.length; i++) {
            noteIdList.add(Integer.parseInt(idArray[i].trim()));
        }
        noteService.deleteBatchNotes(noteIdList);
        log.info("管理员批量删除笔记成功，共删除 {} 条", noteIdList.size());
        return Result.success();
    }


    /**
     * 管理员搜索笔记
     */
    @PostMapping("/search/notes")
    @Operation(summary = "搜索笔记", description = "管理员根据关键字搜索笔记（模糊搜索）")
    public Result<PageResult> searchNotes(@RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "10") int pageSize,
                                          @RequestParam(required = false) String keyword) {
        log.info("管理员搜索笔记，关键字：{}，页码：{}，每页数量：{}", keyword, page, pageSize);

        PageResult pageResult = noteService.adminSearchNotes(keyword, page, pageSize);

        return Result.success(pageResult);
    }


    /**
     * 查看所有评论
     * */
    @GetMapping("/comments")
    @Operation(summary = "查看所有评论", description = "管理员查看所有用户的评论，分页查询")
    public Result<PageResult> allComments(PageQueryDTO pageQueryDTO)
    {
        log.info("管理员查看所有用户的评论，分页参数：page={}, pageSize={}", pageQueryDTO.getPage(), pageQueryDTO.getPageSize());
        PageResult pageResult = commentService.adminCommentList(pageQueryDTO);
        log.info("查看所有评论成功，共{}条记录", pageResult.getTotal());
        return Result.success(pageResult);
    }


    @GetMapping("/statistics")
    @Operation(summary = "获取统计数据", description = "获取用户、文章、笔记、评论的数量统计")
    public Result<Map<String, Integer>> getStatistics()
    {
        log.info("开始统计用户、文章、笔记、评论数量");
        Map<String, Integer> statistics = new HashMap<>();

        // 统计用户数量
        int userCount = userService.countUsers();
        statistics.put("userCount", userCount);

        // 统计文章数量（使用新方法）
        int articleCount = articleService.countArticles();
        statistics.put("articleCount", articleCount);

        // 统计笔记数量
        int noteCount = noteService.countNotes();
        statistics.put("noteCount", noteCount);

        // 统计评论数量
        int commentCount = commentService.countComments();
        statistics.put("commentCount", commentCount);
        return Result.success(statistics);
    }



    /**
     * 获取所有分类（树形结构）
     * @return 分类列表
     */
    @GetMapping("/categories")
    @Operation(summary = "获取所有分类", description = "管理员获取所有分类（树形结构）")
    public Result<List<Category>> getAllCategories() {
        log.info("管理员获取所有分类");
        List<Category> categories = categoryService.getAllCategories();
        return Result.success(categories);
    }



    /**
     * 获取分类详情
     * @param categoryId 分类ID
     * @return 分类详情
     */
    @GetMapping("/category")
    @Operation(summary = "获取分类详情", description = "管理员获取指定分类的详细信息")
    public Result<Category> getCategory(@RequestParam("id") Integer categoryId) {
        log.info("管理员获取分类详情，ID：{}", categoryId);
        Category category = categoryService.getCategoryById(categoryId);
        if (category == null) {
            return Result.error("分类不存在");
        }
        return Result.success(category);
    }


    /**
     * 添加分类
     */
    @PostMapping("/add/category")
    @Operation(summary = "添加分类", description = "管理员添加新分类")
    public Result addCategory(@RequestBody Category category) {
        log.info("管理员添加分类: {}", category);
        categoryService.addCategory(category);
        return Result.success();
    }

    /**
     * 更新分类
     * @param category 分类信息
     * @return 操作结果
     */
    @PutMapping("/update/category")
    @Operation(summary = "更新分类", description = "管理员更新分类信息")
    public Result updateCategory(@RequestBody Category category) {
        log.info("管理员更新分类，ID：{}", category.getCategoryId());
        categoryService.updateCategory(category);
        log.info("管理员更新分类成功，ID：{}", category.getCategoryId());
        return Result.success();
    }


    /**
     * 删除分类
     * @param categoryId 分类ID
     * @return 操作结果
     */
    @DeleteMapping("/delete/category")
    @Operation(summary = "删除分类", description = "管理员删除分类（包含子分类）")
    public Result deleteCategory(@RequestParam("id") Integer categoryId) {
        log.info("管理员删除分类，ID：{}", categoryId);

        if (categoryId == null) {
            return Result.error("分类ID不能为空");
        }

        // 检查是否有文章使用该分类
        int articleCount = categoryService.countArticlesByCategory(categoryId);
        if (articleCount > 0) {
            return Result.error("该分类下有文章，无法删除");
        }

        // 检查是否有子分类
        int childCount = categoryService.countChildCategories(categoryId);
        if (childCount > 0) {
            // 级联删除子分类
            categoryService.deleteCategoryWithChildren(categoryId);
        } else {
            categoryService.deleteCategory(categoryId);
        }

        log.info("管理员删除分类成功，ID：{}", categoryId);
        return Result.success();
    }


}
