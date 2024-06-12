package com.luck.chat.config;

import com.luck.chat.properties.AliSmsProperties;
import com.luck.chat.utils.AliSmsUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmsConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AliSmsUtils aliSmsUtils(AliSmsProperties aliSmsProperties){
        return new AliSmsUtils(
                aliSmsProperties.getProduct(),
                aliSmsProperties.getDomain(),
                aliSmsProperties.getAccessKeyId(),
                aliSmsProperties.getAccessKeySecret(),
                aliSmsProperties.getSignName(),
                aliSmsProperties.getTemplateCode());
    }
}
