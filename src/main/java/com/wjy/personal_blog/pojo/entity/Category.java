package com.wjy.personal_blog.pojo.entity;

import com.alibaba.druid.support.monitor.annotation.MTable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("category")
public class Category {
    @TableId(value = "category_id",type = IdType.AUTO)
    private Integer categoryId;
    @TableField("category_name")
    private String categoryName;
    @TableField("category_order")
    private Integer categoryOrder;
    @TableField("category_description")
    private String categoryDescription;
    @TableField("category_icon")
    private String categoryIcon;
    @TableField("category_parent_id")
    private Integer categoryParentId;
}
