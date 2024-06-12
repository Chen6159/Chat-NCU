package com.luck.chat.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 聊天列表表
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatList implements Serializable {
    //主键
    private Long id;

    //发送方用户id
    private Long senderId;

    //接受方用户id
    private Long receiverId;

    //群聊id
    private Long groupChatId;
}
