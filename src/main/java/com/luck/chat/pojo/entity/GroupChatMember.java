package com.luck.chat.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 群聊成员表
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupChatMember implements Serializable {
    //主键
    private Long id;

    //群聊表id
    private Long groupChatId;

    //用户id
    private Long userId;

    //用户身份权限
    private Integer role;

    //用户加入时间
    private LocalDateTime joinTime;

}
