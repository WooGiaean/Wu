package com.wjy.personal_blog.service.impl;

import com.github.pagehelper.Page;
import com.wjy.personal_blog.mapper.NoteMapper;
import com.wjy.personal_blog.pojo.dto.NotesDTO;
import com.wjy.personal_blog.pojo.entity.Notes;
import com.wjy.personal_blog.result.PageResult;
import com.wjy.personal_blog.service.NoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class NoteServiceImpl implements NoteService {

    @Autowired
    private NoteMapper noteMapper;


    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public  PageResult noteList() {
        long start = System.currentTimeMillis();
        Page<Notes> list = noteMapper.list();
        PageResult pageResult = new PageResult(list.getTotal(),list.getResult());
        //遍历pageResult的数据
        /*pageResult.getRecords().forEach(notes -> {

        });*/
        long end = System.currentTimeMillis();
        log.info("查询笔记耗时：{}",end-start);
        return pageResult;
    }

    /*
    * 根据id获取笔记内容
    * 先到redis中查询，没有则从数据库中查询
    * */
    @Override
    public Notes getNoteById(Integer id) {
        Notes noteById = noteMapper.getNoteById(id);
        return noteById;
    }


    /**
     * 模糊查询笔记（根据关键字）
     * */
    @Override
    public PageResult queryByKeyWord(NotesDTO notesDTO) {
        log.info("关键字查询笔记：{}",notesDTO);
        Notes notes = new Notes();
        BeanUtils.copyProperties(notesDTO,notes);
        Page<Notes> result = noteMapper.queryByKeyWord(notes);
        PageResult pageResult = new PageResult(result.getTotal(),result.getResult());
        return pageResult;
    }

    @Override
    public void addNewNote(NotesDTO notesDTO) {
        log.info("添加新笔记：{}",notesDTO);
        Notes notes = new Notes();
        BeanUtils.copyProperties(notesDTO,notes);
        //设置创建时间
        notes.setNoteCreateTime(LocalDateTime.now());
        notes.setNoteUpdateTime(LocalDateTime.now());
        //数据库插入新数据
        noteMapper.addNewNote(notes);
    }


    /**
     * 修改笔记
     * */
    @Override
    public void updateNote(NotesDTO notesDTO) {
        log.info("修改笔记:{}",notesDTO);
        Notes notes = new Notes();
        BeanUtils.copyProperties(notesDTO,notes);
        notes.setNoteUpdateTime(LocalDateTime.now());
        noteMapper.updateNote(notes);
    }

    /**
     * 删除笔记
     * */
    @Override
    public void deleteNote(Integer noteId) {
        log.info("删除笔记：{}",noteId);
        if(noteId==null){
            log.error("删除笔记失败：id为空");
            return;
        }
        noteMapper.deleteNote(noteId);
        log.info("删除笔记成功：{}",noteId);
    }


}
