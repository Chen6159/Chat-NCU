package com.luck.chat.mapper;

import com.luck.chat.pojo.entity.Friend;
import com.luck.chat.pojo.entity.FriendInvitation;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FriendMapper {

    /**
     * 插入一条好友申请
     * @param friendInvitation
     */
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")
    @Insert("insert into chat.friend_invitation (user_id, sender_id, sender_name, sender_avatar,category_id, hello_word, status, create_time) VALUES " +
            "(#{userId},#{senderId},#{senderName},#{senderAvatar},#{categoryId},#{helloWord},#{status},#{createTime})")
    void insert(FriendInvitation friendInvitation);

    /**
     * 根据userId和发送方id查询好友申请记录
     * @param userId
     * @param senderId
     * @return
     */
    @Select("select * from chat.friend_invitation where user_id=#{userId} and sender_id=#{senderId}")
    FriendInvitation selectInvitationByUserIdAndSenderId(Long userId, Long senderId);

    /**
     * 根据id查询请求申请
     * @param userId
     * @return
     */
    @Select("select * from chat.friend_invitation where user_id=#{userId} and status=0  order by create_time desc")
    List<FriendInvitation> selectInvitationListByUserId(Long userId);


    /**
     * 根据id查询所有对应好友列表
     * @param userId
     * @return
     */
    @Select("select " +
            "friend.id as id," +
            "#{userId} as userId," +
            "user.id as friendId," +
            "user.avatar as friendAvatar," +
            "user.username as friendName," +
            "friend.category_id as categoryId," +
            "friend.create_time as createTime " +
            "from chat.user inner join chat.friend on friend.friend_id=user.id " +
            "where friend.user_id=#{userId} order by user.username")
    List<Friend> selectByUserId(Long userId);

    /**
     * 插入一条好友信息
     * @param friend
     */
    @Insert("insert into chat.friend (user_id, friend_id, category_id, create_time) VALUES " +
            "(#{userId},#{friendId},#{categoryId},#{createTime})")
    void insertFriend(Friend friend);


    /**
     * 修改好友申请状态(改为接受)
     * @param invitation
     */
    @Update("update chat.friend_invitation set status =#{status} where id=#{id}")
    void updateInvitationStatus(FriendInvitation invitation);

    /**
     * 根据userId和senderId删除好友申请
     * @param userId
     * @param senderId
     */
    @Delete("delete from chat.friend_invitation where user_id=#{userId} and sender_id=#{senderId}")
    void deleteInvitationByUserIdAndSenderId(Long userId, Long senderId);
}
