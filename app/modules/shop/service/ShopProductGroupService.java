package modules.shop.service;

import java.util.List;

import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import jws.dal.sqlbuilder.Sort;
import modules.shop.ddl.ShopProductGroupDDL;

public class ShopProductGroupService {

	public static ShopProductGroupDDL get(int id){
		return Dal.select("ShopProductGroupDDL.*", id);
	}
	public static ShopProductGroupDDL findByProductIdAndGroupId(String productId,String groupId){
		Condition condition = new Condition("ShopProductGroupDDL.groupId","=",groupId);
		condition.add(new Condition("ShopProductGroupDDL.productId","=",productId ),"and");
		List<ShopProductGroupDDL> list = Dal.select("ShopProductGroupDDL.*", condition, null, 0, 1);
		if( list == null || list.size() == 0 ) return null;
		return list.get(0);
	}
	
	
	public static List<ShopProductGroupDDL> findByProductId(String productId){
		Condition condition = new Condition("ShopProductGroupDDL.productId","=",productId);		  
		return Dal.select("ShopProductGroupDDL.*", condition, new Sort("ShopProductGroupDDL.orderBy",true), 0, -1);
	}
	
	public static boolean delGroupByProductId(String productId){
		Condition condition = new Condition("ShopProductGroupDDL.productId","=",productId);	
		return Dal.delete(condition) > 0;
	}
}
