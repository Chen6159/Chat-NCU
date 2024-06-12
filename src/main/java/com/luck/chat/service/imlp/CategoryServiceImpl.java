package com.luck.chat.service.imlp;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.luck.chat.constant.RedisConstant;
import com.luck.chat.context.BaseContext;
import com.luck.chat.mapper.CategoryMapper;
import com.luck.chat.pojo.dto.CategoryFriendDTO;
import com.luck.chat.pojo.dto.CategoryModifyDTO;
import com.luck.chat.pojo.entity.Category;
import com.luck.chat.pojo.entity.Result;
import com.luck.chat.pojo.vo.CategoryAddVO;
import com.luck.chat.pojo.vo.CategoryListVO;
import com.luck.chat.service.CategoryService;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {


    @Autowired
    private CategoryMapper categoryMapper;


    /**
     * 新建分类
     * @param categoryName
     * @return
     */
    @Override
    @CacheEvict(value = RedisConstant.USERS_CATEGORY_LIST,key = RedisConstant.LOCAL_USERID)
    public CategoryAddVO save(String categoryName) {

        Category category=Category.builder()
                .userId(BaseContext.getCurrentId())
                .categoryName(categoryName)
                .build();
        categoryMapper.insert(category);

        return new CategoryAddVO(category.getId());
    }

    /**
     * 获取对应用户id的所有分类信息
     * @param userId
     * @return
     */
    // todo 选择定时清理redis中的数据,可以通过前端传递每次这个时间点的未在线用户id列表来删除redis中对应数据
    @Override
    @Cacheable(value = RedisConstant.USERS_CATEGORY_LIST,key = "#userId")
    public List<CategoryListVO> list(Long userId) {
        List<CategoryListVO> categoryListVOList=new ArrayList<>();

        List<Category> categoryList =categoryMapper.list(userId);
        categoryList.forEach(it->{
            CategoryListVO categoryListVO=new CategoryListVO();
            BeanUtils.copyProperties(it,categoryListVO);
            categoryListVOList.add(categoryListVO);
        });

        return categoryListVOList;
    }

    /**
     * 判断是否存在该分类信息
     * @param categoryName
     * @return
     */
    @Override
    public boolean contains(String categoryName) {

        Category category=Category.builder()
                .userId(BaseContext.getCurrentId())
                .categoryName(categoryName)
                .build();
        return categoryMapper.contains(category);
    }

    /**
     * 修改分类名称
     * @param categoryModifyDTO
     */
    @Override
    @CacheEvict(value = RedisConstant.USERS_CATEGORY_LIST,key = RedisConstant.LOCAL_USERID)
    public void update(CategoryModifyDTO categoryModifyDTO) {
        //修改当前用户id的一个分类
        categoryMapper.update(categoryModifyDTO.getCategoryId(),categoryModifyDTO.getCategoryName());
    }


    /**
     * 批量修改好友所属分类
     * @param categoryFriendDTO
     */
    @Override
    public void modifyFriendCategory(CategoryFriendDTO categoryFriendDTO) {
        Long userId=BaseContext.getCurrentId();
        Long categoryId= categoryFriendDTO.getCategoryId();
        List<Long> friendIdList=categoryFriendDTO.getFriendIdList();
        friendIdList.forEach(friendId->{
            categoryMapper.updateFriendCategoryList(userId,categoryId,friendId);
        });
    }
}
