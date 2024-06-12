package com.luck.chat.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 好友列表内置对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendListVO implements Serializable {

     private Long friendId;

     private String friendName;

     private String friendAvatar;
}
