package com.luck.chat.constant;

public class RedisConstant {

    //redis验证码分区
    public static final String SMS="verif:";

    //用户基本信息分区
    public static final String USERS_MSG="users:msg:";

    //当前用户id(SpEL表达式)
    public static final String LOCAL_USERID="T(com.luck.chat.context.BaseContext).currentId";

    //用户分类信息集合
    public static final String USERS_CATEGORY_LIST="users:categoryList:";

    //用户好友申请信息分区
    public static final String USERS_INVITATION_LIST="users:invitationList:";

    //用户好友列表信息分区
    public static final String USERS_FRIEND_LIST="users:friendList:";

    //用户好友列表分类信息分区
    public static final String USERS_FRIEND_LIST_CATEGORY="users:friendList:category:";

    //用户聊天列表信息分区
    public static final String USERS_CHAT_LIST="users:chatList:";

    //用户聊天列表集合分区
    public static final String USERS_CHAT_LISTS="users:chatLists:";

    //群聊信息分区
    public static final String GROUP_CHAT_MESSAGE="group:chatMessage:";

    //好友已读回执信息
    public static final String FRIEND_READ_RECEIPT="friend:readReceipt:";
}
