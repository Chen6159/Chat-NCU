package com.luck.chat.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO implements Serializable {

    //用户名
    private String username;

    //用户签名
    private String description;

    //性别
    private String gender;

    //用户头像
    private String avatar;
}
