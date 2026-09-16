package com.wjy.personal_blog.service;

import com.wjy.personal_blog.pojo.dto.NotesDTO;
import com.wjy.personal_blog.pojo.dto.PageQueryDTO;
import com.wjy.personal_blog.pojo.entity.Notes;
import com.wjy.personal_blog.result.PageResult;

import java.util.List;

public interface NoteService {
    /* 笔记列表 */
    PageResult noteList(Integer currentId);

    /* 管理员笔记列表 */
    PageResult adminNoteList(PageQueryDTO pageQueryDTO);

    Notes getNoteById(Integer id);

    PageResult queryByKeyWord(NotesDTO notesDTO);

    void addNewNote(NotesDTO notesDTO);

    //修改笔记
    void updateNote(NotesDTO notesDTO);

    void deleteNote(Integer noteId);

    int countNotes();

    Notes getNoteByAdmin(Integer noteId);

    void deleteNoteByAdmin(Integer noteIds);

    void deleteBatchNotes(List<Integer> noteIdList);

    PageResult adminSearchNotes(String keyword, int page, int pageSize);
}
