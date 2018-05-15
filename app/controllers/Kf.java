package controllers;

import java.util.Arrays;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jws.Logger;
import jws.mvc.Controller;
import util.WXDecriptUtil;

public class Kf extends Controller {

	/**
	 * EncodingAESKey 0J7nEu6LDMJdUkAieAyqxb3H360pPj0yqUwXNcawmaj
	 * @param signature
	 * @param timestamp
	 * @param nonce
	 * @param echostr
	 */
	public static void validate(String signature,String timestamp,String nonce,String echostr){
		Logger.info("Kf.validate %s", request.method);
		if(request.method.equalsIgnoreCase("get")){
			String[] params = new String[]{"wenxiaoyu",timestamp,nonce};
			Arrays.sort(params);
			StringBuffer sb = new StringBuffer();
			for(String param:params){
				sb.append(param);
			}
			String mySignature = WXDecriptUtil.SHA1(sb.toString());
			
			if(mySignature.equals(signature)){
				renderText(echostr);
			}
			Logger.info("Kf消息验签名,我的签名%s,微信的签名%s,SHA1字符串：%s",mySignature,signature,sb.toString());
			renderText("fail");
		}
		
		String msgBody = request.params.allSimple().get("body");
		Logger.info("Kf收到微信客服消息内容:%s", msgBody);
		JsonObject obj = new JsonParser().parse(msgBody).getAsJsonObject();
		String FromUserName = obj.get("FromUserName").getAsString();
		
		
		String response = "<xml>"+
						     "<ToUserName><![CDATA["+FromUserName+"]]></ToUserName>"+
						     "<FromUserName><![CDATA[xiaoyu022994]]></FromUserName>"+
						     "<CreateTime>"+(System.currentTimeMillis()/1000)+"</CreateTime>"+
						     "<MsgType><![CDATA[transfer_customer_service]]></MsgType>"+
						  "</xml>";
		Logger.info("Kf转发微信消息内容:%s", response);
		renderText(response);
	}
}
