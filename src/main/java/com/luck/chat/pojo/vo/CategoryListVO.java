package com.luck.chat.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryListVO implements Serializable {

    //分类id
    private Long id;

    //分类名称
    private String categoryName;
}
