package com.luck.chat.mapper;

import com.luck.chat.pojo.entity.Category;
import com.luck.chat.pojo.entity.GroupChatMember;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategoryMapper {


    /**
     * 新建分类
     * @param category
     * @return
     */
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")
    @Insert("insert into chat.category (user_id, category_name) VALUES " +
            "(#{userId},#{categoryName})")
    void insert(Category category);

    /**
     * 查询对应userId的所有分类
     * @param userId
     * @return
     */
    @Select("select * from chat.category where user_id=#{userId}")
    List<Category> list(Long userId);

    /**
     * 判断该分类是否存在
     * @param category
     * @return
     */
    @Select("select exists(select 1 from chat.category where user_id=#{userId} and category_name=#{categoryName})")
    boolean contains(Category category);

    /**
     * 根据userId和分类id修改分类名称
     * @param categoryId
     * @param categoryName
     */
    @Update("update chat.category set category_name=#{categoryName} where id=#{categoryId}")
    void update(Long categoryId, String categoryName);

    /**
     * 批量修改好友分类
     * @param userId
     * @param categoryId
     * @param friendId
     */
    @Update("update chat.friend set category_id=#{categoryId}" +
            " where user_id=#{userId} and friend_id=#{friendId}")
    void updateFriendCategoryList(Long userId, Long categoryId, Long friendId);

    /**
     * 添加群聊成员表
     * @param chatMemberList
     */
    void insertMemberList(List<GroupChatMember> chatMemberList);
}
