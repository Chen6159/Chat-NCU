package com.luck.chat.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendInvitationDTO implements Serializable {

    //申请者用户id
    private Long senderId;

    //分类id
    private Long categoryId;

    //1 接受 2 拒绝
    private Integer status;
}
