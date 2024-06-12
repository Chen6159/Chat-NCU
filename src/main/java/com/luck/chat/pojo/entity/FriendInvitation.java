package com.luck.chat.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 好友申请表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendInvitation implements Serializable {
    //主键
    private Long id;

    //接收用户id
    private Long userId;

    //发送好友申请的用户id
    private Long senderId;

    //该用户名称
    private String senderName;

    //该用户头像
    private String senderAvatar;

    //发送者的用户预选的分类id
    private Long categoryId;

    //招呼语句(非空)
    private String helloWord;

    //请求状态(请求中,已接受,已拒绝)
    private Integer status;

    //创建时间
    private LocalDateTime createTime;
}
