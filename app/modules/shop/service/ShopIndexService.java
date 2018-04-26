package modules.shop.service;

import java.util.List;

import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import modules.shop.ddl.ShopIndexDDL;

public class ShopIndexService {

	public static ShopIndexDDL get(int id){
		return Dal.select("ShopIndexDDL.*", id);
	}
	
	public static ShopIndexDDL getByShopId(String shopId){
		 Condition cond = new Condition("ShopIndexDDL.shopId","=",shopId);
		 List<ShopIndexDDL> list = Dal.select("ShopIndexDDL.*", cond, null, 0, 1);
		 if(list==null || list.size()==0) return null;
		 return list.get(0);
	}
	
	public static boolean update(String shopId,String name,String avatar,String config){
		ShopIndexDDL shop = getByShopId(shopId);
		
		//根据shopID
		
		if(shop==null){
			ShopIndexDDL s = new ShopIndexDDL();
			s.setShopId(shopId);
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
			return Dal.update(shop, "ShopIndexDDL.name,ShopIndexDDL.avatar,ShopIndexDDL.config", 
					new Condition("ShopIndexDDL.id","=",shop.getId()))>0;
		}
	}
}
