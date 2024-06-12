package com.luck.chat.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewChatMessagesDTO implements Serializable {

    //聊天列表id
    private Long chatListId;

    //旧消息id
    private Long oldMessageId;
}
