package com.luck.chat.controller;

import com.luck.chat.constant.CodeConstant;
import com.luck.chat.context.BaseContext;
import com.luck.chat.pojo.dto.FriendAddDTO;
import com.luck.chat.pojo.dto.FriendInvitationDTO;
import com.luck.chat.pojo.entity.FriendInvitation;
import com.luck.chat.pojo.entity.ReadReceipt;
import com.luck.chat.pojo.entity.Result;
import com.luck.chat.pojo.vo.FriendListVO;
import com.luck.chat.service.FriendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 好友相关接口
 */
@RestController
@Slf4j
@RequestMapping("/friend")
public class FriendController {



    @Autowired
    private FriendService friendService;

    /**
     * 请求添加好友
     * @param friendAddDTO
     * @return
     */
    @PostMapping("/add")
    public Result add(@RequestBody FriendAddDTO friendAddDTO){
        log.info("好友{}请求添加好友{}", BaseContext.getCurrentId(),friendAddDTO.getReceiverId());
        if(friendAddDTO.getHelloWord()==null|| friendAddDTO.getHelloWord().isEmpty()){
            return Result.error(CodeConstant.MESSAGE_NOT_NULL);
        }
        if(friendService.getByUserIdAndSenderId(friendAddDTO.getReceiverId(),BaseContext.getCurrentId())!=null){
            return Result.error(CodeConstant.INVITATION_SENT);//已发送过好友申请
        }
        friendService.save(friendAddDTO);
        return Result.success();
    }


    /**
     * 获取好友列表信息(按名字排序)
     * @return
     */
    @GetMapping("/list")
    public Result<List<FriendListVO>> friendList(){
        log.info("请求获取用户id{} 的好友列表信息",BaseContext.getCurrentId());
        return Result.success(friendService.friendListByUserId(BaseContext.getCurrentId()));
    }


    /**
     * 根据分类id获取用户对应好友列表
     * @param categoryId
     * @return
     */
    @GetMapping("/list/{categoryId}")
    public Result<List<FriendListVO>> friendList(@PathVariable Long categoryId){
        log.info("分类id为{},对应当前用户的好友列表",categoryId);
        return Result.success(friendService.friendListByUserIdAndCategoryId(BaseContext.getCurrentId(),categoryId));
    }



    /**
     * 获取当前用户申请列表
     * @return
     */
    @GetMapping("/invitationList")
    public Result<List<FriendInvitation>> invitationList(){
        log.info("获取用户申请列表");

        List<FriendInvitation> list=friendService.invitationListByUserId(BaseContext.getCurrentId());

        return Result.success(list);
    }


    /**
     * 处理好友申请
     */
    @PostMapping("/handle")
    public Result handleInvitation(@RequestBody FriendInvitationDTO friendInvitationDTO){
        log.info("处理用户好友申请信息");
        friendService.handle(friendInvitationDTO);
        return Result.success();
    }

    /**
     * 获取好友已读回执信息
     * @return
     */
    @GetMapping("/readReceipt/{friendId}")
    public Result<ReadReceipt> getReadReceipt(@PathVariable Long friendId){
        log.info("获取好友已读回执信息");
        ReadReceipt readReceipt=friendService.getReadReceipt(BaseContext.getCurrentId(),friendId);
        return Result.success(readReceipt);
    }

}
