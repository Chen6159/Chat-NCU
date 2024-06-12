package com.luck.chat.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendAddDTO implements Serializable {

    //接收方用户id(好友id)
    private Long receiverId;

    //好友分类id
    private Long categoryId;

    //招呼语句(留言)
    private String helloWord;

}
