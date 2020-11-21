package com.wxx.gulimall.thirdparty.component;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author wangxin
 * @date 2020/11/21
 */
@Component
public class SmsComponent {

    /**
     * 短信签名
     */
    @Value("${aliyuncs.sms.signName}")
    private String signName;

    /**
     * 短信模板code
     */
    @Value("${aliyuncs.sms.templateCode}")
    private String templateCode;

    @Value("${aliyuncs.sms.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyuncs.sms.accessSecret}")
    private String accessSecret;

    /**
     * 发送短信验证码
     * @param phoneNumbers /
     * @param code /
     */
    public void send(String phoneNumbers, String code) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessSecret);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", phoneNumbers);
        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("TemplateCode", templateCode);
        request.putQueryParameter("TemplateParam", "{\"code\":\"" + code + "\"}");
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }
}
