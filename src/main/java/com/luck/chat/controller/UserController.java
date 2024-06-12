package com.luck.chat.controller;

import ch.qos.logback.core.testUtil.RandomUtil;
import com.luck.chat.constant.CodeConstant;
import com.luck.chat.constant.RedisConstant;
import com.luck.chat.context.BaseContext;
import com.luck.chat.pojo.dto.GroupMessageDTO;
import com.luck.chat.pojo.dto.UserLoginDTO;
import com.luck.chat.pojo.dto.UserUpdateDTO;
import com.luck.chat.pojo.entity.Result;
import com.luck.chat.pojo.entity.User;
import com.luck.chat.pojo.vo.ChatListVO;
import com.luck.chat.pojo.vo.GroupMemberListVO;
import com.luck.chat.pojo.vo.UserLoginVo;
import com.luck.chat.pojo.vo.UserSearchVO;
import com.luck.chat.service.UserService;
import com.luck.chat.utils.AliSmsUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AliSmsUtils aliSmsUtils;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 获取验证码
     * @param phone
     * @return
     */
    @PostMapping("/code")
    public Result getCode(String phone) throws Exception {
        if(!(Pattern.matches("^1[3-9]\\d{9}$",phone))){
            return Result.error(CodeConstant.WRONG_PHONE_NUMBER);
        }

        //判断是否重复获取过验证码
        String key= RedisConstant.SMS +phone;
        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))){
            log.info("重复获取验证码!");
            return Result.error(CodeConstant.REPEAT_GET_CODE);
        }

        log.info("获取验证码手机号:{}",phone);
        //随机生成一个4位数字
        String code= String.valueOf((1000+ new Random().nextInt(9000)));

        //发送失败
        if(!aliSmsUtils.sendSms(phone,code)){
            return Result.error(CodeConstant.FAIL_SEND_CODE);
        }

        //发送成功再把验证码放到redis中,1分钟过期
        stringRedisTemplate.opsForValue().set(key,code,1, TimeUnit.MINUTES);
        return Result.success();
    }



    /**
     * 用户登录
     * @param userLoginDTO
     * @return
     */
    @PostMapping("/login")
    public Result<UserLoginVo> login(@RequestBody UserLoginDTO userLoginDTO){
        log.info("登录参数:{}",userLoginDTO);

        //第一次登录就算自动注册账号
        //检查redis中对应key的验证码,检查完后生成jwt令牌.有效期三天,方法结束后清除对应key值

        if(Objects.equals(stringRedisTemplate.opsForValue().get(RedisConstant.SMS + userLoginDTO.getPhone()), userLoginDTO.getCode())){
            //10分钟后redis中的验证码自动过期
            return Result.success(userService.login(userLoginDTO));
        }
        return Result.error(CodeConstant.WRONG_CODE);

    }

    /**
     * 根据id查询用户信息
     * @return
     */
    @GetMapping("/{id}")
    public  Result<User> queryById(@PathVariable Long id){
        log.info("用户id:{}", id);
        User user=userService.getById(id);
        if(user==null){
            return Result.error(CodeConstant.FAIL_GET_USER_MSG);
        }
        return Result.success(user);
    }

    /**
     * 更新用户信息
     * @param userUpdateDTO
     * @return
     */
    @PutMapping("/update")
    public Result modify(@RequestBody UserUpdateDTO userUpdateDTO){
        log.info("用户更新的信息:{}",userUpdateDTO);
        userService.update(userUpdateDTO);
        return Result.success();
    }

    /**
     * 通过手机号搜索用户
     * @param phone
     * @return
     */
    @GetMapping("/search")
    public Result<UserSearchVO> queryByPhone(String phone){
        log.info("通过手机号搜索用户:{}",phone);
        if(!(Pattern.matches("^1[3-9]\\d{9}$",phone))){
            return Result.error(CodeConstant.WRONG_PHONE_NUMBER);
        }
        UserSearchVO userSearchVO=userService.getByPhone(phone);
        if(userSearchVO==null||userSearchVO.getId()==null){
            return Result.error(CodeConstant.USER_NOT_EXIST);
        }
        return Result.success(userSearchVO);
    }


    /**
     * 创建群聊
     * @return
     */
    @PostMapping("/groupChat/add")
    public Result<ChatListVO> createGroupChat(@RequestBody GroupMessageDTO groupMessageDTO){
        log.info("创建群聊,群聊名称:{}",groupMessageDTO.getGroupName());
        try {
            return Result.success(userService.createGroup(groupMessageDTO));
        } catch (Exception e) {
            return Result.error(CodeConstant.CREATE_GROUP_CHAT_ERROR);
        }
    }

    /**
     * 根据群聊id获取群聊内所有信息
     * @param groupChatId
     * @return
     */
    @GetMapping("/groupChat/{groupChatId}")
    public Result<List<GroupMemberListVO>> groupMemberListByGroupChatId(@PathVariable Long groupChatId){
        log.info("获取群聊id:{}的所有信息",groupChatId);
        List<GroupMemberListVO> list= userService.groupMemberList(groupChatId);
        if(list==null||list.isEmpty()){
            return Result.error(CodeConstant.WRONG_GROUP_CHAT_ID);
        }
        return Result.success(list);
    }

}
