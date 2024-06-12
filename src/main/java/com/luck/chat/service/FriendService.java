package com.luck.chat.service;

import com.luck.chat.pojo.dto.FriendAddDTO;
import com.luck.chat.pojo.dto.FriendInvitationDTO;
import com.luck.chat.pojo.entity.FriendInvitation;
import com.luck.chat.pojo.entity.ReadReceipt;
import com.luck.chat.pojo.vo.FriendListVO;

import java.util.List;

public interface FriendService {

    /**
     * 添加好友申请
     * @param friendAddDTO
     */
    void save(FriendAddDTO friendAddDTO);


    /**
     * 根据userId和发送方id查询好友申请记录
     * @param userId
     * @param senderId
     * @return
     */
    FriendInvitation getByUserIdAndSenderId(Long userId,Long senderId);

    /**
     * 根据用户id获取好友列表
     * @param userId
     * @return
     */
    List<FriendInvitation> invitationListByUserId(Long userId);

    /**
     * 根据用户id获取好友列表信息(按名字排序)
     * @param userId
     * @return
     */
    List<FriendListVO> friendListByUserId(Long userId);

    /**
     * 根据用户id和分类id获取好友列表信息
     * @param userId
     * @param categoryId
     * @return
     */
    List<FriendListVO> friendListByUserIdAndCategoryId(Long userId, Long categoryId);

    /**
     * 处理好友申请
     * @param friendInvitationDTO
     */
    void handle(FriendInvitationDTO friendInvitationDTO);

    /**
     * 获取好友已读回执信息
     * @param userId
     * @param friendId
     * @return
     */
    ReadReceipt getReadReceipt(Long userId, Long friendId);
}
