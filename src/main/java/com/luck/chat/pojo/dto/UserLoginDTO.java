package com.luck.chat.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO implements Serializable {

    //用户手机号
    private String phone;

    //用户验证码
    private String code;
}
