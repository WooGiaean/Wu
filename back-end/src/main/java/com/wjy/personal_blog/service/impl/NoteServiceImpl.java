package com.wjy.personal_blog.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wjy.personal_blog.constants.RedisConstant;
import com.wjy.personal_blog.context.BaseContext;
import com.wjy.personal_blog.mapper.NoteMapper;
import com.wjy.personal_blog.pojo.dto.NotesDTO;
import com.wjy.personal_blog.pojo.dto.PageQueryDTO;
import com.wjy.personal_blog.pojo.entity.Notes;
import com.wjy.personal_blog.result.PageResult;
import com.wjy.personal_blog.service.NoteService;
import com.wjy.personal_blog.utils.SecurityUtil;
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
    public  PageResult noteList(Integer currentId) {
        //获取当前用户id
        //笔记列表键，加入用户ID确保每个用户有自己的缓存
        String recentNotesKey = RECENT_NOTES_KEY + currentId;
        //过期时间：10min
        long expiredMinutes = RedisConstant.HOT_DATA_EXPIRED_MINUTES;
        //获取redis操作对象
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();

        //从缓存中获取数据
        Object cacheNotes = ops.get(recentNotesKey);
        //如果缓存中有数据则直接返回缓存数据
        if(cacheNotes!=null && cacheNotes instanceof PageResult){
            log.info("从缓存中查询笔记数据");
            return (PageResult) cacheNotes;
        }else{
            log.warn("缓存数据格式错误，清除key：{}",recentNotesKey);
            redisTemplate.delete(recentNotesKey);
        }

        //没有则到数据库查询
        log.info("从数据库中查询笔记数据");
        Page<Notes> list = noteMapper.list(currentId);
        PageResult pageResult = new PageResult(list.getTotal(),list.getResult());

        //将数据写入redis缓存
        ops.set(recentNotesKey,pageResult,expiredMinutes, TimeUnit.MINUTES);

        return pageResult;
    }

    @Override
    public PageResult adminNoteList(PageQueryDTO pageQueryDTO) {
        log.info("管理员开始查询所有用户的笔记");

        log.info("分页查询参数：{}", pageQueryDTO);
        int page = pageQueryDTO.getPage() != null ? pageQueryDTO.getPage() : 1;
        int pageSize = pageQueryDTO.getPageSize() != null ? pageQueryDTO.getPageSize() : 10;

        PageHelper.startPage(page, pageSize);
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
        return id!=null?noteMapper.getNoteById(id):null;
    }


    /**
     * 模糊查询笔记（根据关键字）
     * */
    @Override
    public PageResult queryByKeyWord(NotesDTO notesDTO) {
        log.info("关键字查询笔记：{}",notesDTO);
        Notes notes = new Notes();
        BeanUtils.copyProperties(notesDTO,notes);
        //设置当前用户ID
        Integer currentId = SecurityUtil.getCurrentUserId();
        notes.setNoteUserId(currentId);
        Page<Notes> result = noteMapper.queryByKeyWord(notes);
        PageResult pageResult = new PageResult(result.getTotal(),result.getResult());
        return pageResult;
    }

    @Override
    public void addNewNote(NotesDTO notesDTO) {
        log.info("添加新笔记：{}",notesDTO);
        //添加用户权限管理
        Integer currentId = SecurityUtil.getCurrentUserId();
        Notes notes = new Notes();
        BeanUtils.copyProperties(notesDTO,notes);
        //设置笔记创建人id
        notes.setNoteUserId(currentId);
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
        //获取当前用户ID
        Integer currentId = SecurityUtil.getCurrentUserId();
        //查询笔记信息
        Notes note = noteMapper.getNoteById(notesDTO.getNoteId());
        if(note == null){
            log.warn("笔记不存在");
            return;
        }
        //验证权限
        if(!note.getNoteUserId().equals(currentId)){
            log.warn("用户 {} 没有权限修改此笔记", currentId);
            return;
        }
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
        //获取当前用户ID
        Integer currentId = SecurityUtil.getCurrentUserId();
        //查询笔记信息
        Notes note = noteMapper.getNoteById(noteId);
        if(note == null){
            log.warn("笔记不存在");
            return;
        }
        //验证权限
        if(!note.getNoteUserId().equals(currentId)){
            log.warn("用户 {} 没有权限删除此笔记", currentId);
            return;
        }
        noteMapper.deleteNote(noteId);
        log.info("删除笔记成功：{}",noteId);
        redisTemplate.delete(RECENT_NOTES_KEY);
    }


}
