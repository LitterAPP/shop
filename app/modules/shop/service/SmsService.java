package modules.shop.service;

import java.util.ArrayList;
import java.util.List;

import jws.Jws;
import jws.Logger;
import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import modules.shop.ddl.SmsCodeHistoryDDL;
import util.QQSMSUtil;

public class SmsService {
	
	public static int sendAuthCode(String mobile){
        int code=(int)((Math.random()*9+1)*1000);//为变量赋随机值1000-9999
		List<String> params = new ArrayList<String>();
		params.add(""+code);
		params.add("15");//15分钟有效
		try {
			//QQSMSUtil.sendWithParam("86",order.getSellerTelNumber(),70567,null,"青乐科技",null,null);
			boolean result =QQSMSUtil.sendWithParam("86",mobile,
					Integer.parseInt(String.valueOf(Jws.configuration.get("tencent.sms.tmp_authcode.id"))),
					params,
					String.valueOf(Jws.configuration.get("tencent.sms.tmp_signname")),
					null,null);
			if(!result){
				return 0;
			}
			SmsCodeHistoryDDL smsDDL = new SmsCodeHistoryDDL();
			smsDDL.setCode(code);
			smsDDL.setCreateTime(System.currentTimeMillis());
			smsDDL.setExpireTime(System.currentTimeMillis()+15*60*1000);
			smsDDL.setMobile(mobile);
			smsDDL.setStatus(0);
			if(Dal.insert(smsDDL)>0){
				return code;
			}
			return 0;
		} catch (Exception e) {
			Logger.error(e, e.getMessage());
		}
		return 0;
	}
	
	public static boolean validateSmsCode(String mobile,int code){
		Condition cond = new Condition("SmsCodeHistoryDDL.mobile","=",mobile);
		cond.add(new Condition("SmsCodeHistoryDDL.code","=",code), "and");
		cond.add(new Condition("SmsCodeHistoryDDL.status","=",0), "and");
		cond.add(new Condition("SmsCodeHistoryDDL.expireTime",">=",System.currentTimeMillis()), "and");
		List<SmsCodeHistoryDDL> list = Dal.select("SmsCodeHistoryDDL.id", cond, null, 0, 1);
		if(list==null || list.size()==0) return false;
		updateStatus(list.get(0));
		return true;
	}
	
	public static void updateStatus(SmsCodeHistoryDDL sms){
		if( sms == null) return ;
		sms.setStatus(1);
		Dal.update(sms, "SmsCodeHistoryDDL.status",new Condition("SmsCodeHistoryDDL.id","=",sms.getId()));
	}
}
