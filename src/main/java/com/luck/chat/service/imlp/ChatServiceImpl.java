package com.luck.chat.service.imlp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luck.chat.constant.MessageReadStatusType;
import com.luck.chat.constant.MessageTypeConstant;
import com.luck.chat.constant.RedisConstant;
import com.luck.chat.mapper.ChatMapper;
import com.luck.chat.pojo.entity.ChatList;
import com.luck.chat.pojo.entity.ChatMessage;
import com.luck.chat.pojo.entity.GroupChat;
import com.luck.chat.pojo.entity.User;
import com.luck.chat.pojo.vo.ChatListVO;
import com.luck.chat.pojo.vo.ChatMessageVO;
import com.luck.chat.service.ChatService;
import com.luck.chat.service.GroupService;
import com.luck.chat.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ChatMapper chatMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;


    /**
     * 根据userId获取用户聊天列表
     * @param userId
     * @return
     */
    @Override
    public List<ChatListVO> chatLists(Long userId) {
//        String key=RedisConstant.USERS_CHAT_LISTS + userId;
        List<ChatListVO> list=new ArrayList<>();
//        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))){
//            try {
//                list=objectMapper.readValue(stringRedisTemplate.opsForValue().get(key),new TypeReference<List<ChatListVO>>() {});
//                return list;
//            } catch (JsonProcessingException e) {
//                log.error("redis反序列化失败!");
//            }
//        }else{
            //从数据库查找并按最后一条消息发送时间排序
            List<ChatList> chatLists= chatMapper.selectChatListsByUserId(userId);
            for(ChatList chatList:chatLists){
                if(chatList.getGroupChatId()==null){
                    //要查找最新的聊天信息和用户信息
                    User user=userService.getById(chatList.getReceiverId());
                    ChatMessage latestMessage=chatMapper.selectLatestMessage(chatList.getId());
                    ChatListVO chatListVO=new ChatListVO();
                    chatListVO.setChatListId(chatList.getId());
                    chatListVO.setChatAvatar(user.getAvatar());
                    chatListVO.setChatName(user.getUsername());
                    chatListVO.setLastMessageType(latestMessage.getMessageType());
                    chatListVO.setLastMessageContent(latestMessage.getMessageContent());
                    chatListVO.setReadStatus(latestMessage.getReadStatus());
                    chatListVO.setLastSendTime(latestMessage.getSendTime());
                    list.add(chatListVO);
                }else{
                    //群聊信息,查找该群聊的信息和最后一条聊天信息
                    GroupChat groupChat=groupService.getById(chatList.getGroupChatId());
                    ChatMessage latestMessage=chatMapper.selectLatestMessage(chatList.getId());
                    ChatListVO chatListVO=new ChatListVO();
                    chatListVO.setChatListId(chatList.getId());
                    chatListVO.setChatAvatar(groupChat.getGroupAvatar());
                    chatListVO.setChatName(groupChat.getGroupName());
                    if(latestMessage!=null){
                        chatListVO.setLastMessageType(latestMessage.getMessageType());
                        chatListVO.setLastMessageContent(latestMessage.getMessageContent());
                        chatListVO.setReadStatus(latestMessage.getReadStatus());
                        chatListVO.setLastSendTime(latestMessage.getSendTime());
                    }else{
                        chatListVO.setLastMessageContent("");
                        chatListVO.setLastMessageType(MessageTypeConstant.TEXT_MESSAGE);
                        chatListVO.setReadStatus(MessageReadStatusType.UNREAD);
                        chatListVO.setLastSendTime(LocalDateTime.now());
                    }
                    list.add(chatListVO);
                }
            }
            //降序排序
            list.sort(((o1, o2) -> o2.getLastSendTime().compareTo(o1.getLastSendTime())));
//            //存入redis中
//            try {
//                stringRedisTemplate.opsForValue().set(key,objectMapper.writeValueAsString(list),72, TimeUnit.HOURS);
//            } catch (JsonProcessingException e) {
//                log.error("序列化存redis失败!");
//            }
            return  list;
        }

    /**
     * 获取新的聊天消息
     * @param chatListId
     * @param oldMessageId
     * @return
     */
    @Override
    public List<ChatMessageVO> getNews(Long chatListId, Long oldMessageId) {
        //根据聊天列表id和旧消息id获取最新的聊天消息
//        List<ChatMessageVO> oldChatMessages=chatMapper.selectOldChatMessagesForTen(chatListId,oldMessageId);
        List<ChatMessageVO> newChatMessages=chatMapper.selectNewChatMessages(chatListId,oldMessageId);

//        List<ChatMessageVO> list= Stream.concat(oldChatMessages.stream(),newChatMessages.stream()).collect(Collectors.toList());
        return newChatMessages;
    }
}
