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
            "join user on note_user_id=user_id")
    Page<Notes> adminSeeNotesList();


    /* 通过笔记id搜寻笔记 */
    @Select("select * from notes where note_id=#{id}")
    Notes getNoteById(Integer id);


    /* 通过用户id查询用户所有笔记 */
    @Select("select * from notes where note_user_id=#{noteUserId}")
    Notes getNoteByUserId(Integer noteUserId);

    /* 模糊查询笔记 */
    Page<Notes> queryByKeyWord(Notes notes);

    /* 添加新笔记 */
    @Insert("insert into notes(note_topic,note_content,note_create_time,note_update_time,note_user_id) " +
            "values(#{noteTopic}," +
            "#{noteContent},#{noteCreateTime},#{noteUpdateTime},#{noteUserId})")
    void addNewNote(Notes notes);


    void updateNote(Notes notes);

    @Delete("delete from notes where note_id=#{noteId}")
    void deleteNote(Integer noteId);


    //@Delete("delete from notes where note_id in (#{noteIdList})")
    void deleteBatchNotes(List<Integer> noteIdList);

    /*
    * 查询每个用户
    * */
    @MapKey("note_user_id")
    Map<Integer, Map<String, Long>> countNoteByUserIdMaps(@Param("userIds") List<Integer> userIds);

    @Select("select count(*) from notes where note_user_id=#{noteUserId}")
    Integer countNoteByUserId(Integer noteUserId);

    @Select("select count(*) from notes")
    Integer countAllNotes();

    List<Notes> adminSearchNotes(String keyword);


    //int countByUserId(Integer userId);
}
