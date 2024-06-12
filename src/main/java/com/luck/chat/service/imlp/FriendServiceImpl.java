package com.luck.chat.service.imlp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luck.chat.constant.FriendStatusConstant;
import com.luck.chat.constant.MessageReadStatusType;
import com.luck.chat.constant.MessageTypeConstant;
import com.luck.chat.constant.RedisConstant;
import com.luck.chat.context.BaseContext;
import com.luck.chat.mapper.ChatMapper;
import com.luck.chat.mapper.FriendMapper;
import com.luck.chat.pojo.dto.FriendAddDTO;
import com.luck.chat.pojo.dto.FriendInvitationDTO;
import com.luck.chat.pojo.entity.*;
import com.luck.chat.pojo.vo.ChatMessageVO;
import com.luck.chat.pojo.vo.FriendListVO;
import com.luck.chat.service.FriendService;
import com.luck.chat.service.UserService;
import com.luck.chat.websocket.WebSocketSingleServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
public class FriendServiceImpl implements FriendService {


    @Autowired
    private WebSocketSingleServer webSocketSingleServer;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private FriendMapper friendMapper;

    @Autowired
    private ChatMapper chatMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 添加好友申请
     *
     * @param friendAddDTO
     */
    @Override
    //这里要删除redis中对方的缓存数据
    @CacheEvict(value = RedisConstant.USERS_INVITATION_LIST, key = "#friendAddDTO.receiverId")
    public void save(FriendAddDTO friendAddDTO) {
        //这个好友申请要被推送给服务端,再由服务端推送给指定用户
        User sender = userService.getById(BaseContext.getCurrentId());
        FriendInvitation friendInvitation = FriendInvitation.builder()
                .userId(friendAddDTO.getReceiverId())//接收者id
                .senderId(BaseContext.getCurrentId())//发送者id
                .senderName(sender.getUsername())//发送者名字
                .senderAvatar(sender.getAvatar())//发送者头像
                .categoryId(friendAddDTO.getCategoryId())//发送方预选的分类id
                .helloWord(friendAddDTO.getHelloWord())//招呼语句
                .status(FriendStatusConstant.REQUESTING)//状态
                .createTime(LocalDateTime.now()).build();//时间
        friendMapper.insert(friendInvitation);
        //推送给指定用户的申请
        try {
            webSocketSingleServer.sendToUser(friendAddDTO.getReceiverId(), objectMapper.writeValueAsString(friendInvitation));
        } catch (JsonProcessingException e) {
            log.info("好友申请对象转json串失败");
        }
    }

    /**
     * 根据userId和发送方id查询好友申请记录
     *
     * @param userId
     * @param senderId
     * @return
     */
    @Override
    public FriendInvitation getByUserIdAndSenderId(Long userId, Long senderId) {
        return friendMapper.selectInvitationByUserIdAndSenderId(userId, senderId);
    }

    /**
     * 根据用户id获取好友申请列表
     *
     * @param userId
     * @return
     */
    @Override
    @Cacheable(value = RedisConstant.USERS_INVITATION_LIST, key = "#userId")
    public List<FriendInvitation> invitationListByUserId(Long userId) {
        //注意这里只是查询申请状态为 请求中的好友申请(放redis中)
        return friendMapper.selectInvitationListByUserId(userId);
    }


    /**
     * 获取所有好友列表信息(按名字排序)
     *
     * @param userId
     * @return
     */
    @Override
    public List<FriendListVO> friendListByUserId(Long userId) {
        String key=RedisConstant.USERS_FRIEND_LIST+userId;
        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))){
            try {
                return objectMapper.readValue(stringRedisTemplate.opsForValue().get(key), new TypeReference<List<FriendListVO>>(){});
            } catch (JsonProcessingException e) {
                log.error("redis解析好友列表信息失败!");
                stringRedisTemplate.delete(key);
            }
        }
        //注意按照名字排序
        List<Friend> friends = friendMapper.selectByUserId(userId);
        List<FriendListVO> friendList = new ArrayList<>();
        friends.forEach(friend -> {
            FriendListVO friendListVO = new FriendListVO();
            BeanUtils.copyProperties(friend, friendListVO);
            friendList.add(friendListVO);
        });
        try {
            stringRedisTemplate.opsForValue().set(key,objectMapper.writeValueAsString(friendList),72, TimeUnit.HOURS);
        } catch (JsonProcessingException e) {
            log.error("好友列表信息存redis失败");
            stringRedisTemplate.delete(key);
        }
        return friendList;
    }

    /**
     * 根据用户id和分类id获取好友列表信息
     *
     * @param userId
     * @param categoryId
     * @return
     */
    @Override
//    @Cacheable(value = RedisConstant.USERS_FRIEND_LIST_CATEGORY, key = "#userId.toString() + #categoryId.toString()")
    public List<FriendListVO> friendListByUserIdAndCategoryId(Long userId, Long categoryId) {
        List<Friend> friends = friendMapper.selectByUserId(userId);
        List<FriendListVO> friendList = new ArrayList<>();
        friends.forEach(friend -> {
            if (friend.getCategoryId().equals(categoryId)) {
                FriendListVO friendListVO = new FriendListVO();
                BeanUtils.copyProperties(friend, friendListVO);
                friendList.add(friendListVO);
            }
        });

        return friendList;
    }

    /**
     * 处理好友申请
     *
     * @param friendInvitationDTO
     */
    @Override
    @Transactional
    @CacheEvict(value = RedisConstant.USERS_INVITATION_LIST, key = RedisConstant.LOCAL_USERID)
    public void handle(FriendInvitationDTO friendInvitationDTO) {
        //首先,查询好友申请这个申请信息
        FriendInvitation invitation = getByUserIdAndSenderId(BaseContext.getCurrentId(), friendInvitationDTO.getSenderId());

        //如果同意,则添加好友,还要生成一个聊天单聊列表,修改好友申请信息状态,添加一条聊天记录,
        if (friendInvitationDTO.getStatus().equals(FriendStatusConstant.ACCEPTED)) {
            invitation.setStatus(FriendStatusConstant.ACCEPTED);
            //生成好友(双向的)
            Friend friendUser = Friend.builder()
                    .userId(BaseContext.getCurrentId())
                    .friendId(friendInvitationDTO.getSenderId())
                    .categoryId(friendInvitationDTO.getCategoryId())
                    .createTime(LocalDateTime.now())
                    .build();
            Friend friendSender = Friend.builder()//申请好友方
                    .userId(friendInvitationDTO.getSenderId())
                    .friendId(BaseContext.getCurrentId())
                    .categoryId(invitation.getCategoryId())
                    .createTime(LocalDateTime.now())
                    .build();
            friendMapper.insertFriend(friendUser);
            friendMapper.insertFriend(friendSender);

            //修改好友申请信息状态
            friendMapper.updateInvitationStatus(invitation);

            //生成聊天单聊列表
            ChatList chatSenderList=ChatList.builder()
                    .senderId(friendInvitationDTO.getSenderId())
                    .receiverId(BaseContext.getCurrentId())
                    .build();
            ChatList chatReceiverList=ChatList.builder()
                    .senderId(BaseContext.getCurrentId())
                    .receiverId(friendInvitationDTO.getSenderId())
                    .build();
            chatMapper.insertSingleChatList(chatSenderList);
            chatMapper.insertSingleChatList(chatReceiverList);

            //添加一条聊天记录,要推送一下这个聊天信息(推送聊天信息会携带列表id,userId,聊天信息(String),)
            ChatMessage chatMessageForSender=ChatMessage.builder()
                            .chatListId(chatSenderList.getId())
                    .userId(friendInvitationDTO.getSenderId())
                    .messageType(MessageTypeConstant.TEXT_MESSAGE)
                    .messageContent(invitation.getHelloWord())
                    .readStatus(MessageReadStatusType.UNREAD)
                    .sendTime(LocalDateTime.now())
                    .build();
            //前端VO展示
            ChatMessageVO chatMessageVOForSender=ChatMessageVO.builder()
                    .userAvatar(invitation.getSenderAvatar())
                    .userName(invitation.getSenderName())
                    .build();
            BeanUtils.copyProperties(chatMessageForSender,chatMessageVOForSender);

            ChatMessage chatMessageForUser=ChatMessage.builder()
                    .chatListId(chatReceiverList.getId())
                    .userId(friendInvitationDTO.getSenderId())
                    .messageType(MessageTypeConstant.TEXT_MESSAGE)
                    .messageContent(invitation.getHelloWord())
                    .readStatus(MessageReadStatusType.READ)//由于是当前用户处理请求,所以为已读
                    .sendTime(LocalDateTime.now())
                    .build();
            //前端VO展示
            ChatMessageVO chatMessageVOForUser=ChatMessageVO.builder()
                    .userAvatar(invitation.getSenderAvatar())
                    .userName(invitation.getSenderName())
                    .build();
            BeanUtils.copyProperties(chatMessageForUser,chatMessageVOForUser);

            //批量插入聊天消息
            List<ChatMessage> chatMessageList=List.of(chatMessageForSender,chatMessageForUser);
            chatMapper.insertChatMessageList(chatMessageList);
            try {
                //推送给发送消息的
                webSocketSingleServer.sendToUser(friendInvitationDTO.getSenderId(), objectMapper.writeValueAsString(chatMessageVOForSender));
                //推送给接收消息的
                webSocketSingleServer.sendToUser(BaseContext.getCurrentId(),objectMapper.writeValueAsString(chatMessageVOForUser));
            } catch (JsonProcessingException e) {
                log.error("处理好友请求时消息转json推送失败!");
            }

        }
        //如果拒绝,则删除好友申请信息
        else {
           friendMapper.deleteInvitationByUserIdAndSenderId(BaseContext.getCurrentId(),friendInvitationDTO.getSenderId());
        }
    }

    /***
     * 获取好友已读回执信息(暂时不写)
     * @param userId
     * @param friendId
     * @return
     */
    @Override
    public ReadReceipt getReadReceipt(Long userId, Long friendId) {

        return null;
    }


}
