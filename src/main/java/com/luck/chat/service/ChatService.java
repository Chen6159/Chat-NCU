package com.luck.chat.service;

import com.luck.chat.pojo.vo.ChatListVO;
import com.luck.chat.pojo.vo.ChatMessageVO;

import java.util.List;

public interface ChatService {

    /**
     * 根据id获取用户聊天列表
     * @return
     */
    List<ChatListVO> chatLists(Long userId);

    /**
     * 获取新的聊天消息
     * @param chatListId
     * @param oldMessageId
     * @return
     */
    List<ChatMessageVO> getNews(Long chatListId, Long oldMessageId);


}
