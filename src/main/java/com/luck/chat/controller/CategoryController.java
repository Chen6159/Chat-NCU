package com.luck.chat.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.luck.chat.constant.CodeConstant;
import com.luck.chat.constant.RedisConstant;
import com.luck.chat.context.BaseContext;
import com.luck.chat.pojo.dto.CategoryFriendDTO;
import com.luck.chat.pojo.dto.CategoryModifyDTO;
import com.luck.chat.pojo.entity.Result;
import com.luck.chat.pojo.vo.CategoryAddVO;
import com.luck.chat.pojo.vo.CategoryListVO;
import com.luck.chat.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {


    @Autowired
    private CategoryService categoryService;

    /**
     * 新建一个分类
     * @return
     */
    @PostMapping("/add")
    public Result<CategoryAddVO> addCategory(@RequestBody CategoryModifyDTO categoryModifyDTO) throws JsonProcessingException {
        String categoryName=categoryModifyDTO.getCategoryName();
        log.info("当前用户新建分类名:{}",categoryName);
        if(categoryService.contains(categoryName)){
            return Result.error(CodeConstant.ALREADY_EXIST_CATEGORY);
        }
        return Result.success(categoryService.save(categoryName));

    }

    /**
     * 获取当前用户所有分类
     * @return
     */
    @GetMapping("/list")
    public Result<List<CategoryListVO>> listCategory(){
        List<CategoryListVO> list =categoryService.list(BaseContext.getCurrentId());
        log.info("用户id {},的所有分类信息:{}",BaseContext.getCurrentId(),list);

        if(list==null||list.isEmpty()){
            return Result.error(CodeConstant.NULL_CATEGORY);
        }
        return Result.success(list);
    }


    /**
     * 修改分类名称
     * @param categoryModifyDTO
     * @return
     */
    @PutMapping("/name")
    public Result modifyCategoryName(@RequestBody CategoryModifyDTO categoryModifyDTO){
        log.info("修改分类名称:{}",categoryModifyDTO);
        categoryService.update(categoryModifyDTO);

        return Result.success();
    }

    /**
     * 修改部分好友的分类
     * @param categoryFriendDTO
     * @return
     */
    @PutMapping("/friend")
    public Result modifyFriendCategory(@RequestBody CategoryFriendDTO categoryFriendDTO){
        log.info("修改一些好友的分类");
        categoryService.modifyFriendCategory(categoryFriendDTO);
        return  Result.success();
    }

}
