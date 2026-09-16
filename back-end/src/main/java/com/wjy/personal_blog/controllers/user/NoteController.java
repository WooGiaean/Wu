package com.wjy.personal_blog.controllers.user;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wjy.personal_blog.pojo.dto.ArticleDTO;
import com.wjy.personal_blog.pojo.dto.NotesDTO;
import com.wjy.personal_blog.pojo.entity.Notes;
import com.wjy.personal_blog.result.PageResult;
import com.wjy.personal_blog.result.Result;
import com.wjy.personal_blog.service.NoteService;
import com.wjy.personal_blog.utils.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/notes")
@Slf4j
@Tag(name = "笔记管理", description = "用户笔记相关接口")
public class NoteController {

    @Autowired
    private NoteService noteService;


    /**
    * 展示所有笔记（在note页面）
    * */
    @GetMapping("/list")
    @Operation(summary = "查看笔记列表", description = "查看当前用户的笔记列表")
    public Result<PageResult> listNotes( @RequestParam(defaultValue = "1")Integer page,
                                         @RequestParam(defaultValue = "10")Integer pageSize){
        log.info("开始查询笔记列表，页码：{}，数量{}",page,pageSize);
        PageHelper.startPage(page,pageSize);
        Integer currentId = SecurityUtil.getCurrentUserId();
        PageResult pageResult = noteService.noteList(currentId);
        return Result.success(pageResult);
    }

    /**
    * 根据id获取笔记
    * */

    @GetMapping("/{id}")
    @Operation(summary = "获取笔记详情", description = "根据ID获取笔记详情")
    public Result<Notes> getNote(@PathVariable("id")Integer id){
        log.info("查询id为的笔记:{}",id);
        Notes note = noteService.getNoteById(id);
        return Result.success(note);
    }

    /**
     * 关键字搜索
     * */
    @PostMapping("/query")
    @Operation(summary = "搜索笔记", description = "根据关键字搜索笔记")
    public Result<PageResult> queryNoteByKeyWord(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestBody NotesDTO notesDTO){
        log.info("关键字查询笔记：{}",notesDTO);

        //防止关键字太多
        if(notesDTO.getNoteTopic().length()>50||notesDTO.getNoteContent().length()>50){
            return Result.error("关键字太多了");
        }

        //分页查询
        PageHelper.startPage(page,pageSize);
        PageResult pageResult = noteService.queryByKeyWord(notesDTO);
        return Result.success(pageResult);
    }

    /**
     * 添加新笔记
     * @param notesDTO
     * @return
     */
    @PostMapping("/insert")
    @Operation(summary = "添加新笔记", description = "创建新笔记")
    public Result insertNewNote(@RequestBody NotesDTO notesDTO){
        log.info("添加新笔记：{}",notesDTO);
        noteService.addNewNote(notesDTO);
        return Result.success();
    }

    @PutMapping("/update")
    @Operation(summary = "修改笔记", description = "修改指定笔记内容")
    public Result updateNote(@RequestBody NotesDTO notesDTO){
        log.info("修改笔记:{}",notesDTO);
        noteService.updateNote(notesDTO);
        return Result.success();
    }


    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除笔记", description = "删除指定笔记")
    public Result deleteNote(@PathVariable("id") Integer noteId){
        log.info("删除笔记：{}",noteId);
        noteService.deleteNote(noteId);
        return Result.success();
    }
}
