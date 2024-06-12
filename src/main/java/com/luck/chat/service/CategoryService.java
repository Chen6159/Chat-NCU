package com.luck.chat.service;

import com.luck.chat.pojo.dto.CategoryFriendDTO;
import com.luck.chat.pojo.dto.CategoryModifyDTO;
import com.luck.chat.pojo.vo.CategoryAddVO;
import com.luck.chat.pojo.vo.CategoryListVO;

import java.util.List;

public interface CategoryService {

    /**
     * 新建分类
     * @param categoryName
     * @return
     */
    CategoryAddVO save(String categoryName);

    /**
     * 获取用户id对应的所有分类信息
     * @param userId
     * @return
     */
    List<CategoryListVO> list(Long userId);

    /**
     * 是否存在该分类
     * @param categoryName
     * @return
     */
    boolean contains(String categoryName);

    /**
     * 修改分类名称
     * @param categoryModifyDTO
     */
    void update(CategoryModifyDTO categoryModifyDTO);

    /**
     * 批量修改好友所属的分类
     * @param categoryFriendDTO
     */
    void modifyFriendCategory(CategoryFriendDTO categoryFriendDTO);
}
