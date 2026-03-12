package com.wjy.personal_blog.controllers.user;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wjy.personal_blog.pojo.dto.ArticleDTO;
import com.wjy.personal_blog.pojo.dto.NotesDTO;
import com.wjy.personal_blog.pojo.entity.Notes;
import com.wjy.personal_blog.result.PageResult;
import com.wjy.personal_blog.result.Result;
import com.wjy.personal_blog.service.NoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/notes")
@Slf4j
public class NoteController {

    @Autowired
    private NoteService noteService;


    /**/
    @RequestMapping("/")
    public String turnToNoteListPage(){
        return "redirect:/Note/note.html";
    }

    /**
    * 展示所有笔记（在note页面）
    * */
    @GetMapping("/list")
    @ResponseBody
    public Result<PageResult> listNotes( @RequestParam(defaultValue = "1")Integer page,
                                         @RequestParam(defaultValue = "10")Integer pageSize){
        log.info("开始查询笔记列表，页码：{}，数量{}",page,pageSize);
        PageResult pageResult = noteService.noteList();
        return Result.success(pageResult);
    }

    /**
    * 根据id获取笔记
    * */

    @GetMapping("/noteId")
    @ResponseBody
    public Result<Notes> getNote(@RequestParam("id")Integer id){
        log.info("查询id为的笔记:{}",id);
        Notes note = noteService.getNoteById(id);
        return Result.success(note);
    }

    /**
     * 关键字搜索
     * */
    @PostMapping("/query")
    @ResponseBody
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
    @ResponseBody
    public Result insertNewNote(@RequestBody NotesDTO notesDTO){
        log.info("添加新笔记：{}",notesDTO);
        noteService.addNewNote(notesDTO);
        return Result.success();
    }

    @PutMapping("/update")
    @ResponseBody
    public Result updateNote(@RequestBody NotesDTO notesDTO){
        log.info("修改笔记:{}",notesDTO);
        noteService.updateNote(notesDTO);
        return Result.success();
    }


    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public Result deleteNote(@PathVariable("id") Integer noteId){
        log.info("删除笔记：{}",noteId);
        noteService.deleteNote(noteId);
        return Result.success();
    }
}
