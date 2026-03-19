package com.wjy.personal_blog.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tag implements Serializable {
    private Integer tagId;
    private String tagName;
    private String tagDescription;

    /* 非数据库字段 */
    //private Integer articleCount;

}
