package com.luck.chat.mapper;

import com.luck.chat.pojo.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    /**
     * 根据用户手机号查询用户信息
     * @param phone
     * @return
     */
    @Select("select * from chat.user where phone=#{phone}")
    User selectByPhone(String phone);


    /**
     * 插入用户数据
     * @param user
     */
    void insert(User user);

    /**
     * 根据id获取用户数据
     * @param id
     * @return
     */
    @Select("select * from chat.user where id=#{id}")
    User selectById(Long id);

    /**
     * 更新用户信息
     * @param user
     */
    void update(User user);
}
