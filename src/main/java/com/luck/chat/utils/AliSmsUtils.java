package com.luck.chat.utils;


import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import com.aliyun.tea.TeaException;
import com.aliyun.tea.TeaModel;
import com.aliyun.teautil.Common;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class AliSmsUtils {
    private String product;
    private String domain;
    private String accessKeyId;
    private String accessKeySecret;
    private String signName;
    private String templateCode;

    /**
     * 发送手机验证码功能
     * @param telephone
     * @param code
     * @throws Exception
     */
    public boolean sendSms(String telephone,String code) throws Exception {
        //创建客户端对象
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret)
                .setEndpoint(domain);
        com.aliyun.dysmsapi20170525.Client client=new com.aliyun.dysmsapi20170525.Client(config);

        //创建客户端请求
        com.aliyun.dysmsapi20170525.models.SendSmsRequest sendSmsRequest = new com.aliyun.dysmsapi20170525.models.SendSmsRequest()
                .setPhoneNumbers(telephone)
                .setSignName(signName)
                .setTemplateCode(templateCode)
                .setTemplateParam("{\"code\":\""+code+"\"}");
        try {
            //客户端对象发送请求
            SendSmsResponse sendSmsResponse=client.sendSmsWithOptions(sendSmsRequest, new com.aliyun.teautil.models.RuntimeOptions());
            log.info(Common.toJSONString(TeaModel.buildMap(sendSmsResponse)));
            if(sendSmsResponse.getBody().getCode().equals("OK")){
                return true;
            }
            return false;
        } catch (TeaException error) {
            // 错误 message
            System.out.println(error.getMessage());
            // 诊断地址
            System.out.println(error.getData().get("Recommend"));
            com.aliyun.teautil.Common.assertAsString(error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            System.out.println(error.getMessage());
            System.out.println(error.getData().get("Recommend"));
            com.aliyun.teautil.Common.assertAsString(error.message);
        }

        return false;
    }




}
