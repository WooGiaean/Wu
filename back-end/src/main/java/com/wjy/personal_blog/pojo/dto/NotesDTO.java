package com.wjy.personal_blog.pojo.dto;

import lombok.Data;

@Data
public class NotesDTO {
    private Integer noteId;
    private String noteTopic;
    private String noteContent;
}
