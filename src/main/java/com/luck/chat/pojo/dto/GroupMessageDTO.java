package com.luck.chat.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupMessageDTO implements Serializable {

    String groupName;

    String groupAvatar;

    List<Long> groupMemberIdList;
}
