package com.luck.chat.pojo.vo;

import com.luck.chat.pojo.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageVO {

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

    //消息用户头像
    private String userAvatar;

    //消息用户名
    private String userName;

}
