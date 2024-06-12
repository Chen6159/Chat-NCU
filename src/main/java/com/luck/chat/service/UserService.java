package com.luck.chat.service;

import com.luck.chat.pojo.dto.GroupMessageDTO;
import com.luck.chat.pojo.dto.UserLoginDTO;
import com.luck.chat.pojo.dto.UserUpdateDTO;
import com.luck.chat.pojo.entity.User;
import com.luck.chat.pojo.vo.ChatListVO;
import com.luck.chat.pojo.vo.GroupMemberListVO;
import com.luck.chat.pojo.vo.UserLoginVo;
import com.luck.chat.pojo.vo.UserSearchVO;

import java.util.List;

public interface UserService {

    /**
     * 用户登录,第一次登录自动注册
     * @param userLoginDTO
     * @return
     */
    UserLoginVo login(UserLoginDTO userLoginDTO);

    /**
     * 根据用户id查询用户数据
     * @param id
     * @return
     */
    User getById(Long id);

    /**
     * 用户更新信息
     * @param userUpdateDTO
     */
    void update(UserUpdateDTO userUpdateDTO);

    /**
     * 通过手机号搜索用户
     * @param phone
     * @return
     */
    UserSearchVO getByPhone(String phone);


    /**
     * 创建群聊
     * @param groupMessageDTO
     */
    ChatListVO createGroup(GroupMessageDTO groupMessageDTO);

    /**
     * 根据群聊id获取群聊成员信息
     * @param groupChatId
     * @return
     */
    List<GroupMemberListVO> groupMemberList(Long groupChatId);

}
