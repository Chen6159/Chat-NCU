package com.luck.chat.controller;

import com.luck.chat.context.BaseContext;
import com.luck.chat.pojo.dto.NewChatMessagesDTO;
import com.luck.chat.pojo.entity.ChatMessage;
import com.luck.chat.pojo.entity.Result;
import com.luck.chat.pojo.vo.ChatListVO;
import com.luck.chat.pojo.vo.ChatMessageVO;
import com.luck.chat.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;


    /**
     * 获取用户聊天列表(按照最后一套消息排序)
     * @return
     */
    @GetMapping("/list")
    public Result<List<ChatListVO>> chatLists(){
        log.info("获取用户聊天列表");

        return Result.success(chatService.chatLists(BaseContext.getCurrentId()));
    }

    /**
     * 获取新的聊天消息
     * @return
     */
    @GetMapping("/new")
    public Result<List<ChatMessageVO>> getNews(Long chatListId , Long oldMessageId){

        log.info("获取新的聊天消息,聊天列表id:{},旧消息id:{}",chatListId,oldMessageId);

        List<ChatMessageVO> chatMessageVOList= chatService.getNews(chatListId,oldMessageId);
        return Result.success(chatMessageVOList);
    }

}
