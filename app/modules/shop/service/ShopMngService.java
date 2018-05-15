package modules.shop.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jws.Jws;
import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import modules.shop.ddl.ShopMngSessionDDL;
import modules.shop.ddl.ShopMngUserDDL;
import util.IDUtil;
import util.MD5Util;
import util.WxQRCodeUtil;

public class ShopMngService {

	public static ShopMngSessionDDL checkSession(String session){
		Condition condition = new Condition("ShopMngSessionDDL.session","=",session);
		condition.add(new Condition("ShopMngSessionDDL.expireTime",">",System.currentTimeMillis()), "and");
		List<ShopMngSessionDDL> sessions = Dal.select("ShopMngSessionDDL.*", condition, null, 0, 1);
		if(sessions==null || sessions.size()==0) return null;
		return sessions.get(0);
	}
	
	public static ShopMngUserDDL getByUserName(String userName){
		Condition condition = new Condition("ShopMngUserDDL.userName","=",userName); 
		List<ShopMngUserDDL> users = Dal.select("ShopMngUserDDL.*", condition, null, 0, 1);
		if(users==null || users.size()==0) return null;
		return users.get(0);
	}
	
	public static boolean createUser(String userName,String mobile,String password){
		ShopMngUserDDL user = new ShopMngUserDDL();
		user.setCreateTime(System.currentTimeMillis());
		user.setMobile(mobile);
		user.setUserName(userName);
		String md5Pwd = MD5Util.md5(password);		
		user.setPassword(md5Pwd);
		user.setShopId(IDUtil.gen("SP"));		
		return Dal.insert(user)>0;
	}
	
	public static ShopMngSessionDDL login(String userName,String password){
		Condition condition = new Condition("ShopMngUserDDL.userName","=",userName);
		String md5Pwd = MD5Util.md5(password);
		condition.add(new Condition("ShopMngUserDDL.password","=",md5Pwd), "and");
		List<ShopMngUserDDL> users = Dal.select("ShopMngUserDDL.*", condition, null, 0, 1);
		if(users==null || users.size()==0) return null;
		ShopMngUserDDL user = users.get(0);
		
		Condition cond = new Condition("ShopMngSessionDDL.userId","=",user.getId());
		List<ShopMngSessionDDL> sessions = Dal.select("ShopMngSessionDDL.*", cond, null, 0, 1);
		if(sessions==null || sessions.size()==0){
			ShopMngSessionDDL newsession = new ShopMngSessionDDL();
			newsession.setCreateTime(System.currentTimeMillis());
			newsession.setExpireTime(System.currentTimeMillis()+6*60*60*1000);//2小时失效
			newsession.setUserId(user.getId());
			newsession.setUserName(user.getUserName());
			newsession.setShopId(user.getShopId());
			//生成店铺二维码URL
			Map<String,String> scene = new HashMap<String,String>();
			scene.put("shopId", user.getShopId());
			String qrCodeKey=WxQRCodeUtil.genRQCode(
					Jws.configuration.getProperty("shop.appId"), 
					scene,
					"pages/shop/shopIndex",//pages/shop/shopIndex 上线小程序才有效
					1);
			newsession.setShopQrcodeKey(qrCodeKey);			
			
			newsession.setSession(UUID.randomUUID().toString().replace("-", ""));
			Dal.insert(newsession);
			return newsession;
		}else{
			ShopMngSessionDDL old = sessions.get(0);
			old.setExpireTime(System.currentTimeMillis()+6*60*60*1000);
			Dal.update(old, "ShopMngSessionDDL.expireTime", new Condition("ShopMngSessionDDL.id","=",old.getId()));
			return old;
		} 
	}
	
	public static void main(String[] args){
		System.out.println(MD5Util.md5("111111"));
	}
}
