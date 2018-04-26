package modules.shop.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jws.Jws;
import jws.Logger;
import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import jws.dal.sqlbuilder.Sort;
import modules.shop.ddl.ShopApplyInfoDDL;
import modules.shop.ddl.ShopOrderDDL;
import modules.shop.ddl.ShopProductDDL;
import modules.shop.ddl.ShopProductGroupDDL;
import modules.shop.ddl.ShopTogetherDDL;
import modules.shop.ddl.ShopTogetherJoinerDDL;
import modules.shop.ddl.UsersDDL;
import util.API;
import util.AmountUtil;
import util.IDUtil;

public class ShopOrderService {
	//0=支付中 1=支付成功 2=支付取消 3=支付失败
	public static final int ORDER_PAYING = 0;//支付中
	public static final int ORDER_PAYED = 1;//支付成功 待发货
	public static final int ORDER_PAYED_TOGETHER = 4;//支付完成拼团中
	public static final int ORDER_PAY_CANCEL = 2;//支付取消
	public static final int ORDER_PAY_FAIL = 3;//支付失败
	public static final int ORDER_REFUND = 5;//退款成功
	public static final int ORDER_PAYED_TOGETHER_1 = 6;//拼团成功，待发货/抽奖
	public static final int ORDER_DELIVERED = 7;//已投递  具有快递编号了
	public static final int ORDER_SIGNED = 8;//已签收，
	public static final int ORDER_DONE = 9;//用户已收货
	public static final int ORDER_LUCKY_PAYED = 10;//	
	public static final int ORDER_LUCKY_MAN= 11;//已支付抽奖完成，中奖订单
	public static final int ORDER_UNLUCKY_MAN= 12;//已支付抽奖完成，未中奖订单
	public static final int ORDER_TOGETHER_NOT_FULL= 14;//过期未成团，退款
	
	public static boolean createOrder(boolean together,String togetherId,
			String userAccountId,String couponAccountId,
			int useUserAccount,int useCouponAccount,int useCash,
			int buyNum,String orderId,int buyerUserId,String litterAppParams,
			String address,ShopProductDDL product,ShopProductGroupDDL group) throws Exception{
		 
		ShopOrderDDL order = new ShopOrderDDL();
		//生成拼团Id
		if(together){
			if(StringUtils.isEmpty(togetherId)){
				order.setTogetherId(IDUtil.gen("TOG"));
			}else{
				ShopTogetherDDL togetherInfo = ShopTogetherService.getShopTogether(togetherId);
				if(togetherInfo!=null && togetherInfo.getStatus() == ShopTogetherService.TOGETHER_ING ){
					order.setTogetherId(togetherId);
				}else{
					throw new Exception("团已结束");
				}
			}
			
		}		
		order.setShopId(product.getShopId());
		order.setSellerTelNumber(product.getSellerTelNumber());
		order.setSellerWxNumber(product.getSellerWxNumber());
		order.setGroupId(group.getGroupId());
		order.setGroupName(group.getGroupName());
		order.setBuyerUserId(buyerUserId);
		order.setExpireTime(System.currentTimeMillis()+2*60*60*1000);
		order.setOrderId(orderId);
		order.setLitterAppParams(litterAppParams);
		order.setOrderTime(System.currentTimeMillis());
		order.setProductId(product.getProductId());
		order.setProductName(product.getProductName());
		order.setProductNowAmount(product.getProductNowAmount());
		order.setProductOriginAmount(product.getProductOriginAmount());
		order.setSellerUserId(product.getSellerUserId());
		order.setStatus(ORDER_PAYING); 
		order.setAddress(address);
		order.setProductTogetherAmount(product.getProductTogetherAmount());
		order.setGroupPrice(group.getGroupPrice());
		order.setGroupTogetherPrice(group.getGroupTogetherPrice());
		order.setGroupImg(group.getGroupImage());
		order.setProductType(product.getProductType()==null?
				ShopProductService.Type.PRODUCT_TYPE_ENTITY.getValue():product.getProductType());
		if(useUserAccount>0){
			if(!UserAccountService.reduceBalance(userAccountId, useUserAccount)){
				if(Dal.insert(order)>0){
					payFail(order);
					return false;
				}
			}
		}
		
		if(useCouponAccount>0){
			if(!UserAccountService.reduceBalance(couponAccountId, useCouponAccount)){
				if(Dal.insert(order)>0){
					payFail(order);
					return false;
				}
			}	
		}
		
		order.setUseCash(useCash);
		order.setUseCouponAmount(useCouponAccount);
		order.setUseCouponAccountId(couponAccountId);
		order.setUseUserAccountId(userAccountId);
		order.setUseUserAmount(useUserAccount);
		order.setBuyNum(buyNum);
		//以签约的收费比例为准
		int sellerUid = product.getSellerUserId();
		ShopApplyInfoDDL applyInfo = ApplyService.getApplyInfo(sellerUid);
		order.setPlatformGetsRate(applyInfo==null?0:applyInfo.getFeeRate());
		
		order.setUseCash(useCash);
		
		if(useCash == 0){
			order.setStatus(ORDER_PAYED);
			order.setPayTime(System.currentTimeMillis());
			if(Dal.insert(order)>0){
				paySuccess(order);
				return true;
			}
		}
		
		return Dal.insert(order)>0;
	}
	
	public static ShopOrderDDL findByOrderId(String orderId){
		Condition condition = new Condition("ShopOrderDDL.orderId","=",orderId);
		List<ShopOrderDDL> list = Dal.select("ShopOrderDDL.*", condition, null, 0, 1);
		if(list==null || list.size()==0)return null;
		return list.get(0);
	}
	
	public static void paySuccess(ShopOrderDDL order){
		if(order==null) return ;
		order.setStatus(ORDER_PAYED);
		order.setPayTime(System.currentTimeMillis());
		//平台收入取整
		int platformGets = order.getPlatformGetsRate() * (order.getUseCash()+order.getUseCouponAmount() + order.getUseUserAmount()) / 100;
		order.setPlatformGets(platformGets);
		order.setSellerGets(order.getUseCash()+order.getUseCouponAmount() + order.getUseUserAmount()-platformGets );
		
		if( !StringUtils.isEmpty(order.getTogetherId()) ){
			order.setStatus(ORDER_PAYED_TOGETHER);
			if(order.getProductType() == ShopProductService.Type.PRODUCT_TYPE_PRIZE.getValue()){
				order.setStatus(ORDER_LUCKY_PAYED);
			}
			
			Dal.update(order, "ShopOrderDDL.status,ShopOrderDDL.platformGets,"
					+ "ShopOrderDDL.sellerGets,ShopOrderDDL.payTime,"
					+ "ShopOrderDDL.transactionId,ShopOrderDDL.notifyBody", new Condition("ShopOrderDDL.orderId","=",order.getOrderId()));
			//创建一个团	
			ShopTogetherService.JoinTogether(order);
		}else{
			//实物发货，进入物流表
			if(order.getProductType() == ShopProductService.Type.PRODUCT_TYPE_ENTITY.getValue()){
				ShopExpressService.initExpress(order.getOrderId());
			}else{//订单直接完成
				order.setStatus(ShopOrderService.ORDER_DONE);
			}
			Dal.update(order, "ShopOrderDDL.status,ShopOrderDDL.platformGets,"
					+ "ShopOrderDDL.sellerGets,ShopOrderDDL.payTime,"
					+ "ShopOrderDDL.transactionId,ShopOrderDDL.notifyBody", new Condition("ShopOrderDDL.orderId","=",order.getOrderId()));
			
		}
		//触发订单支付成功微信服务通知
		if(!StringUtils.isEmpty(order.getLitterAppParams())){
			UsersDDL buyer = UserService.get(order.getBuyerUserId());
			 
			JsonObject parsms = new JsonParser().parse(order.getLitterAppParams()).getAsJsonObject();
			String packagestr = parsms.get("package").getAsString();
			String page="pages/shop/orderdetail?orderId="+order.getOrderId();
			Map<String,Map> dataMap = new HashMap<String,Map>();
			Map<String,String> k1 = new HashMap<String,String>();
			k1.put("value",order.getOrderId());
			k1.put("color", "#000033");
			
			Map<String,String> k2 = new HashMap<String,String>();
			int amount = (order.getUseCash()==null?0:order.getUseCash())+(order.getUseCouponAmount()==null?0:order.getUseCouponAmount())+(order.getUseUserAmount()==null?0:order.getUseUserAmount());
			k2.put("value",AmountUtil.f2y(amount)+"元");
			k2.put("color", "#000033"); 
			
			Map<String,String> k3 = new HashMap<String,String>();
			k3.put("value", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			k3.put("color", "#3300cc");
			
			
			Map<String,String> k4 = new HashMap<String,String>();
			k4.put("value", order.getProductName());
			k4.put("color", "#3300cc");
			
			
			Map<String,String> k5 = new HashMap<String,String>();
			k5.put("value", "如有疑问，请联系微信号：xiaoyu022994");
			k5.put("color", "#3300cc");
			
			dataMap.put("keyword1", k1);
			dataMap.put("keyword2", k2);
			dataMap.put("keyword3", k3); 
			dataMap.put("keyword4", k4); 
			dataMap.put("keyword5", k5); 
			
			API.sendWxMessage(parsms.get("appId").getAsString(), 
					buyer.getOpenId(), Jws.configuration.getProperty("wx.msg.template.id.wczf"), 
					page,packagestr.split("=")[1] , dataMap);
		} 
		
	}
	
	public static void payFail(ShopOrderDDL order){
		if(order==null) return ;
		order.setStatus(ORDER_PAY_FAIL);		
		Dal.update(order, "ShopOrderDDL.status,ShopOrderDDL.payTime,"
				+ "ShopOrderDDL.transactionId,ShopOrderDDL.notifyBody", new Condition("ShopOrderDDL.orderId","=",order.getOrderId()));
	}
	
	//支付取消，如果订单使用了账户余额，则需要回滚余额操作
	public static void payCanel(ShopOrderDDL order){
		if(order==null) return ;
		order.setStatus(ORDER_PAY_CANCEL); 	
		if(order.getUseUserAmount()>0){
			UserAccountService.backBalance(order.getUseUserAccountId(), order.getUseUserAmount());
		}
		if(order.getUseCouponAmount()>0){
			UserAccountService.backBalance(order.getUseCouponAccountId(), order.getUseCouponAmount());
		}
		Dal.update(order, "ShopOrderDDL.status", new Condition("ShopOrderDDL.orderId","=",order.getOrderId()));
	} 
	
	public static void doneTogetherForPrize(ShopTogetherDDL together){
		//完成抽奖逻辑
		List<ShopTogetherJoinerDDL> joiners = ShopTogetherService.listJoinerByTogetherId(together.getTogetherId());
	}
	/**
	 * 完成实物团购微信通知
	 * @param together
	 */
	public static void doneTogether(ShopTogetherDDL together){
		//给所有人发送平台成功消息 			
		Map<String,Map> dataMap = new HashMap<String,Map>();
		Map<String,String> k1 = new HashMap<String,String>();
			k1.put("value","[拼团成功]"+together.getProductName());
		k1.put("color", "#000033"); 
		
		Map<String,String> k2 = new HashMap<String,String>();
		k2.put("value", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(together.getCreateTime()));
		k2.put("color", "#3300cc");
		
		StringBuffer jonerStr = new StringBuffer();
		List<ShopTogetherJoinerDDL> joners = ShopTogetherService.listJoinerByTogetherId(together.getTogetherId());
		if(joners!=null && joners.size()>0){
			for(ShopTogetherJoinerDDL joiner:joners){
				jonerStr.append(joiner.getUserName()).append(",");
			}
		} 
		Map<String,String> k3 = new HashMap<String,String>();
		k3.put("value", jonerStr.substring(0, jonerStr.lastIndexOf(",")));
		k3.put("color", "#3300cc");
		
		
		Map<String,String> k4 = new HashMap<String,String>();
		k4.put("value", "如果未按承诺时间发货，系统将按照规则进行退款");
		k4.put("color", "#3300cc");
		
		dataMap.put("keyword1", k1);
		dataMap.put("keyword2", k2);
		dataMap.put("keyword3", k3); 
		dataMap.put("keyword4", k4);
		
		if(joners!=null && joners.size()>0){
			for(ShopTogetherJoinerDDL joner:joners){
				ShopOrderDDL order = ShopOrderService.findByOrderId(joner.getOrderId());
				if(order==null || StringUtils.isEmpty(order.getLitterAppParams()))continue;
				JsonObject params = new JsonParser().parse(order.getLitterAppParams()).getAsJsonObject();
				String packagestr = params.get("package").getAsString();
				UsersDDL user = UserService.get(joner.getUserId());
				if(user==null)continue;
				Logger.info("begin send wx message,%s,packagestr=%s", params,packagestr);
				API.sendWxMessage(
						params.get("appId").getAsString(), 
						user.getOpenId(), 
						Jws.configuration.getProperty("wx.msg.template.id.wcpt"),
						"pages/shop/orderdetail?orderId="+joner.getOrderId(),
						packagestr.split("=")[1], dataMap);
			}
		}
		
		
		Condition condition = new Condition("ShopOrderDDL.togetherId","=",together.getTogetherId());
		List<ShopOrderDDL> list = Dal.select("ShopOrderDDL.*", condition, null, 0, -1);
		if(list == null || list.size() ==0 ) return ;
		for(ShopOrderDDL order : list){
			order.setStatus(ORDER_PAYED_TOGETHER_1); 			
			if(order.getProductType() == ShopProductService.Type.PRODUCT_TYPE_ENTITY.getValue()){
				ShopExpressService.initExpress(order.getOrderId());
			}else{//订单直接完成
				order.setStatus(ShopOrderService.ORDER_DONE);
			} 
			Dal.update(order, "ShopOrderDDL.status", new Condition("ShopOrderDDL.orderId","=",order.getOrderId()));
		}
	}
	/**
	 * 我的订单
	 * @param buyerUserId
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public static List<ShopOrderDDL> listOrder(int userId,int status,boolean imSeller,int page,int pageSize){
		
		Condition condition = null;
		if(imSeller){
			condition = new Condition("ShopOrderDDL.sellerUserId","=",userId);
		}else{
			condition = new Condition("ShopOrderDDL.buyerUserId","=",userId);
		}
		if(status!=-1){
			if(status == 1){
				List<Integer> statusList = new ArrayList<Integer>();
				statusList.add(ShopOrderService.ORDER_PAYED);
				statusList.add(ShopOrderService.ORDER_PAYED_TOGETHER_1);
				condition.add(new Condition("ShopOrderDDL.status","in",statusList), "and");
			}else{
				condition.add(new Condition("ShopOrderDDL.status","=",status), "and");
			}
			
		}
		Sort sort = new Sort("ShopOrderDDL.id",false);
		return  Dal.select("ShopOrderDDL.*", condition, sort, (page-1)*pageSize, pageSize);
	}
	
	public static List<ShopOrderDDL> listMngOrder(String shopId,String orderId,String keyword,int status,int page,int pageSize){
		Condition condition = new Condition("ShopOrderDDL.id",">",0);
		if(!StringUtils.isEmpty(orderId)){
			condition.add(new Condition("ShopOrderDDL.orderId","=",orderId), "and");			 
		}
		if(!StringUtils.isEmpty(shopId)){
			condition.add(new Condition("ShopOrderDDL.shopId","=",shopId), "and");			 
		}
		if(!StringUtils.isEmpty(keyword)){
			condition.add(new Condition("ShopOrderDDL.productName","like","%"+keyword+"%"), "and");
		}
		
		if(status!=-1){
			condition.add(new Condition("ShopOrderDDL.status","=",status), "and");
		}
		
		Sort sort = new Sort("ShopOrderDDL.id",false);
		
		return  Dal.select("ShopOrderDDL.*", condition, sort, (page-1)*pageSize, pageSize);
	}
	
	public static int countMngOrder(String shopId,String orderId,String keyword,int status){
		Condition condition = new Condition("ShopOrderDDL.id",">",0);
		if(!StringUtils.isEmpty(orderId)){
			condition.add(new Condition("ShopOrderDDL.orderId","=",orderId), "and");			 
		}
		if(!StringUtils.isEmpty(shopId)){
			condition.add(new Condition("ShopOrderDDL.shopId","=",shopId), "and");			 
		}
		
		if(!StringUtils.isEmpty(keyword)){
			condition.add(new Condition("ShopOrderDDL.productName","like","%"+keyword+"%"), "and");
		}
		
		if(status!=-1){
			condition.add(new Condition("ShopOrderDDL.status","=",status), "and");
		}	
 		
		return  Dal.count(condition);
	}
	 
}
