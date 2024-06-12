package com.luck.chat.service.imlp;

import com.luck.chat.constant.GroupChatRoleConstant;
import com.luck.chat.constant.MessageReadStatusType;
import com.luck.chat.constant.MessageTypeConstant;
import com.luck.chat.constant.RedisConstant;
import com.luck.chat.context.BaseContext;
import com.luck.chat.mapper.CategoryMapper;
import com.luck.chat.mapper.ChatMapper;
import com.luck.chat.mapper.GroupMapper;
import com.luck.chat.mapper.UserMapper;
import com.luck.chat.pojo.dto.GroupMessageDTO;
import com.luck.chat.pojo.dto.UserLoginDTO;
import com.luck.chat.pojo.dto.UserUpdateDTO;
import com.luck.chat.pojo.entity.*;
import com.luck.chat.pojo.vo.ChatListVO;
import com.luck.chat.pojo.vo.GroupMemberListVO;
import com.luck.chat.pojo.vo.UserLoginVo;
import com.luck.chat.pojo.vo.UserSearchVO;
import com.luck.chat.properties.JWTProperties;
import com.luck.chat.service.UserService;
import com.luck.chat.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.RandomStringUtils;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JWTProperties jwtProperties;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private ChatMapper chatMapper;

    /**
     * 对信息进行SHA1加密
     *
     * @param msg
     * @return
     */
    public static String getSHA1(String msg) {
        try {
            // 获取SHA-1摘要算法的 MessageDigest 实例
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            // 更新摘要使用指定的字节数组
            md.update(msg.getBytes());

            // 完成哈希计算，得到结果
            byte[] digest = md.digest();

            // 将得到的哈希值转换为十六进制字符串
            String hash = String.valueOf(Hex.encodeHex(digest));
            log.info("手机号加密:{}",hash);
            return hash;

        } catch (NoSuchAlgorithmException e) {
            System.out.println("SHA-1 algorithm not found.");
            return null;
        }
    }


    /**
     * 用户登录(第一次登录自动注册)
     *
     * @param userLoginDTO
     * @return
     */
    @Override
    public UserLoginVo login(UserLoginDTO userLoginDTO) {
        String encode_phone = getSHA1(userLoginDTO.getPhone());
        //先加密手机号,根据用户手机号查询用户信息
        userLoginDTO.setPhone(encode_phone);
        User user = userMapper.selectByPhone(encode_phone);
        if (user == null) {
            //注册用户(插入数据)
            user = new User();
            String randomName = "用户" + RandomStringUtils.randomAlphanumeric(10);
            user.setUsername(randomName);
            user.setCreateTime(LocalDateTime.now());
            user.setDescription("这里空空的,啥也没有");
            user.setPhone(encode_phone);
            user.setAvatar("https://chat-ncu.oss-cn-nanjing.aliyuncs.com/1-1715767463382");
            userMapper.insert(user);

            //用户注册后自动创建一个朋友分类
            Category category = Category.builder()
                    .userId(user.getId())
                    .categoryName("朋友")
                    .build();
            categoryMapper.insert(category);
        }

        //生成JWT令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        String token = JWTUtils.generateJWT(jwtProperties.getSecretKey(), jwtProperties.getExpiration(), claims);

        //封装VO
        UserLoginVo userLoginVo = new UserLoginVo(user.getId(), user.getUsername(), token);

        return userLoginVo;
    }

    /**
     * 根据用户id查询用户数据
     *
     * @param id
     * @return
     */
    @Override
    @Cacheable(value = RedisConstant.USERS_MSG, key = "#id")
    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    /**
     * 更新用户信息
     *
     * @param userUpdateDTO
     */
    @Override
    @CacheEvict(value = RedisConstant.USERS_MSG, key = RedisConstant.LOCAL_USERID)
    public void update(UserUpdateDTO userUpdateDTO) {
        User user = User.builder()
                .id(BaseContext.getCurrentId())
                .username(userUpdateDTO.getUsername())
                .description(userUpdateDTO.getDescription())
                .gender(userUpdateDTO.getGender())
                .avatar(userUpdateDTO.getAvatar())
                .build();

        userMapper.update(user);
    }

    /**
     * 通过手机号搜索用户
     *
     * @param phone
     * @return
     */
    @Override
    public UserSearchVO getByPhone(String phone) {
        User user = userMapper.selectByPhone(getSHA1(phone));
        if(user==null){
            return null;
        }
        UserSearchVO userSearchVO = new UserSearchVO();
        BeanUtils.copyProperties(user, userSearchVO);

        return userSearchVO;
    }

    /**
     * 创建群聊
     * @param groupMessageDTO
     */
    @Override
    @Transactional
    public ChatListVO createGroup(GroupMessageDTO groupMessageDTO) {
        //群主为当前用户
        GroupChat groupChat=GroupChat.builder()
                .groupAvatar(groupMessageDTO.getGroupAvatar())
                .groupName(groupMessageDTO.getGroupName())
                .creatorId(BaseContext.getCurrentId())
                .createTime(LocalDateTime.now())
                .build();
        List<Long> memberIdList=groupMessageDTO.getGroupMemberIdList();
        memberIdList.add(BaseContext.getCurrentId());
        //插入一个群聊表
        groupMapper.insertChat(groupChat);

        //插入群聊成员表,和所有成员的一个群聊聊天
        List<GroupChatMember> chatMemberList=new ArrayList<>();
        List<ChatList> chatLists=new ArrayList<>();
        for(Long userId:memberIdList){
            //群聊成员
            GroupChatMember groupChatMember=GroupChatMember
                    .builder()
                    .groupChatId(groupChat.getId())
                    .userId(userId)
                    .joinTime(LocalDateTime.now())
                    .build();
            if(userId.equals(BaseContext.getCurrentId())){
                groupChatMember.setRole(GroupChatRoleConstant.LEADER);
            }else {
                groupChatMember.setRole(GroupChatRoleConstant.MEMBER);
            }
            chatMemberList.add(groupChatMember);

            //每个用户的一个群聊聊天
            ChatList chatList=ChatList.builder()
                    .senderId(userId)
                    .groupChatId(groupChat.getId())
                    .build();
            chatLists.add(chatList);

        }
        categoryMapper.insertMemberList(chatMemberList);
        chatMapper.insertGroupChatList(chatLists);


        //当前用户的chatListVO的id就是最后一个chatList的id
        ChatListVO chatListVO =new ChatListVO();
        chatListVO.setChatName(groupChat.getGroupName());
        chatListVO.setChatAvatar(groupChat.getGroupAvatar());
        chatListVO.setChatListId(chatLists.get(chatLists.size()-1).getId());
        chatListVO.setLastMessageContent("");
        chatListVO.setLastMessageType(MessageTypeConstant.TEXT_MESSAGE);
        chatListVO.setReadStatus(MessageReadStatusType.UNREAD);
        chatListVO.setLastSendTime(LocalDateTime.now());

        return chatListVO;
    }


    /**
     * 根据群聊id获取群聊成员信息
     * @param groupChatId
     * @return
     */
    @Override
    public List<GroupMemberListVO> groupMemberList(Long groupChatId) {
        return groupMapper.selectGroupMemberMessageListById(groupChatId);
    }
}
