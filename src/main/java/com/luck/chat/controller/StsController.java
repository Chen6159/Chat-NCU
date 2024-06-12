package com.luck.chat.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luck.chat.constant.CodeConstant;
import com.luck.chat.pojo.entity.Result;
import com.luck.chat.utils.AliOssUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.auth.sts.AssumeRoleRequest;
import com.aliyuncs.auth.sts.AssumeRoleResponse;

import java.io.File;

/**
 * 获取文件上传临时凭证STS
 */
@RestController
@Slf4j
public class StsController {

    @Autowired
    private AliOssUtils aliOssUtils;

    /**
     *  获取访问凭证
     * @return
     */
    @GetMapping("/sts")
    public Result<AssumeRoleResponse.Credentials> getStsToken() throws ClientException {
        log.info("正在获取访问凭证");
        Result<AssumeRoleResponse.Credentials> result=new Result<>();
        result.setData(aliOssUtils.generateStsToken());
        if(result.getData()==null){
            return Result.error(CodeConstant.FAIL_GET_STS);
        }
        result.setCode(CodeConstant.SUCCESS);
        return result;
    }


}
