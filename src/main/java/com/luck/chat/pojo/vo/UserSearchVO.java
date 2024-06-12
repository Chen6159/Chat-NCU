package com.luck.chat.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchVO implements Serializable {
    //用户id
    public Long id;

    //用户名
    public String username;

    //用户头像
    public String avatar;
}
