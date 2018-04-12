package controllers;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jws.Jws;
import jws.Logger;
import jws.http.Request;
import jws.http.Response;
import jws.http.sf.HTTP;
import jws.mvc.Controller;
import modules.shop.ddl.UsersDDL;
import modules.shop.service.UserAccountService;
import modules.shop.service.UserService;
import util.AES;
import util.RtnUtil;
import util.WXDecriptUtil;

public class Login extends Controller{

	public static void loginByWeixin(String session,String code,String rawData,String encryptedData,
			String signature,String iv,String appid){ 
		
		if(StringUtils.isEmpty(code) || StringUtils.isEmpty(rawData) || StringUtils.isEmpty(iv)
				|| StringUtils.isEmpty(encryptedData)|| StringUtils.isEmpty(signature) || StringUtils.isEmpty(appid)){
			renderJSON(RtnUtil.returnFail("非法参数"));
		}
		
		try{
 			String queryString = String.format("?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
 					appid,
 					Jws.configuration.getProperty(appid+".secret"),
 					code);
			Request request = new Request("wx","session",queryString);
			Response response = HTTP.GET(request);
			if(response.getStatusCode()!=200){
				renderJSON(RtnUtil.returnFail("请求微信获取sessionKey失败，http状态错误"));
			} 
			Logger.info("response form wx %s", response.getContent());
			JsonObject obj = new JsonParser().parse(response.getContent()).getAsJsonObject();
			String sessionKey = obj.get("session_key").getAsString();
			String signStr = rawData+sessionKey;
			String mySignature =WXDecriptUtil.SHA1(signStr);
			if(!mySignature.equals(signature)){
				Logger.error("SHA1 签名串 %s,mySignature %s ,youSignature %s",signStr, mySignature,signature);
				renderJSON(RtnUtil.returnFail("请求微信获取sessionKey失败，数据不完整"));
			}
			byte[] encryptedDataBase64Decoder = Base64.decodeBase64(encryptedData);
			byte[] sessionKeyBase64Decoder = Base64.decodeBase64(sessionKey);
			byte[] ivBase64Decoder = Base64.decodeBase64(iv);
			AES aes = new AES();
			byte[] aseBytes = aes.decrypt(encryptedDataBase64Decoder, sessionKeyBase64Decoder, ivBase64Decoder);
			String decryptedData = new String(WXDecriptUtil.decode(aseBytes));
			Logger.info("after ecrypt4Base64 %s",decryptedData);
			JsonObject userDataJson = new JsonParser().parse(decryptedData).getAsJsonObject();
			
			String avatarUrl = userDataJson.get("avatarUrl").getAsString();
			int gender = userDataJson.get("gender").getAsInt();
			String nickName = userDataJson.get("nickName").getAsString();
			String city = userDataJson.get("city").getAsString();
			String province = userDataJson.get("province").getAsString();
			String country = userDataJson.get("country").getAsString();
			String openId =  userDataJson.get("openId").getAsString();
			
			UsersDDL userInfo = UserService.updateUser(session, avatarUrl, city, country, 
					gender, province, nickName,openId);
			
			UserAccountService.createBasicAccount(userInfo.getId().intValue());
			
			renderJSON(RtnUtil.returnSuccess("ok",userInfo));
		}catch(Exception  e){
			Logger.error(e, "");
			renderJSON(RtnUtil.returnFail("请求微信获取sessionKey失败"));
		} 
	}
}
