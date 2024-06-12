package com.luck.chat.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginVo implements Serializable {

    //用户id
    private Long id;

    //用户名
    private String name;

    //jwt令牌
    private String token;
}
