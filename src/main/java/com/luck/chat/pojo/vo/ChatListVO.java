package com.luck.chat.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatListVO implements Serializable {

    //聊天列表id
    private Long chatListId;

    //聊天对象名称
    private String chatName;

    //聊天对象头像框
    private String chatAvatar;

    //最后一条聊天类型
    private Integer lastMessageType;

    //最后一条聊天信息
    private String lastMessageContent;

    //已读未读状态
    private Integer readStatus;

    //最后发送时间
    private LocalDateTime lastSendTime;

}
