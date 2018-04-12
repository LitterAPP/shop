package modules.shop.service;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jws.Logger;
import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import modules.shop.ddl.UsersDDL;
import modules.shop.ddl.ShopOrderDDL;
import modules.shop.ddl.ShopProductDDL;
import modules.shop.ddl.ShopTogetherDDL;
import modules.shop.ddl.ShopTogetherJoinerDDL;
import util.API;

public class ShopTogetherService {

	// 1=拼团中 2=拼团完成 3=过期
	public static final int TOGETHER_ING=1;
	public static final int TOGETHER_DONE=2;
	public static final int TOGETHER_EXPIR=3;
	
	
	public static void JoinTogether(ShopOrderDDL order){
		if(order == null || order.getStatus() != ShopOrderService.ORDER_PAYED_TOGETHER){
			Logger.debug("JoinTogether order %s",new Gson().toJson(order));
			return ;
		}
		ShopProductDDL product = ShopProductService.getByProductId(order.getProductId());
		ShopTogetherDDL sttd = getShopTogether(order.getTogetherId());
		boolean isMaster = false;
		if(sttd == null){
			isMaster = true;
			createTogether(order,product);
		}else{
			updateTogetherNumber(sttd);
		} 
		createTogetherJoiner(order.getBuyerUserId(),order.getTogetherId(),isMaster,product.getProductId(),order.getOrderId());
	}
	
	public static ShopTogetherDDL getShopTogether(String togetherId){
		Condition condition = new Condition("ShopTogetherDDL.togetherId","=",togetherId);
		List<ShopTogetherDDL> list = Dal.select("ShopTogetherDDL.*", condition, null, 0, 1);
		if(list == null || list.size() == 0) return null;
		return list.get(0);
	}
	
	public static boolean createTogether(ShopOrderDDL order,ShopProductDDL product){
		ShopTogetherDDL sttd = new ShopTogetherDDL();
		sttd.setCreateTime(System.currentTimeMillis());
		sttd.setExpireTime(System.currentTimeMillis() + product.getTogetherExpirHour() * 60*60*1000);
		sttd.setProductId(product.getProductId());
		sttd.setProductName(product.getProductName());
		sttd.setTogetherId(order.getTogetherId());
		sttd.setTogetherNumber(product.getTogetherNumber());
		sttd.setTogetherNumberResidue(product.getTogetherNumber()-1);
		UsersDDL user = UserService.get(order.getBuyerUserId());
		sttd.setMasterAvatar(user.getAvatarUrl());
		sttd.setMasterName(user.getNickName());
		sttd.setStatus(TOGETHER_ING);
		sttd.setProductType(product.getProductType());
		return Dal.insert(sttd) > 0;
	}
	
	public static boolean updateTogetherNumber(ShopTogetherDDL together){
		if(together==null){
			return false;
		}
		Condition condition = new Condition("ShopTogetherDDL.togetherId","=",together.getTogetherId());
		together.setTogetherNumberResidue(together.getTogetherNumberResidue()-1);
		if(together.getTogetherNumberResidue()<=0){
			
			together.setStatus(ShopTogetherService.TOGETHER_DONE);
			together.setTogetherNumberResidue(0);
			
			if(together.getProductType() == null || together.getProductType() == ShopProductService.Type.PRODUCT_TYPE_ENTITY.getValue()){
				ShopOrderService.doneTogether(together);
			}else if(together.getProductType() == ShopProductService.Type.PRODUCT_TYPE_PRIZE.getValue()){
				ShopOrderService.doneTogetherForPrize(together);
			}
		} 
		return Dal.update(together, "ShopTogetherDDL.togetherNumberResidue,ShopTogetherDDL.status", condition)>0;
	}
	
	public static boolean expireTogether(ShopTogetherDDL together){
		if(together==null){
			return false;
		}
		Condition condition = new Condition("ShopTogetherDDL.togetherId","=",together.getTogetherId());
		together.setStatus(TOGETHER_EXPIR);
		return Dal.update(together, "ShopTogetherDDL.status", condition)>0;
	}
	
	public static boolean createTogetherJoiner(int userId,String togetherId,boolean isMaster,String productId,String orderId){
		ShopTogetherJoinerDDL stj = new ShopTogetherJoinerDDL();
		stj.setProductId(productId);
		stj.setIsMaster(isMaster?1:0);
		stj.setJoinTime(System.currentTimeMillis());
		stj.setTogetherId(togetherId);
		UsersDDL user = UserService.get(userId);
		stj.setUserId(userId);
		stj.setUserAvatar(user.getAvatarUrl());
		stj.setUserName(user.getNickName());
		stj.setOrderId(orderId);
		return Dal.insert(stj)>0;
	}
	
	public static List<ShopTogetherDDL> listByProductId(String productId,int page,int pageSize){
		page = page==0?1:page;
		pageSize = pageSize==0?5:pageSize;
		Condition condition = new Condition("ShopTogetherDDL.productId","=",productId);
		return Dal.select("ShopTogetherDDL.*", condition, null, (page-1)*pageSize, pageSize);
	}
	
	public static int countByProductId(String productId){
		Condition condition = new Condition("ShopTogetherDDL.productId","=",productId);
		return Dal.count(condition);
	}
	
	
	public static List<ShopTogetherDDL> listCanJoinByProductId(String productId,int page,int pageSize){
		page = page==0?1:page;
		pageSize = pageSize==0?5:pageSize;
		Condition condition = new Condition("ShopTogetherDDL.productId","=",productId);
		condition.add( new Condition("ShopTogetherDDL.expireTime",">",System.currentTimeMillis()+30*60*100), "and");
		condition.add( new Condition("ShopTogetherDDL.status","=",TOGETHER_ING), "and");
		return Dal.select("ShopTogetherDDL.*", condition, null, (page-1)*pageSize, pageSize);
	} 
	
	public static List<ShopTogetherJoinerDDL> listJoinerByTogetherId(String togetherId){ 
		if(StringUtils.isEmpty(togetherId))return null;
		Condition condition = new Condition("ShopTogetherJoinerDDL.togetherId","=",togetherId); 
		return Dal.select("ShopTogetherJoinerDDL.*", condition, null, 0, -1);
	} 
	
	
	public static ShopTogetherDDL listByTogetherId(String togetherId){ 
		Condition condition = new Condition("ShopTogetherDDL.togetherId","=",togetherId);
		List<ShopTogetherDDL> list =  Dal.select("ShopTogetherDDL.*", condition, null, 0, 1);
		if(list==null || list.size()==0)
			return null;
		else 
			return list.get(0);
	}
	
	public static List<ShopTogetherDDL> listUnDone(){ 
		Condition condition = new Condition("ShopTogetherDDL.status","!=",TOGETHER_DONE);
		condition.add(new Condition("ShopTogetherDDL.expireTime","<=",System.currentTimeMillis()) , "and");
		List<ShopTogetherDDL> list =  Dal.select("ShopTogetherDDL.*", condition, null, 0, -1);
		return list;
	}
	
	
}
