package com.luck.chat.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 好友表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Friend implements Serializable {
    //主键
    private Long id;

    //用户id
    private Long userId;

    //好友id
    private Long friendId;

    //好友头像(冗余字段)
    private String friendAvatar;

    //好友名字(冗余字段)
    private String friendName;

    //好友类别id
    private Long categoryId;

    //创建时间
    private LocalDateTime createTime;

}
