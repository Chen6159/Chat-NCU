package com.luck.chat.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "chat.alisms")
@Data
public class AliSmsProperties {

    private String product;
    private String domain;
    private String accessKeyId;
    private String accessKeySecret;
    private String signName;
    private String templateCode;
}
