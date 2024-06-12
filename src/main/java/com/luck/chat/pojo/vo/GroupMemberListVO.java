package com.luck.chat.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupMemberListVO implements Serializable {

    //主键
    private Long id;

    //群聊表id
    private Long groupChatId;

    //用户id
    private Long userId;

    //用户名
    private String userName;

    //用户头像
    private String userAvatar;

    //用户身份权限
    private Integer role;

    //用户加入时间
    private LocalDateTime joinTime;
}
