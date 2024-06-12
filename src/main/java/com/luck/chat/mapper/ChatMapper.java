package com.luck.chat.mapper;

import com.luck.chat.pojo.entity.ChatList;
import com.luck.chat.pojo.entity.ChatMessage;
import com.luck.chat.pojo.vo.ChatMessageVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ChatMapper {
    /**
     * 插入单聊列表
     * @param chatSingleList
     */
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")
    @Insert("insert into chat.chat_list (sender_id, receiver_id) VALUES " +
            "(#{senderId},#{receiverId})")
    void insertSingleChatList(ChatList chatSingleList);


    /**
     *  插入群聊聊天列表
     * @param chatLists
     */
    void insertGroupChatList(List<ChatList> chatLists);



    /**
     * 批量插入聊天消息
     * @param chatMessageList
     */
    void insertChatMessageList(List<ChatMessage> chatMessageList);

    /**
     * 根据id查找聊天列表
     * @param chatListId
     * @return
     */
    @Select("select * from chat.chat_list where id=#{chatListId}")
    ChatList selectChatListById(Long chatListId);

    /**
     * 根据senderId和receiverId查找聊天列表
     * @param senderId
     * @param receiverId
     * @return
     */
    @Select("select * from chat.chat_list where sender_id=#{senderId} and receiver_id=#{receiverId}")
    ChatList selectChatListBySenderIdAndReceiverId(Long senderId, Long receiverId);

    /**
     * 查找该用户所有聊天列表
     * @param userId
     * @return
     */
    @Select("select * from chat.chat_list where sender_id=#{userId}")
    List<ChatList> selectChatListsByUserId(Long userId);

    /**
     * 根据chatListId查找最新的一条消息
     * @param chatListId
     * @return
     */
    @Select("select * from chat.chat_message where chat_list_id=#{chatListId} order by send_time desc limit 1")
    ChatMessage selectLatestMessage(Long chatListId);

    /**
     * 获取最新的聊天消息
     * @param chatListId
     * @param oldMessageId
     * @return
     */
    @Select("select m.*,u.username as userName,u.avatar as userAvatar from chat.chat_message as m inner join chat.user as u on m.user_id=u.id" +
            " where m.chat_list_id=#{chatListId} and m.id>#{oldMessageId} order by m.send_time ")
    List<ChatMessageVO> selectNewChatMessages(Long chatListId, Long oldMessageId);

    /**
     * 获取对应消息的前十条消息
     * @param chatListId
     * @param oldMessageId
     * @return
     */
    @Select("select m.*,u.username as userName,u.avatar as userAvatar from chat.chat_message as m inner join chat.user as u on m.user_id=u.id " +
            "where m.chat_list_id=#{chatListId} and m.id<=#{oldMessageId} order by m.send_time limit 10")
    List<ChatMessageVO> selectOldChatMessagesForTen(Long chatListId,Long oldMessageId);


}
