package com.wjy.personal_blog.service;

import com.wjy.personal_blog.pojo.dto.NotesDTO;
import com.wjy.personal_blog.pojo.entity.Notes;
import com.wjy.personal_blog.result.PageResult;

public interface NoteService {
    /* 笔记列表 */
    PageResult noteList();

    Notes getNoteById(Integer id);

    PageResult queryByKeyWord(NotesDTO notesDTO);

    void addNewNote(NotesDTO notesDTO);

    //修改笔记
    void updateNote(NotesDTO notesDTO);

    void deleteNote(Integer noteId);
}
