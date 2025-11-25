package com.wjy.personal_blog.mapper;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.github.pagehelper.Page;
import com.wjy.personal_blog.pojo.dto.NotesDTO;
import com.wjy.personal_blog.pojo.entity.Notes;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface NoteMapper {
    /* 查询所有随笔 */
    @Select("select * from notes order by note_create_time desc")
    Page<Notes> list();

    /* 通过id搜寻笔记 */
    @Select("select * from notes where note_id=#{id}")
    Notes getNoteById(Integer id);

    /* 模糊查询笔记 */
    Page<Notes> queryByKeyWord(Notes notes);

    /* 添加新笔记 */
    @Insert("insert into notes(note_topic,note_content,note_create_time,note_update_time) " +
            "values(#{noteTopic}," +
            "#{noteContent},#{noteCreateTime},#{noteUpdateTime})")
    void addNewNote(Notes notes);


    void updateNote(Notes notes);

    @Delete("delete from notes where note_id=#{noteId}")
    void deleteNote(Integer noteId);
}
