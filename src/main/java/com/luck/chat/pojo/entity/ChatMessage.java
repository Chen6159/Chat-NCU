package com.luck.chat.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 聊天内容表
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage implements Serializable {
    //主键
    private Long id;

    //聊天列表id
    private Long chatListId;

    //发送消息用户id
    private Long userId;

    //消息内容(注意不能为空)
    private String messageContent;

    //消息类型
    private Integer messageType;

    //已读未读状态
    private Integer readStatus;

    //发送时间
    private LocalDateTime sendTime;
}
