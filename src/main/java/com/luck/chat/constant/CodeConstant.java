package com.luck.chat.constant;


public class CodeConstant {

    //成功响应
    public static final Integer SUCCESS=1;

    //一般响应失败码
    public static final Integer FAIL=0;

    //一分钟内获取过验证码
    public static final Integer REPEAT_GET_CODE=2;

    //服务端请求发送验证码失败
    public static final Integer FAIL_SEND_CODE=3;

    //获取STS访问凭证失败
    public static final Integer FAIL_GET_STS=4;

    //验证码不匹配,用户登录/注册失败
    public static final Integer WRONG_CODE=5;

    //服务端获取用户信息失败
    public static final Integer FAIL_GET_USER_MSG=6;

    //该用户不存在
    public static final Integer USER_NOT_EXIST=7;

    //用户分类信息为空
    public static final Integer NULL_CATEGORY=8;

    //该分类已经存在
    public static final Integer ALREADY_EXIST_CATEGORY=9;

    //手机号不匹配
    public static final Integer WRONG_PHONE_NUMBER=10;

    //已发送好友申请
    public static final Integer  INVITATION_SENT=11;

    //创建群聊失败,可能需要重试
    public static final Integer CREATE_GROUP_CHAT_ERROR=12;

    //无该群聊信息!
    public static final Integer WRONG_GROUP_CHAT_ID=13;

    //消息不能为空
    public static final Integer MESSAGE_NOT_NULL=14;

}
