package modules.shop.service;

import java.util.List;
import java.util.UUID;

import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import modules.shop.ddl.ShopMngSessionDDL;
import modules.shop.ddl.ShopMngUserDDL;
import util.MD5Util;

public class ShopMngService {

	public static ShopMngSessionDDL checkSession(String session){
		Condition condition = new Condition("ShopMngSessionDDL.session","=",session);
		condition.add(new Condition("ShopMngSessionDDL.expireTime",">",System.currentTimeMillis()), "and");
		List<ShopMngSessionDDL> sessions = Dal.select("ShopMngSessionDDL.*", condition, null, 0, 1);
		if(sessions==null || sessions.size()==0) return null;
		return sessions.get(0);
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
