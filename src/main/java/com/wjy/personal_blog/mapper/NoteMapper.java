package com.wjy.personal_blog.mapper;

import com.github.pagehelper.Page;
import com.wjy.personal_blog.pojo.entity.Notes;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface NoteMapper {
    /* 查询当前用户所有随笔 */
    @Select("select * from notes where note_user_id = #{noteUserId} " +
            "order by note_create_time desc")
    Page<Notes> list(@Param("noteUserId")Integer noteUserId);

    @Select("select notes.*,user.user_nickname as noteAuthor from notes " +
            "join user where note_user_id=user_id;")
    Page<Notes> adminSeeNotesList();


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


    @MapKey("note_user_id")
    Map<Integer, Map<String, Long>> countNoteByUserId(@Param("userIds") List<Integer> userIds);
}
