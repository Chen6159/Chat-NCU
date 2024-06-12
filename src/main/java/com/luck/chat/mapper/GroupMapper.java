package com.luck.chat.mapper;

import com.luck.chat.pojo.entity.ChatListIdWithUserId;
import com.luck.chat.pojo.entity.GroupChat;
import com.luck.chat.pojo.vo.GroupMemberListVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GroupMapper {

    /**
     * 根据id查找群聊信息
     * @param id
     * @return
     */
    @Select("select * from chat.group_chat where id=#{id}")
    GroupChat selectById(Long id);

    /**
     * 插入一个群聊表
     * @param groupChat
     */
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")
    @Insert("insert into chat.group_chat (group_name, group_avatar, creator_id, create_time) VALUES " +
            "(#{groupName},#{groupAvatar},#{creatorId},#{createTime}) ")
    void insertChat(GroupChat groupChat);

    /**
     * 联表查询对应群聊id的所有成员id还有对应的聊天listId;
     * @param groupChatId
     * @return
     */
    @Select("select c.id as chatListId,g.user_id as userId from chat.group_chat_member as g inner join chat.chat_list as c  " +
            "on g.group_chat_id=c.group_chat_id and g.user_id=c.sender_id " +
            "where g.group_chat_id=#{groupChatId}")
    List<ChatListIdWithUserId> selectChatListIdWithUserIdByGroupChatId(Long groupChatId);

    /**
     * 根据群聊id获取群聊的所有成员信息
     * @param groupChatId
     * @return
     */
    @Select("select g.*,u.username as userName,u.avatar as userAvatar from chat.group_chat_member as g inner join chat.user as u " +
            "on g.user_id=u.id " +
            "where g.group_chat_id =#{groupChatId}")
    List<GroupMemberListVO> selectGroupMemberMessageListById(Long groupChatId);
}
