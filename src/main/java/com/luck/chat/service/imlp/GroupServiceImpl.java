package com.luck.chat.service.imlp;

import com.luck.chat.constant.RedisConstant;
import com.luck.chat.mapper.GroupMapper;
import com.luck.chat.pojo.entity.GroupChat;
import com.luck.chat.service.GroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GroupServiceImpl implements GroupService {


    @Autowired
    private GroupMapper groupMapper;



    /**
     * 根据id查询群聊信息
     * @param id
     * @return
     */
    @Override
    @Cacheable(value = RedisConstant.GROUP_CHAT_MESSAGE,key = "#id")
    public GroupChat getById(Long id) {
        return groupMapper.selectById(id);
    }


}
