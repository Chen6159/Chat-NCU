package com.luck.chat.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.auth.sts.AssumeRoleRequest;
import com.aliyuncs.auth.sts.AssumeRoleResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;

/**
 * 获取STS的临时访问凭证
 */
@Data
@AllArgsConstructor
@Slf4j
public class AliOssUtils {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private String roleArn;
    private String roleSessionName;
    private Long expireTime;
    private String policy;

    /**
     * 生成sts的临时访问凭证
     * @return
     * @throws ClientException
     */
    public AssumeRoleResponse.Credentials generateStsToken() throws ClientException {
        try {
            String regionId = "cn-nanjing";
            // 添加endpoint。适用于Java SDK 3.12.0及以上版本。
            DefaultProfile.addEndpoint(regionId, "Sts", endpoint);

            // 构造default profile。
            IClientProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
            // 构造client。
            DefaultAcsClient client = new DefaultAcsClient(profile);
            final AssumeRoleRequest request = new AssumeRoleRequest();
            // 适用于Java SDK 3.12.0及以上版本。
            request.setSysMethod(MethodType.POST);

            //设置RAM用户的arn
            request.setRoleArn(roleArn);
            //设置绘画名字(随意取)
            request.setRoleSessionName(roleSessionName);
            //设置权限策略
            request.setPolicy(policy);
            //设置有效时间
            request.setDurationSeconds(expireTime);
            final AssumeRoleResponse response = client.getAcsResponse(request);

            return response.getCredentials();
        } catch (ClientException e) {
            System.out.println("Failed：");
            System.out.println("Error code: " + e.getErrCode());
            System.out.println("Error message: " + e.getErrMsg());
            System.out.println("RequestId: " + e.getRequestId());
            return null;
        }

    }


}
