package com.luck.chat.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 群聊表
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupChat implements Serializable {
    //主键
    private Long id;

    //群聊名称
    private String groupName;

    //群聊头像
    private String groupAvatar;

    //创建人id
    private Long creatorId;

    //创建时间
    private LocalDateTime createTime;

}
