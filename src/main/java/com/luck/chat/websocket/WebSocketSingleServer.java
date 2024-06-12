package com.luck.chat.websocket;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luck.chat.constant.*;
import com.luck.chat.context.BaseContext;
import com.luck.chat.mapper.ChatMapper;
import com.luck.chat.mapper.GroupMapper;
import com.luck.chat.pojo.dto.ChatMessageDTO;
import com.luck.chat.pojo.entity.ChatList;
import com.luck.chat.pojo.entity.ChatListIdWithUserId;
import com.luck.chat.pojo.entity.ChatMessage;
import com.luck.chat.pojo.vo.ChatMessageVO;
import com.luck.chat.properties.JWTProperties;
import com.luck.chat.utils.JWTUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * ws服务
 */

@Component
@Slf4j
public class WebSocketSingleServer extends TextWebSocketHandler {


    /**
     * 记录当前连接数
     */
    private static ConcurrentHashMap<Long, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ChatMapper chatMapper;

    @Autowired
    private JWTProperties jwtProperties;

    @Autowired
    private GroupMapper groupMapper;

    /**
     * 连接成功调用方法
     * @param session
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        URI uri=session.getUri();
        String token = "";
        if (uri != null && uri.getQuery() != null) {
            // 提取查询字符串
            String query = uri.getQuery();

            // 直接解析 token 参数
            String[] keyValue = query.split("=");
            if ("token".equals(keyValue[0]) && keyValue.length > 1) {
                token = keyValue[1];
            }
        }
        if (StringUtils.isBlank(token)){
            session.close();
            log.error("token错误");
        }
        try {
            log.info("jwt校验:{}", token);
            Jws<Claims> claimsJws = JWTUtils.parseJWT(token,jwtProperties.getSecretKey());
            Long userId = Long.valueOf(claimsJws.getPayload().get(JWTClaimsConstant.USER_ID).toString());
            //存放当前用户id
            BaseContext.setCurrentId(userId);
            session.getAttributes().put("userId",userId);
            log.info("当前用户id：{}", userId);
            session.getAttributes().put("userId",userId);
            sessionMap.put(userId,session);
            log.info("新建连接session={},当前在线人数:{}",session.getId(),sessionMap.size());
        } catch (Exception ex) {
            log.error("token校验失败!");
            session.close();
        }

    }


    /**
     * 处理文本消息
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("收到消息:{}",message);
        String json=message.getPayload();
        JsonNode jsonNode=objectMapper.readTree(json);
        Long userId=(Long)session.getAttributes().get("userId");
        if(jsonNode.get("messageContent")!=null&&!jsonNode.get("messageContent").isEmpty()){
            ChatMessageDTO chatMessageDTO=objectMapper.readValue(json, ChatMessageDTO.class);
            ChatMessage chatMessageForSender=ChatMessage.builder()
                    .chatListId(chatMessageDTO.getChatListId())
                    .userId(userId)
                    .messageContent(chatMessageDTO.getMessageContent())
                    .messageType(chatMessageDTO.getMessageType())
                    .readStatus(MessageReadStatusType.UNREAD)
                    .sendTime(LocalDateTime.now())
                    .build();
            //前端VO展示
            ChatMessageVO chatMessageVOForSender=ChatMessageVO.builder()
                    .userAvatar(chatMessageDTO.getUserAvatar())
                    .userName(chatMessageDTO.getUserName())
                    .build();
            BeanUtils.copyProperties(chatMessageForSender,chatMessageVOForSender);

            //获取到聊天列表后,通过聊天列表查找对应是否为单聊或者群聊
            Long chatListId=chatMessageForSender.getChatListId();
            ChatList chatSenderList=new ChatList();
            //查询对应userId,和chatListId是否是单聊
            String key1=RedisConstant.USERS_CHAT_LIST + chatListId;
            if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(key1))){
                chatSenderList= objectMapper.readValue(stringRedisTemplate.opsForValue().get(key1),ChatList.class);
            }else{
                //这里通过
                chatSenderList=chatMapper.selectChatListById(chatListId);
                stringRedisTemplate.opsForValue().set(key1,objectMapper.writeValueAsString(chatSenderList),72, TimeUnit.HOURS);
            }
            if(chatSenderList.getGroupChatId()==null||chatSenderList.getGroupChatId().equals(0L)){
                //单聊
                //查出对方的聊天列表id
                ChatList chatReceiverList=new ChatList();
                String key2=RedisConstant.USERS_CHAT_LIST +chatSenderList.getReceiverId()+chatSenderList.getSenderId();//使用receiverId+SenderId拼接(因为是对方,所以要反过来)
                if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(key2))){
                    chatReceiverList= objectMapper.readValue(stringRedisTemplate.opsForValue().get(key2),ChatList.class);
                }else{
                    chatReceiverList= chatMapper.selectChatListBySenderIdAndReceiverId(chatSenderList.getReceiverId(),chatSenderList.getSenderId());
                    stringRedisTemplate.opsForValue().set(key2,objectMapper.writeValueAsString(chatReceiverList),72,TimeUnit.HOURS);
                }
                //封装接受者的message
                ChatMessage chatMessageForReceiver=ChatMessage.builder()
                        .chatListId(chatReceiverList.getId())//这个决定放在哪个聊天窗口
                        .userId(userId)//这个决定聊天信息在左边还是右边
                        .messageContent(chatMessageDTO.getMessageContent())
                        .messageType(chatMessageDTO.getMessageType())
                        .readStatus(MessageReadStatusType.UNREAD)
                        .sendTime(LocalDateTime.now())
                        .build();
                //前端VO展示
                ChatMessageVO chatMessageVOForReceiver=ChatMessageVO.builder()
                        .userAvatar(chatMessageDTO.getUserAvatar())
                        .userName(chatMessageDTO.getUserName())
                        .build();
                BeanUtils.copyProperties(chatMessageForReceiver,chatMessageVOForReceiver);

                //插入到数据库后发送
                List<ChatMessage> chatMessages=List.of(chatMessageForSender,chatMessageForReceiver);
                chatMapper.insertChatMessageList(chatMessages);
                chatMessageVOForReceiver.setId(chatMessageForReceiver.getId());//id补充
                chatMessageVOForSender.setId(chatMessageForSender.getId());//上面那个id补充
                sendToUser(userId,objectMapper.writeValueAsString(chatMessageVOForSender));
                sendToUser(chatSenderList.getReceiverId(),objectMapper.writeValueAsString(chatMessageVOForReceiver));
            }else{
                //通过这个聊天列表,向该聊天列表中的群聊id对应的所有成员端推送消息
                Long groupChatId=chatSenderList.getGroupChatId();
                //通过群聊id拿到对应所有成员id,再通过群聊id和成员id找到每个成员所属的列表id ,封装好n条消息推送
                //inner联表查询拿到这个每个用户id对应的chatListId
                String key2=RedisConstant.USERS_CHAT_LISTS + groupChatId;
                List<ChatListIdWithUserId> chatListIdWithUserIds=new ArrayList<>();
                if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(key2))){
                    chatListIdWithUserIds=objectMapper.readValue(stringRedisTemplate.opsForValue().get(key2),  new TypeReference<List<ChatListIdWithUserId>>(){});
                }else{
                    chatListIdWithUserIds =groupMapper.selectChatListIdWithUserIdByGroupChatId(groupChatId);
                    stringRedisTemplate.opsForValue().set(key2,objectMapper.writeValueAsString(chatListIdWithUserIds),72,TimeUnit.HOURS);
                }

                //拿到存数据库的消息集合和给前端的消息集合
                List<ChatMessage> chatMessageList=new ArrayList<>();
                List<ChatMessageVO> chatMessageVOList=new ArrayList<>();
                List<Long> userIdList=new ArrayList<>();//这个主要是为了给每条消息发送给对应的ws客户端
                chatListIdWithUserIds.forEach((chatListIdWithUserId -> {
                    //拿出id和listId封装先存数据库再发送
                    ChatMessage chatMessage=ChatMessage.builder()
                            .chatListId(chatListIdWithUserId.getChatListId())
                            .userId(userId)
                            .messageContent(chatMessageDTO.getMessageContent())
                            .messageType(chatMessageDTO.getMessageType())
                            .readStatus(MessageReadStatusType.UNREAD)
                            .sendTime(LocalDateTime.now())
                            .build();
                    chatMessageList.add(chatMessage);

                    ChatMessageVO chatMessageVO=ChatMessageVO.builder()
                            .userAvatar(chatMessageDTO.getUserAvatar())
                            .userName(chatMessageDTO.getUserName())
                            .build();
                    BeanUtils.copyProperties(chatMessage,chatMessageVO);
                    chatMessageVOList.add(chatMessageVO);

                    userIdList.add(chatListIdWithUserId.getUserId());//各个用户id集合
                }));
                chatMapper.insertChatMessageList(chatMessageList);//批量插入数据库
                for(int i=0;i<chatMessageVOList.size();i++){
                    chatMessageVOList.get(i).setId(chatMessageList.get(i).getId());
                    //发送ws客户端
                    sendToUser(userIdList.get(i),objectMapper.writeValueAsString(chatMessageVOList.get(i)));
                }

            }

        }
    }
    
    /**
     * 连接关闭调用方法
     * @param session
     * @param status
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        try {
            Long userId=(Long) session.getAttributes().get("userId");
            if(sessionMap.containsKey(userId)){
                sessionMap.remove(userId);
            }
        }finally {
            BaseContext.removeCurrentId();//释放当前线程资源
            log.info("连接session:{},关闭,当前在线人数:{}",session.getId(),sessionMap.size());
        }

    }


    /**
     * 推送消息给指定用户端
     * @param receiverId
     * @param message
     */
    public void sendToUser(Long receiverId,String message){
        WebSocketSession session=sessionMap.get(receiverId);
        try {
            //这里推送的消息给前端即可,前端会解析看是否是通知或者其他消息
            if(session!=null){
                session.sendMessage(new TextMessage(message));
            }else{
                log.info("对方不在线,未建立连接");
            }
        } catch (IOException e) {
            log.error("发送消息失败,接收方id:{}",receiverId);
        }
    }


}
