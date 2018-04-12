package modules.shop.service;

import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import modules.shop.ddl.ShopIndexDDL;

public class ShopIndexService {

	public static ShopIndexDDL get(int id){
		return Dal.select("ShopIndexDDL.*", id);
	}
	
	public static boolean update(int id,String name,String avatar,String config){
		ShopIndexDDL shop = get(id);
		if(shop==null){
			ShopIndexDDL s = new ShopIndexDDL();
			s.setId(id);
			s.setName(name);
			s.setAvatar(avatar);
			s.setConfig(config);
			s.setFollow(0);
			s.setCreateTime(System.currentTimeMillis());
			return Dal.insert(s)>0;
		}else{
			shop.setName(name);
			shop.setAvatar(avatar);
			shop.setConfig(config);
			return Dal.update(shop, "ShopIndexDDL.name,ShopIndexDDL.avatar,ShopIndexDDL.config", new Condition("ShopIndexDDL.id","=",id))>0;
		}
	}
}
