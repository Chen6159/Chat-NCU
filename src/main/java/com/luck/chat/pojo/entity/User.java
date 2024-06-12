package com.luck.chat.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户表
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    //主键
    private Long id;

    //手机号(加密后)
    private String phone;

    //用户名
    private String username;

    //用户头像
    private String avatar;

    //用户签名
    private String description;

    //用户性别
    private String gender;

    //用户年龄
    private String age;

    //创建时间
    private LocalDateTime createTime;
}
