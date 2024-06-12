package com.luck.chat.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 分类表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Category implements Serializable {

    //分类id
    private Long id;

    //分类所属用户id
    private Long userId;

    //分类名称
    private String categoryName;
}
