package com.wjy.personal_blog.service.impl;

import com.github.pagehelper.Page;
import com.wjy.personal_blog.constants.RedisConstant;
import com.wjy.personal_blog.context.BaseContext;
import com.wjy.personal_blog.mapper.NoteMapper;
import com.wjy.personal_blog.pojo.dto.NotesDTO;
import com.wjy.personal_blog.pojo.entity.Notes;
import com.wjy.personal_blog.result.PageResult;
import com.wjy.personal_blog.service.NoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static com.wjy.personal_blog.constants.RedisConstant.RECENT_NOTES_KEY;

@Service
@Slf4j
public class NoteServiceImpl implements NoteService {

    @Autowired
    private NoteMapper noteMapper;


    @Autowired
    private RedisTemplate<String,Object>redisTemplate;

    /*
    * 缓存笔记列表
    * */
    @Override
    public  PageResult noteList() {
        //笔记列表键
        String recentNotesKey = RECENT_NOTES_KEY;
        //过期时间：5min
        long expiredSeconds = RedisConstant.TEST_EXPIRED_SECONDS;
        //获取redis操作对象
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();

        //从缓存中获取数据，并计算耗时时间
        long redisBegin = System.currentTimeMillis();
        Object cacheNotes = ops.get(recentNotesKey);
        long redisEnd = System.currentTimeMillis();
        //如果缓存中有数据则直接返回缓存数据
        if(cacheNotes!=null){
            if(cacheNotes instanceof PageResult){
                log.info("从缓存中查询笔记数据,Redis耗时：{}",redisEnd-redisBegin);
                return (PageResult) cacheNotes;
            }else{
                log.warn("缓存数据格式错误，清除key：{}",recentNotesKey);
                redisTemplate.delete(recentNotesKey);
            }
        }
        //获取当前用户id
        Integer currentId = BaseContext.getCurrentId();

        //没有则到数据库查询
        log.info("从数据库中查询笔记数据");
        long start = System.currentTimeMillis();
        Page<Notes> list = noteMapper.list(currentId);
        long end = System.currentTimeMillis();
        PageResult pageResult = new PageResult(list.getTotal(),list.getResult());
        log.info("从数据库查询笔记,耗时：{}",end-start);

        //将数据写入redis缓存
        ops.set(recentNotesKey,pageResult,60*2, TimeUnit.SECONDS);

        return pageResult;
    }

    @Override
    public PageResult adminNoteList() {
        log.info("管理员开始查询所有用户的笔记");
        Page<Notes> notes = noteMapper.adminSeeNotesList();
        if(notes==null){
            log.warn("没有查询到笔记数据");
            return null;
        }
        log.info("管理员查询成功，返回数据");
        return new PageResult(notes.getTotal(),notes.getResult());
    }

    /*
    * 根据id获取笔记内容
    * 先到redis中查询，没有则从数据库中查询
    * */
    @Override
    public Notes getNoteById(Integer id) {
        return noteMapper.getNoteById(id);
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
        redisTemplate.delete(RECENT_NOTES_KEY);
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
        redisTemplate.delete(RECENT_NOTES_KEY);
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
        redisTemplate.delete(RECENT_NOTES_KEY);
    }


}
