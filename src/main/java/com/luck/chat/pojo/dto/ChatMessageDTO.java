package com.luck.chat.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO implements Serializable {

    //聊天列表id
    private Long chatListId;

    //聊天类型(默认就是0 文本)
    private Integer messageType;

    //聊天内容
    private String messageContent;

    //聊天用户名
    private String userName;

    //聊天用户头像
    private String userAvatar;
}
