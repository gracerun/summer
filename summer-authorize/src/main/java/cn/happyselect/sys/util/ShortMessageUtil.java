package cn.happyselect.sys.util;

import com.gracerun.util.SpringProfilesUtil;
import lombok.extern.slf4j.Slf4j;

/*
pom.xml
<dependency>
  <groupId>com.aliyun</groupId>
  <artifactId>aliyun-java-sdk-core</artifactId>
  <version>4.5.3</version>
</dependency>
*/
@Slf4j
public class ShortMessageUtil {

    public static void sendMessage(String phone, String code) {
        if (SpringProfilesUtil.isDev()
                || SpringProfilesUtil.isTest()
                || SpringProfilesUtil.isGray()) {
            return;
        }
        return;

//        DefaultProfile profile = DefaultProfile.getProfile(SmsConstant.SMS_REGION_ID, SmsConstant.SMS_ACCESSKEY_ID, SmsConstant.SMS_ACCESSKEY_SECRET);
//        IAcsClient client = new DefaultAcsClient(profile);
//
//        CommonRequest request = new CommonRequest();
//        request.setSysMethod(MethodType.POST);
//        request.setSysDomain(SmsConstant.SMS_DOMAIN);
//        request.setSysVersion("2017-05-25");
//        request.setSysAction("SendSms");
//        request.putQueryParameter("RegionId", SmsConstant.SMS_REGION_ID);
//        request.putQueryParameter("PhoneNumbers", phone);
//        request.putQueryParameter("SignName", SmsConstant.SMS_SIGN_NAME);
//        request.putQueryParameter("TemplateCode", SmsConstant.SMS_TEMPLATE_CODE);
//        request.putQueryParameter("TemplateParam", "{\"code\":\"" + code + "\"}");
//        try {
//            CommonResponse response = client.getCommonResponse(request);
//            log.info("{}", response.getData());
//            //{"Message":"OK","RequestId":"4AC6B0AE-3DD2-4667-89BA-C0845B70248D","BizId":"882015197225379162^0","Code":"OK"}
//            String json = response.getData();
//            JSONObject jsonObject = JSONObject.parseObject(json);
//            if (SmsConstant.SMS_SUCCESS_CODE.equals(jsonObject.getString("Code"))) {
//                return ResponseBean.successBean();
//            } else {
//                throw new RuntimeException(jsonObject.getString("Message"));
//            }
//        } catch (ServerException e) {
//            throw new RuntimeException(e.getErrMsg());
//        } catch (ClientException e) {
//            throw new RuntimeException(e.getErrMsg());
//        }

    }

    public static void main(String[] args) {
        sendMessage("18518471028", "123456");
    }
}
