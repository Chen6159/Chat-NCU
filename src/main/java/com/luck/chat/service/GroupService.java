package com.luck.chat.service;


import com.luck.chat.pojo.entity.GroupChat;

public interface GroupService {

    /**
     * 根据id查找群聊信息
     * @param id
     * @return
     */
    GroupChat getById(Long id);
}
