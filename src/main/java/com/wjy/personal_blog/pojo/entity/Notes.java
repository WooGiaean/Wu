package com.wjy.personal_blog.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notes {
    private Integer noteId;
    private String noteTopic;
    private String noteContent;
    private LocalDateTime noteCreateTime;
    private LocalDateTime noteUpdateTime;
}
