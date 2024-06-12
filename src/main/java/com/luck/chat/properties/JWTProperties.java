package com.luck.chat.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "chat.jwt")
public class JWTProperties {
    /**
     * jwt令牌相关配置
     */
    private String secretKey;
    private Long expiration;
    private String tokenName;
}
