package modules.common.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import modules.common.ddl.FormIdsDDL;

public class FormIdService {
	public static final int expire_days = 7 ; 
	public static void addFormId(String appId,String openId,int userId,String formId){
		 FormIdsDDL form = new FormIdsDDL();
		 form.setAppId(appId);
		 form.setFormId(formId);
		 form.setOpenId(openId);
		 form.setUserId(userId);
		 form.setUseStatus(0);
		 form.setCreateTime(System.currentTimeMillis());
		 form.setExpireTime(System.currentTimeMillis()+7*24*60*60*1000);
		 Dal.insert(form);
	 }
	
	public static List<FormIdsDDL> listDistinct(String appId){
		Condition condition = new Condition("FormIdsDDL.useStatus","=",0);
		condition.add(new Condition("FormIdsDDL.expireTime",">",System.currentTimeMillis()) , "and");
		condition.add(new Condition("FormIdsDDL.appId","=",appId) , "and");
		List<FormIdsDDL> list = Dal.select("FormIdsDDL.*", condition, null, 0, -1);
		if(list == null || list.size() == 0){
			return null;
		}
		List<FormIdsDDL>  result = new ArrayList<FormIdsDDL>();
		Map<String,String> tmp = new HashMap<String,String>();
		for(FormIdsDDL form : list){
			if(!tmp.containsKey(form.getOpenId())){
				result.add(form);
				tmp.put(form.getOpenId(), "");
			}
		}
		return result;
	}
	
	public static FormIdsDDL getOneForm(String appId,String openId){
		Condition condition = new Condition("FormIdsDDL.useStatus","=",0);
		condition.add(new Condition("FormIdsDDL.expireTime",">",System.currentTimeMillis()) , "and");
		condition.add(new Condition("FormIdsDDL.appId","=",appId) , "and");
		condition.add(new Condition("FormIdsDDL.openId","=",openId) , "and");
		List<FormIdsDDL> list = Dal.select("FormIdsDDL.*", condition, null, 0, 1);
		if(list == null || list.size() == 0){
			return null;
		}
		return list.get(0); 
	}
	
	public static void updateUsed(String appId,String openId,String formId){
		Condition condition = new Condition("FormIdsDDL.formId","=",formId); 
		condition.add(new Condition("FormIdsDDL.appId","=",appId) , "and");
		condition.add(new Condition("FormIdsDDL.openId","=",openId) , "and");
		
		List<FormIdsDDL> list = Dal.select("FormIdsDDL.*", condition, null, 0, 1);
		if(list == null || list.size() == 0){
			return ;
		}
		
		FormIdsDDL update = list.get(0);
		update.setUseStatus(1);
		Dal.update(update, "FormIdsDDL.useStatus", new Condition("FormIdsDDL.id","=",update.getId()));
	}
}
