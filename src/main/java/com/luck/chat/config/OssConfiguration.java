package com.luck.chat.config;


import com.luck.chat.properties.AliOssProperties;
import com.luck.chat.utils.AliOssUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OssConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AliOssUtils aliOssUtils(AliOssProperties aliOssProperties){
        return new AliOssUtils(aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName(),
                aliOssProperties.getRoleArn(),
                aliOssProperties.getRoleSessionName(),
                aliOssProperties.getExpireTime(),
                aliOssProperties.getPolicy()
                );
    }
}
