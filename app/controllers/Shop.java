package controllers;

import java.io.IOException;
import java.io.StringWriter;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import controllers.dto.SelectSourceDto;
import dto.shop.ShopIndexDto;
import dto.shop.ShopNavDto;
import jws.Jws;
import jws.Logger;
import jws.cache.Cache;
import jws.mvc.Controller;
import modules.shop.ddl.UsersDDL;
import modules.shop.ddl.ShopCouponMngDDL;
import modules.shop.ddl.ShopExpressDDL;
import modules.shop.ddl.ShopIndexDDL;
import modules.shop.ddl.ShopMngSessionDDL;
import modules.shop.ddl.ShopOrderDDL;
import modules.shop.ddl.ShopProductAttrRelDDL;
import modules.shop.ddl.ShopProductCommunityRelDDL;
import modules.shop.ddl.ShopProductDDL;
import modules.shop.ddl.ShopProductGroupDDL;
import modules.shop.ddl.ShopProductImagesDDL;
import modules.shop.ddl.ShopTogetherDDL;
import modules.shop.ddl.ShopTogetherJoinerDDL;
import modules.shop.ddl.ShopWetaoCommentDDL;
import modules.shop.ddl.ShopWetaoDDL;
import modules.shop.ddl.UserAccountDDL;
import modules.shop.service.ShopCategoryService;
import modules.shop.service.ShopCommunityService;
import modules.shop.service.ShopCouponMngService;
import modules.shop.service.ShopExpressService;
import modules.shop.service.ShopIndexService;
import modules.shop.service.ShopMngService;
import modules.shop.service.ShopOrderService;
import modules.shop.service.ShopProductAttrService;
import modules.shop.service.ShopProductGroupService;
import modules.shop.service.ShopProductImageService;
import modules.shop.service.ShopProductService;
import modules.shop.service.ShopTogetherService;
import modules.shop.service.ShopWeTaoCommentService;
import modules.shop.service.ShopWeTaoService;
import modules.shop.service.UserAccountService;
import modules.shop.service.UserService;
import util.API;
import util.AmountUtil;
import util.DateUtil;
import util.IDUtil;
import util.MD5Util;
import util.RtnUtil;

public class Shop extends Controller{
	private static Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	public static void createOrder(String session,String productId,String groupId,String appid,int buyNum,
			String userAccountId,String couponAccountId,boolean together,String togetherId,String address){
		try{
			if(buyNum==0)buyNum=1;
			
			UsersDDL user = UserService.findBySession(session);
			if(user==null){
				renderJSON(RtnUtil.returnLoginFail());
			}
			
			if(StringUtils.isEmpty(address)){
				renderJSON(RtnUtil.returnFail("请填写收货地址"));
			}
			
			if(Cache.get("PAYING_USER_"+user.getId())!=null){
				renderJSON(RtnUtil.returnFail("操作频繁,请10秒后再试"));
			}
			Cache.set("PAYING_USER_"+user.getId(), "1", "10s");
			
			ShopProductDDL product = ShopProductService.getByProductId(productId);
			if(product == null || product.getStatus()!=1 || product.getStore()<=0 || product.getStore() < buyNum){
				renderJSON(RtnUtil.returnFail("商品已下架或库存不足"));
			}
			
			ShopProductGroupDDL productGroup =  ShopProductGroupService.findByProductIdAndGroupId(productId, groupId);
			if(productGroup == null){
				renderJSON(RtnUtil.returnFail("商品组已下架"));
			}
			
			if(together && product.getJoinTogether() != 1){
				renderJSON(RtnUtil.returnFail("商品不支持拼团"));
			} 
			//int balance = useBalance?user.getBalance():0;
			
			Map<String,Object> result = new HashMap<String,Object>();
			String out_trade_no = IDUtil.gen("QL"); 
			result.put("orderId", out_trade_no); 
			
			//总共需要支付
			int totalAmount = 0;
			
			if(together && product.getJoinTogether() == 1){
				totalAmount = productGroup.getGroupTogetherPrice()*buyNum;
			}else{
				totalAmount = productGroup.getGroupPrice()*buyNum;
			}
			
			//不需要任何支付
			if( 0 == totalAmount){
				result.put("needPay", false);
				result.put("useBalance", 0);
				boolean order = ShopOrderService.createOrder(false,null,null,null,0,0,0,
						buyNum,out_trade_no,user.getId().intValue(), null,address,product,productGroup);
				result.put("order", order);
				renderJSON(RtnUtil.returnSuccess("OK",result));
			}
			int reduceCoupon = 0;
			int reduceUser = 0;
			int diffPay = totalAmount;
			UserAccountDDL couponAccount = null;
			
			if(!StringUtils.isEmpty(couponAccountId)){
				//优先使用代金券类型账户
				couponAccount = UserAccountService.canUse(couponAccountId, productId, product.getSellerUserId(),totalAmount);
				if( couponAccount == null ){
					renderJSON(RtnUtil.returnFail("代金券无法使用编号:"+couponAccountId));
				}
			}
			
			//先扣代金券金额
			if( couponAccount != null){
				diffPay = totalAmount - couponAccount.getAmount();
			}
			//代金券已经够扣了
			if( diffPay <= 0 ){
				reduceCoupon = totalAmount;
				result.put("needPay", false);
				result.put("useUserBalance", 0);
				result.put("useCouponBalance", reduceCoupon);
				boolean order = ShopOrderService.createOrder(together,togetherId,null,
						couponAccountId,0,reduceCoupon,0,buyNum,out_trade_no,user.getId().intValue(), null,address,product,productGroup);
				result.put("order", order);
				renderJSON(RtnUtil.returnSuccess("OK",result));
			}
			
			//说明使用了代金券，但不够支付,全部扣除
			if( couponAccount != null ){
				reduceCoupon = couponAccount.getAmount();
			}			
			
			//使用用户余额账户扣减
			UserAccountDDL userAccount = UserAccountService.getByAccountId(userAccountId);
			if(userAccount!=null){
				diffPay = diffPay - (userAccount==null?0:userAccount.getAmount());
				//余额扣够了
				if( diffPay <= 0 ){
					reduceUser = totalAmount-reduceCoupon;
					result.put("needPay", false);
					result.put("useUserBalance", reduceUser);
					result.put("useCouponBalance", reduceCoupon);
					boolean order = ShopOrderService.createOrder(together,togetherId,userAccountId,couponAccountId,
							reduceUser,reduceCoupon,0,buyNum,out_trade_no,user.getId().intValue(), null,address,product,productGroup);
					result.put("order", order);
					renderJSON(RtnUtil.returnSuccess("OK",result));
				}
			} 
			
			//使用了余额 还不够扣除
			if(userAccount!=null){
				reduceUser = userAccount.getAmount() ;
			}
			
			String body = Jws.configuration.getProperty(appid+".body.prefix")+"-"+product.getProductCategory();
			String spbill_create_ip = InetAddress.getLocalHost().getHostAddress();
			String notify_url = Jws.configuration.getProperty(appid+".notify.url");
			String trade_type="JSAPI";
			String key = Jws.configuration.getProperty(appid+".pay.key");
			
			
			Map<String,Object> ext = new HashMap<String,Object>();
			ext.put("openid", user.getOpenId());
			Map<String,String>  wxResult = API.weixin_unifiedorder(appid, Jws.configuration.getProperty(appid+".mch_id"), 
				body, out_trade_no, diffPay, spbill_create_ip, 
				notify_url, trade_type, key, ext);
			String prepay_id = wxResult.get("prepay_id");
			String nonce_str = wxResult.get("nonce_str");
			Map<String,String> litterPayParams = API.getLitterAppPayParams(appid, prepay_id, key,nonce_str);
			String jsonstr = litterPayParams!=null?gson.toJson(litterPayParams):null;
			boolean order = ShopOrderService.createOrder(together,togetherId,userAccountId,couponAccountId,
					reduceUser,reduceCoupon,diffPay,buyNum,out_trade_no,user.getId().intValue(),jsonstr,address,product,productGroup);
			result.put("useUserBalance", reduceUser);
			result.put("useCouponBalance", reduceCoupon);
			result.put("useCash", diffPay);
			result.put("needPay", true);
			result.put("order", order);
			result.put("litterPayParams",litterPayParams);			
			renderJSON(RtnUtil.returnSuccess("OK",result));
			
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail(e.getMessage()));
		}
	}
	
	public static void cancelPay(String session ,String orderId){
		try{ 
			UsersDDL user = UserService.findBySession(session);
			if(user==null){
				renderJSON(RtnUtil.returnLoginFail());
			}
			ShopOrderDDL order = ShopOrderService.findByOrderId(orderId);
			ShopOrderService.payCanel(order); 
			
			renderJSON(RtnUtil.returnSuccess());
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail(e.getMessage()));
		}
	}
	
	public static void wxPaynotify(){
		try{
			String notifyBody = params.get("body");
			Logger.info("微信回调，body=%s", notifyBody);
			
			Map<String,String> notifyTreeMap = new TreeMap<String,String>();
			Document reader = DocumentHelper.parseText(notifyBody);  
			Iterator<Element> childIt = reader.getRootElement().elementIterator();
			String notifySign = "";
			while(childIt.hasNext()){
				Element child = childIt.next();
				String name = child.getName();
				String value = child.getText();
				if(name.equals("sign")){
					notifySign = value;
					continue;
				} 
				notifyTreeMap.put(name, value);
			} 
			if(!notifyTreeMap.containsKey("appid")){
				throw new Exception("微信回调时，没有appid参数,requestBody="+notifyBody);
			}
			if(!notifyTreeMap.get("return_code").equals("SUCCESS")){
				throw new Exception("微信回调时return_code!=SUCCESS,requestBody="+notifyBody);
			}
			//验证相应签名是否正确
			StringBuffer notifyParams = new StringBuffer();
			Iterator<Map.Entry<String, String>> respParamsIt = notifyTreeMap.entrySet().iterator();
			while(respParamsIt.hasNext()){
				Map.Entry<String, String> entry = respParamsIt.next(); 
				String theKey = entry.getKey();
				String value = entry.getValue();
				if(StringUtils.isEmpty(value)) continue;
				notifyParams.append(theKey).append("=").append(value).append("&"); 
			}
			String appid = String.valueOf(notifyTreeMap.get("appid"));
			String key = Jws.configuration.getProperty(appid+".pay.key");
			notifyParams.append("key=").append(key);
			String notifyStringA = notifyParams.toString();
			String mySign = MD5Util.md5(notifyStringA);
			if(!mySign.equals(notifySign)){
				throw new Exception("微信回调时，签名不正确，mySign="+mySign+",mySingString="+notifyStringA);
			}
			
			String out_trade_no = String.valueOf(notifyTreeMap.get("out_trade_no"));
			String transaction_id = String.valueOf(notifyTreeMap.get("transaction_id"));
			int cash_fee = Integer.parseInt(String.valueOf(notifyTreeMap.get("cash_fee")));
			
			ShopOrderDDL order = ShopOrderService.findByOrderId(out_trade_no);
			if(order == null){
				Logger.error("微信回调时,找不到订单out_trade_no=%s", out_trade_no);
			}else{
				order.setTransactionId(transaction_id);
				order.setNotifyBody(notifyBody);
				if( order.getUseCash() <= cash_fee  ){
					ShopOrderService.paySuccess(order); 
				}else{
					ShopOrderService.payFail(order);
				}
				
			} 
			Document document = DocumentHelper.createDocument();  
			Element xmlElement = document.addElement("xml");
			Element return_code = xmlElement.addElement("return_code");
			return_code.addCDATA("SUCCESS");
			Element return_msg = xmlElement.addElement("return_msg");
			return_msg.addCDATA("OK");
			OutputFormat format = OutputFormat.createPrettyPrint();  
			format.setEncoding("UTF-8");  
			
			StringWriter sw = new StringWriter();
			XMLWriter writer = new XMLWriter(sw, format);
			try {
				writer.write(document);
				writer.flush();
				writer.close(); 
			} catch (IOException e1) {
				Logger.error(e1, e1.getMessage());
			} 
			String rsp = sw.toString();			
			renderText(rsp); 
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			Document document = DocumentHelper.createDocument();  
			Element xmlElement = document.addElement("xml");
			Element return_code = xmlElement.addElement("return_code");
			return_code.addCDATA("FAIL");
			Element return_msg = xmlElement.addElement("return_msg");
			return_msg.addCDATA(e.getMessage());
			OutputFormat format = OutputFormat.createPrettyPrint();  
			format.setEncoding("UTF-8");  
			
			StringWriter sw = new StringWriter();
			XMLWriter writer = new XMLWriter(sw, format);
			try {
				writer.write(document);
				writer.flush();
				writer.close(); 
			} catch (IOException e1) {
				Logger.error(e1, e1.getMessage());
			} 
			String rsp = sw.toString();			
			renderText(rsp);
		}
		
	}
	
	//获取首页需要展现出售的商品
	public static void index(int page,int pageSize){
		try{
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			page = page == 0?1:page;
			pageSize = pageSize == 0?10:pageSize;
			List<ShopProductDDL> products = ShopProductService.listShopIndexProduct(page, pageSize);
			if(products==null || products.size() == 0){
				renderJSON(RtnUtil.returnSuccess("OK",list));
			}			
			for(ShopProductDDL p : products){
				Map<String,Object> result = new HashMap<String,Object>();
				result.put("productId", p.getProductId());
				result.put("productName", p.getProductName());
				result.put("productBanner", API.getObjectAccessUrlSimple( p.getProductBanner()));
				result.put("productOriginPrice",AmountUtil.f2y(p.getProductOriginAmount()));
				result.put("productNowPrice",AmountUtil.f2y(p.getProductNowAmount()));
				result.put("productTogetherPrice",AmountUtil.f2y(p.getProductTogetherAmount()));
				result.put("joinTogether", p.getJoinTogether());
				result.put("platformChecked", p.getPlatformChecked());

				result.put("togetherSales",String.format("%.1f", p.getTogetherSales()/10000f));
				List<ShopTogetherDDL> togethers = ShopTogetherService.listByProductId(p.getProductId(), 1, 2);
				if( togethers != null && togethers.size() == 2){
					String[] together = new String[]{togethers.get(0).getMasterAvatar(),togethers.get(1).getMasterAvatar()};
					result.put("togethers", together);
				}
				
				//小区合作
				List<ShopProductCommunityRelDDL>  communityList = ShopCommunityService.listByProductId(p.getProductId());
				if(communityList!=null && communityList.size()>0){
					List<Map<String,Object>> communities = new ArrayList<Map<String,Object>>(); 
					for( ShopProductCommunityRelDDL c : communityList ){
						Map<String,Object> map = new HashMap<String,Object>();
						map.put("communityId",c.getCommunityId());
						map.put("communityName",c.getCommunityName()); 
						communities.add(map); 
					}
					result.put("communities", communities);
				}
			
				list.add(result);
				
			}
			renderJSON(RtnUtil.returnSuccess("OK",list));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail(e.getMessage()));
		}
	}
	/**
	 * 
	 * @param pcategoryId
	 * @param subCategoryId
	 * @param sale
	 * @param hot
	 * @param orderBy 1=时间降序 2=销量降序 3=价格降序 4=价格升序 5=综合排序
	 */
	public static void listProduct(String shopId,String productId,String keyword,String pCategoryId,String subCategoryId,boolean isSale,boolean isHot,int status,int orderBy,int page,int pageSize){
		try{
			List<Map<String,Object>> mapList = new ArrayList<Map<String,Object>>();
			Map<String,Object> response = new HashMap<String,Object>();
			
			int total = ShopProductService.countProduct(shopId,productId,keyword, pCategoryId, subCategoryId, isSale?1:0, isHot?1:0, status);
			response.put("total", total);
			response.put("pageTotal", Math.ceil(total/(double)pageSize));
			 
			if(total == 0){
				response.put("list", mapList);
				renderJSON(RtnUtil.returnSuccess("OK",response));
			}			
			List<ShopProductDDL> list = ShopProductService.listProduct(shopId,productId,keyword, pCategoryId, subCategoryId, isSale?1:0, isHot?1:0, status,orderBy, page<=0?1:page, pageSize<=0?10:pageSize);
			
			
			for(ShopProductDDL p : list){
				Map<String,Object> result = new HashMap<String,Object>();
				result.put("productId", p.getProductId());
				result.put("productName", p.getProductName());
				result.put("productBanner", API.getObjectAccessUrlSimple( p.getProductBanner()));
				result.put("productOriginPrice",AmountUtil.f2y(p.getProductOriginAmount()));
				result.put("productNowPrice",AmountUtil.f2y(p.getProductNowAmount()));
				if(p.getJoinTogether()==1){
					result.put("productTogetherPrice",AmountUtil.f2y(p.getProductTogetherAmount()));
					result.put("togetherSales",String.format("%.1f", p.getTogetherSales()/10000f));
				}else{
					result.put("productTogetherPrice","0.0");
					result.put("togetherSales","0");
				}
				result.put("joinTogether", p.getJoinTogether());
				result.put("platformChecked", p.getPlatformChecked());
				
				result.put("sotre", p.getStore());
				result.put("status", p.getStatus());
				result.put("createTime", DateUtil.format(p.getCreateTime()));
				result.put("pv", p.getPv());
				result.put("deal", p.getDeal());
				result.put("isHot", p.getIsHot()==1);
				result.put("isSale", p.getIsSale()==1);

				
				List<ShopTogetherDDL> togethers = ShopTogetherService.listByProductId(p.getProductId(), 1, 2);
				if( togethers != null && togethers.size() == 2){
					String[] together = new String[]{togethers.get(0).getMasterAvatar(),togethers.get(1).getMasterAvatar()};
					result.put("togethers", together);
				}
				
				//小区合作
				/*List<ShopProductCommunityRelDDL>  communityList = ShopCommunityService.listByProductId(p.getProductId());
				if(communityList!=null && communityList.size()>0){
					List<Map<String,Object>> communities = new ArrayList<Map<String,Object>>(); 
					for( ShopProductCommunityRelDDL c : communityList ){
						Map<String,Object> map = new HashMap<String,Object>();
						map.put("communityId",c.getCommunityId());
						map.put("communityName",c.getCommunityName()); 
						communities.add(map); 
					}
					result.put("communities", communities);
				}	*/		
				mapList.add(result);				
			}
			
			response.put("list", mapList);
			renderJSON(RtnUtil.returnSuccess("OK", response));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail(e.getMessage()));
		}
	}
	public static void productDetail(String productId){
		try{
			Map<String,Object> result = new HashMap<String,Object>();
			ShopProductDDL p =ShopProductService.getByProductId(productId);
			if(p==null){
				throw new Exception("商品不存在");
			}
			result.put("status", p.getStatus());
			result.put("productId", p.getProductId());
			result.put("productName", p.getProductName());
			result.put("productDesc", StringUtils.isEmpty(p.getProductDesc())?null:p.getProductDesc().split("`"));
			result.put("joinTogether", p.getJoinTogether());
			result.put("productOriginPrice",AmountUtil.f2y(p.getProductOriginAmount()));
			result.put("productNowPrice",AmountUtil.f2y(p.getProductNowAmount()));
			
			if(p.getJoinTogether()!=null && p.getJoinTogether() == 1){
				result.put("productTogetherPrice",AmountUtil.f2y(p.getProductTogetherAmount()));
				result.put("joinTogether", p.getJoinTogether());
				result.put("togetherNumber", p.getTogetherNumber());
				result.put("togetherSales",p.getTogetherSales());
			}
			result.put("platformChecked", p.getPlatformChecked());
			//截图
			List<ShopProductImagesDDL> ssimages = ShopProductImageService.listImages(productId, ShopProductImageService.SCREENSHOT_TYPE, 1, 5);
			if(ssimages!=null && ssimages.size()>0){
				List<String> screenshots = new ArrayList<String>();
				for( ShopProductImagesDDL img : ssimages ){
					screenshots.add( API.getObjectAccessUrlSimple( img.getImageKey()));
				}
				result.put("screenshots", screenshots);
			}
			//详情图片
			List<ShopProductImagesDDL> detailimages = ShopProductImageService.listImages(productId, ShopProductImageService.DETAIL_TYPE, 1, 10);
			if(detailimages!=null && detailimages.size()>0){
				List<String> detailImages = new ArrayList<String>();
				for( ShopProductImagesDDL img : detailimages ){
					detailImages.add( API.getObjectAccessUrlSimple( img.getImageKey()));
				}
				result.put("detailImages", detailImages);
			}
			
			//详情图片
			List<ShopProductImagesDDL> showimages = ShopProductImageService.listImages(productId, ShopProductImageService.BUYER_SHOW, 1, 10);
			if(showimages!=null && showimages.size()>0){
				List<String> showImages = new ArrayList<String>();
				for( ShopProductImagesDDL img : showimages ){
					showImages.add( API.getObjectAccessUrlSimple( img.getImageKey()));
				}
				result.put("showImages", showImages);
			}
			
			//属性
			List<ShopProductAttrRelDDL> attributesList = ShopProductAttrService.listByProduct(productId);
			if(attributesList!=null && attributesList.size()>0){
				List<String> attributes = new ArrayList<String>();
				for( ShopProductAttrRelDDL attr : attributesList ){
					attributes.add(attr.getAttrName());
				}
				result.put("attributes", attributes);
			}
			
			//参团情况
			List<ShopTogetherDDL> togetherList = ShopTogetherService.listCanJoinByProductId(productId, 1, 5);
			int togetherCount =( ShopTogetherService.countByProductId(productId) );
			result.put("togetherCount", togetherCount);
			if(togetherList!=null && togetherList.size()>0){
				DateFormat df = new SimpleDateFormat("MM-dd HH:mm");
				List<Map<String,Object>> togetherJoiners = new ArrayList<Map<String,Object>>();
				for( ShopTogetherDDL st : togetherList ){
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("togetherId", st.getTogetherId());
					map.put("masterAvatar", st.getMasterAvatar());
					map.put("masterName", st.getMasterName());
					map.put("expireTime", df.format(new Date( st.getExpireTime())));
					map.put("togetherNumberRedius", st.getTogetherNumberResidue());
					togetherJoiners.add(map);
				}
				result.put("togetherJoiners", togetherJoiners);
			} 			
			//商品组
			List<ShopProductGroupDDL> groupList = ShopProductGroupService.findByProductId(productId);
			if(groupList!=null && groupList.size()>0){				 
				List<Map<String,Object>> groups = new ArrayList<Map<String,Object>>();
				int index = 0;
				for( ShopProductGroupDDL group : groupList ){
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("groupId",group.getGroupId());
					map.put("groupIndex",index);
					map.put("groupName", group.getGroupName());
					map.put("groupImage",API.getObjectAccessUrlSimple( group.getGroupImage()));
					map.put("groupPrice",AmountUtil.f2y(group.getGroupPrice()));
					map.put("groupTogetherPrice",AmountUtil.f2y(group.getGroupTogetherPrice()));
					groups.add(map);
					index++;
				}
				result.put("groups", groups);
			}
			//小区合作
			List<ShopProductCommunityRelDDL>  communityList = ShopCommunityService.listByProductId(productId);
			if(communityList!=null && communityList.size()>0){
				List<Map<String,Object>> communities = new ArrayList<Map<String,Object>>(); 
				for( ShopProductCommunityRelDDL c : communityList ){
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("communityId",c.getCommunityId());
					map.put("communityName",c.getCommunityName());
					communities.add(map); 
				}
				result.put("communities", communities);
			} 
			
			//是否有优惠券领取
			List<ShopCouponMngDDL> couponList = ShopCouponMngService.selectCouponActivities(null,productId, p.getSellerUserId(), 3);
			if(couponList!=null && couponList.size()>0){
				List<Map<String,Object>> coupons = new ArrayList<Map<String,Object>>(); 
				for( ShopCouponMngDDL c : couponList ){
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("id",c.getId());
					map.put("amount",AmountUtil.f2y(c.getAmount()));
					map.put("name",c.getCouponName());
					map.put("valid",1);
					map.put("expireTime",DateUtil.format(c.getExpireTime()));
					coupons.add(map); 
				}
				result.put("coupons", coupons);
			}
			renderJSON(RtnUtil.returnSuccess("OK",result));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail(e.getMessage()));
		}
	
	}
	 
	
	public static void listAccounts(String session,String productId,double price){
		try{
			UsersDDL user = UserService.findBySession(session);
			if(user==null){
				renderJSON(RtnUtil.returnLoginFail());
			}
			List<UserAccountDDL> accountList = UserAccountService.listALLByUser(user.getId().intValue());
			
			Map<String,Object> result = new HashMap<String,Object>();
			
			List<Map<String,Object>> validAccounts = new ArrayList<Map<String,Object>>();
			List<Map<String,Object>> invalidAccounts = new ArrayList<Map<String,Object>>();

			if(accountList == null || accountList.size() == 0){
				renderJSON(RtnUtil.returnSuccess("OK",result));
			}
			
			ShopProductDDL product = ShopProductService.getByProductId(productId);
			if(product==null){
				renderJSON(RtnUtil.returnFail("商品不存在"));
			}
			
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for(UserAccountDDL account : accountList){
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("accountId", account.getAccountId());
				map.put("accountName", account.getAccountName());
				map.put("accountAmount", AmountUtil.f2y(account.getAmount()));
				if(account.getAccountType() == 1){
					result.put("basicAccount", map);
				}else if(account.getAccountType() == 2){
					map.put("expireTime", df.format(new Date(account.getExpireTime())));
					try{
						UserAccountService.canUse(account.getAccountId(), productId,product.getSellerUserId(), AmountUtil.y2f(price));
						map.put("canUse", true); 
						validAccounts.add(map);
					}catch(Exception e){
						Logger.warn(e, e.getMessage());
						map.put("canUse", false);
						invalidAccounts.add(map);
					}
				}
			}
			result.put("validAccounts", validAccounts);
			result.put("invalidAccounts", invalidAccounts);
			renderJSON(RtnUtil.returnSuccess("OK",result));
			
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail(e.getMessage()));
		}
	}
	
	public static void getOrder(String session,String orderNo){
		try{
			UsersDDL user = UserService.findBySession(session);
			if(user==null){
				renderJSON(RtnUtil.returnLoginFail());
			}
			Map<String,Object> result = new HashMap<String,Object>();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			ShopOrderDDL order = ShopOrderService.findByOrderId(orderNo);
			ShopProductDDL product = ShopProductService.getByProductId(order.getProductId());
			result.put("isTogether",false);
			if(order.getTogetherId()!=null){
				result.put("isTogether",true);
				ShopTogetherDDL together= ShopTogetherService.listByTogetherId(order.getTogetherId());
				Map<String,Object> togetherMap = new HashMap<String,Object>();
				togetherMap.put("masterAvatar", together.getMasterAvatar());
				togetherMap.put("masterName", together.getMasterName());
				togetherMap.put("createTime", df.format(new Date(together.getCreateTime())));
				togetherMap.put("expireTime", df.format(new Date(together.getExpireTime())));
				result.put("together",togetherMap);
				
				List<ShopTogetherJoinerDDL> togethers = ShopTogetherService.listJoinerByTogetherId(order.getTogetherId());
				List<Map<String,Object>> togetherList = new ArrayList<Map<String,Object>>(); 
				for( ShopTogetherJoinerDDL t : togethers ){
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("avatar",t.getUserAvatar());
					map.put("name",t.getUserName());
					map.put("joinTime",df.format(new Date(t.getJoinTime())));
					togetherList.add(map); 
				}
				result.put("togethers", togetherList);
				result.put("togetherPrice", AmountUtil.f2y(product.getProductTogetherAmount()) );
				result.put("needTogetherNumber",product.getTogetherNumber() - togethers.size());
				result.put("togetherNumber",product.getTogetherNumber());
			} 		
			result.put("order",order); 
			result.put("originPrice",AmountUtil.f2y(product.getProductOriginAmount())); 
			result.put("shareImage",API.getObjectAccessUrlSimple( "4d638917b143496a95bb83d3d935c7c1"));
			renderJSON(RtnUtil.returnSuccess("OK",result));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail(e.getMessage()));
		}
	}

	public static void listOrder(String session,int status,boolean imSeller,int page,int pageSize){
		try{
			UsersDDL user = UserService.findBySession(session);
			if(user==null){
				renderJSON(RtnUtil.returnLoginFail());
			}
			Map<String,Object> result = new HashMap<String,Object>();
			result.put("isSeller", user.getIsSeller()!=null && user.getIsSeller()==1);
			//我的订单列表，需要显示什么呢？
			//商品组图片  商品组名称  商品名称  商品ID 商品组ID 订单状态  购买数量 商品组价格  现金付款额度  优惠券付款额度  下单时间
			List<ShopOrderDDL> list = ShopOrderService.listOrder(user.getId().intValue(),status, imSeller,page, pageSize);
			List<Map<String,Object>> orders = new ArrayList<Map<String,Object>>();
			if(list == null || list.size() == 0){
				result.put("orders",orders);
				renderJSON(RtnUtil.returnSuccess("OK",result));
			}
			for(ShopOrderDDL order : list){
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("orderId",order.getOrderId());
				map.put("groupImg", API.getObjectAccessUrlSimple( order.getGroupImg()));
				map.put("groupName", order.getGroupName());
				map.put("productName", order.getProductName());
				map.put("groupId", order.getGroupId());
				map.put("productId", order.getProductId());
				map.put("orderStatus", order.getStatus());
				map.put("prize", order.getPrizeLevel());
				map.put("buyNum", order.getBuyNum());
				map.put("groupPrice", AmountUtil.f2y(order.getGroupPrice()));
				map.put("totalPay", AmountUtil.f2y(order.getGroupPrice()*order.getBuyNum()));
				if(order.getGroupTogetherPrice() != null){
					map.put("groupTogetherPrice", AmountUtil.f2y(order.getGroupTogetherPrice()));
				}
				if(order.getUseCash()!=null){
					map.put("cashPay", AmountUtil.f2y(order.getUseCash()));
				}
				if(order.getUseCouponAmount()!=null){
					map.put("couponPay", AmountUtil.f2y(order.getUseCouponAmount()));
				}
				if(order.getUseUserAmount()!=null){
					map.put("balancePay", AmountUtil.f2y(order.getUseUserAmount()));
				}
				map.put("orderTime", DateUtil.format(order.getOrderTime()));
				orders.add(map);
			}
			result.put("orders",orders);
			renderJSON(RtnUtil.returnSuccess("OK",result));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
		}
	}
	
	public static void orderDetail(String session,String orderId){
		try{
			UsersDDL user = UserService.findBySession(session);
			if(user==null){
				renderJSON(RtnUtil.returnLoginFail());
			} 
			ShopOrderDDL order = ShopOrderService.findByOrderId(orderId); 
			if(order == null){
				renderJSON(RtnUtil.returnFail("订单不存在"));
			} 
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("isSeller", user.getId().intValue() == order.getSellerUserId());
			map.put("orderId",order.getOrderId());
			map.put("groupImg", API.getObjectAccessUrlSimple( order.getGroupImg()));
			map.put("groupName", order.getGroupName());
			map.put("productName", order.getProductName());
			map.put("groupId", order.getGroupId());
			map.put("productType", order.getProductType());
			map.put("productId", order.getProductId());
			map.put("orderStatus", order.getStatus());
			map.put("buyNum", order.getBuyNum());
			map.put("groupPrice", AmountUtil.f2y(order.getGroupPrice()));
			map.put("prize", order.getPrizeLevel());
			map.put("sellerTelNumber", order.getSellerTelNumber());
			map.put("sellerWxNumber", order.getSellerWxNumber());
			
			map.put("shareImage",API.getObjectAccessUrlSimple( "4d638917b143496a95bb83d3d935c7c1"));
			
			if(order.getGroupTogetherPrice() != null){
				map.put("groupTogetherPrice", AmountUtil.f2y(order.getGroupTogetherPrice()));
			}
			if(order.getUseCash()!=null){
				map.put("cashPay", AmountUtil.f2y(order.getUseCash()));
			}
			if(order.getUseCouponAmount()!=null){
				map.put("couponPay", AmountUtil.f2y(order.getUseCouponAmount()));
			}
			if(order.getUseUserAmount()!=null){
				map.put("balancePay", AmountUtil.f2y(order.getUseUserAmount()));
			}
			map.put("orderTime", DateUtil.format(order.getOrderTime()));
			
			//解析地址信息
			JsonObject addressJson = new JsonParser().parse(order.getAddress()).getAsJsonObject();
			String userName = addressJson.get("userName").getAsString();
			String telNumber = addressJson.get("telNumber").getAsString();
			String address = addressJson.get("provinceName").getAsString() + addressJson.get("cityName").getAsString() + addressJson.get("detailInfo").getAsString();
			Map<String,String> addressMap = new HashMap<String,String>();
			addressMap.put("userName", userName);
			addressMap.put("provinceName", addressJson.get("provinceName").getAsString());
			addressMap.put("cityName", addressJson.get("cityName").getAsString());
			addressMap.put("countyName", addressJson.get("countyName").getAsString());
			addressMap.put("detailInfo", addressJson.get("detailInfo").getAsString());
			addressMap.put("address", address);
			addressMap.put("telNumber", telNumber);
			map.put("address", addressMap); 
			//获取发货跟踪信息
			ShopExpressDDL express = ShopExpressService.getByOrderId(orderId);
			if(express!=null){
				Map<String,Object> expressMap = new HashMap<String,Object>();
				expressMap.put("state",express.getState());
				expressMap.put("id", express.getId());
				expressMap.put("station", express.getAcceptStation());
				expressMap.put("time",express.getAcceptTime());
				map.put("express", expressMap);
			}
			//获取团详情
			map.put("totalPay", AmountUtil.f2y(order.getGroupPrice()*order.getBuyNum()));
			if(!StringUtils.isEmpty(order.getTogetherId())){
				map.put("totalPay", AmountUtil.f2y(order.getGroupTogetherPrice()*order.getBuyNum()));
				ShopTogetherDDL together= ShopTogetherService.listByTogetherId(order.getTogetherId());
				List<ShopTogetherJoinerDDL> togethers = ShopTogetherService.listJoinerByTogetherId(order.getTogetherId());
				List<Map<String,Object>> togetherList = new ArrayList<Map<String,Object>>(); 
				for( ShopTogetherJoinerDDL t : togethers ){
					Map<String,Object> joiner = new HashMap<String,Object>();
					joiner.put("id", t.getTogetherId());
					joiner.put("master", t.getIsMaster()==1);
					joiner.put("avatar",t.getUserAvatar());
					joiner.put("name",t.getUserName());
					joiner.put("joinTime",DateUtil.format(t.getJoinTime()));
					togetherList.add(joiner); 
				}
				Map<String,Object> togetherMap = new HashMap<String,Object>();
				togetherMap.put("joiner", togetherList);
				togetherMap.put("status", together.getStatus());
				togetherMap.put("totalNumber",together.getTogetherNumber());
				togetherMap.put("residueNumber",together.getTogetherNumberResidue());
				togetherMap.put("createTime", DateUtil.format(together.getCreateTime()));
				togetherMap.put("expireTime", DateUtil.format(together.getExpireTime()));
				map.put("together", togetherMap);
			}
			renderJSON(RtnUtil.returnSuccess("OK",map));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail());
		}
	}
	
	//获取物流详细情况 
	public static void shipperTraces(String session,int expressId){
		try{
			UsersDDL user = UserService.findBySession(session);
			if(user==null){
				renderJSON(RtnUtil.returnLoginFail());
			}
			
			ShopExpressDDL express = ShopExpressService.getById(expressId);
			if(express==null){
				renderJSON(RtnUtil.returnFail("物流不存在"));
			}
			express = ShopExpressService.updateExpressTrace(express);	
			renderJSON(RtnUtil.returnSuccess("OK",express));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail());
		}
	}
	
	//status = 1 有效  2无效
	public static void listWallet(String session,int status,int page,int pageSize){
		try{
			
			UsersDDL user = UserService.findBySession(session);
			if(user==null){
				renderJSON(RtnUtil.returnLoginFail());
			}
			Map<String,Object> result = new HashMap<String,Object>();
			int validCount  = UserAccountService.countValidateCoupons(user.getId().intValue());
			result.put("validCouponCount", validCount);
			UserAccountDDL basicAccount = UserAccountService.getBasicAccount(user.getId().intValue());
			if(basicAccount!=null){
				result.put("basicAccountId", basicAccount.getAccountId());
				result.put("basicAccountName", basicAccount.getAccountName());
				result.put("basicAccountAmount",AmountUtil.f2y(basicAccount.getAmount()));
			}else{
				result.put("basicAccountId","");
				result.put("basicAccountName", "");
				result.put("basicAccountAmount","0.00");
			}
			List<UserAccountDDL> couponList = null;
			if(status == 1){
				couponList = UserAccountService.listValidateCoupons(user.getId().intValue(), page, pageSize);
			}else if(status == 2){
				couponList = UserAccountService.listInvalidateCoupons(user.getId().intValue(), page, pageSize);
			}
			
			List<Map<String,Object>> coupons = new ArrayList<Map<String,Object>>();
			if(couponList == null || couponList.size() == 0){
				renderJSON(RtnUtil.returnSuccess("OK",result));
			}
		 
			for(UserAccountDDL account : couponList){
				Map<String,Object> coupon = new HashMap<String,Object>();
				coupon.put("accountId", account.getAccountId());
				coupon.put("accountName", account.getAccountName());
				coupon.put("accountAmount", AmountUtil.f2y(account.getAmount()));
				coupon.put("expireTime",DateUtil.format(account.getExpireTime()));
				coupons.add(coupon);
			} 
			
			result.put("coupons", coupons);
			
			renderJSON(RtnUtil.returnSuccess("OK",result));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail(e.getMessage()));
		}
	}
	
	public static void getCoupon(String session,int couponId,String productId){
		try{
			UsersDDL user = UserService.findBySession(session);
			if(user==null){
				renderJSON(RtnUtil.returnLoginFail());
			}
			/*ShopProductDDL p =ShopProductService.getByProductId(productId);
			if(p==null){
				throw new Exception("商品不存在");
			} */
			Map<String,Object> result = new HashMap<String,Object>();
			boolean get = ShopCouponMngService.getCoupon(couponId, user.getId().intValue());
			result.put("get",get);
			List<ShopCouponMngDDL> couponList = ShopCouponMngService.selectCouponActivities(null,productId, 0, 3);
 			if(couponList!=null && couponList.size()>0){
				List<Map<String,Object>> coupons = new ArrayList<Map<String,Object>>(); 
				for( ShopCouponMngDDL c : couponList ){
					//是否有优惠券领取
					Map<String,Object> map = new HashMap<String,Object>();
					
					String key = "GET_COUPON_"+user.getId().intValue()+"_"+c.getId();
					Object value = Cache.get(key);
					int gets = value==null?0:Integer.parseInt(String.valueOf(value));
					map.put("valid",1);
					if(gets>=c.getLimitTimes()){
						map.put("valid",0);
					} 
					
					map.put("id",c.getId());
					map.put("amount",AmountUtil.f2y(c.getAmount()));
					map.put("name",c.getCouponName());
					map.put("expireTime",DateUtil.format(c.getExpireTime()));
					coupons.add(map); 
				}
				result.put("coupons", coupons);
			}
			renderJSON(RtnUtil.returnSuccess("OK",result));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail(e.getMessage()));
		}
	} 
	
	
	public static void isTogethering(String togetherid){
		try{
			ShopTogetherDDL together = ShopTogetherService.getShopTogether(togetherid);
			if(together!=null && together.getStatus() == ShopTogetherService.TOGETHER_ING){
				renderJSON(RtnUtil.returnSuccess("OK",true));
			}
			renderJSON(RtnUtil.returnSuccess("OK",false));			
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail(e.getMessage()));
		}
	} 
	
	
	/**
	 * 查询店铺首页配置
	 * @param shopId
	 */
	public static void getShopIndexConfig(String shopId){
		try{			
			if(StringUtils.isEmpty(shopId)){
				shopId = String.valueOf(Jws.configuration.get("shop.index.default"));
			}
			ShopIndexDDL shopIndex = ShopIndexService.getByShopId(shopId);
			
			if(shopIndex==null){
				renderJSON(RtnUtil.returnFail("店铺不存在"));
			}
			if(StringUtils.isEmpty(shopIndex.getConfig())){
				renderJSON(RtnUtil.returnSuccess());
			}
			ShopIndexDto shopIndexConfig = gson.fromJson(shopIndex.getConfig(), ShopIndexDto.class);
			//处理图片URL
			shopIndexConfig.shopAvatar = API.getObjectAccessUrlSimple(shopIndexConfig.shopAvatarKey);
			shopIndexConfig.shopBanner = API.getObjectAccessUrlSimple(shopIndexConfig.shopBannerKey);			
			shopIndexConfig.activityBg = API.getObjectAccessUrlSimple(shopIndexConfig.activityBgKey);
			
			 
			for(ShopNavDto dto : shopIndexConfig.firstNavList){
				if(dto.linkType == 2){
					dto.img = API.getObjectAccessUrlSimple(dto.imgkey);
				}
			}
			for(ShopNavDto dto : shopIndexConfig.secondNavList){
				if(dto.linkType == 2){
					dto.img = API.getObjectAccessUrlSimple(dto.imgkey);
				}
			}
			for(ShopNavDto dto : shopIndexConfig.swiperList){
				if(dto.linkType == 2){
					dto.img = API.getObjectAccessUrlSimple(dto.imgkey);
				}
			}
			for(ShopNavDto dto : shopIndexConfig.thirdNavList){
				if(dto.linkType == 2){
					dto.img = API.getObjectAccessUrlSimple(dto.imgkey);
				}
			}
			for(ShopNavDto dto : shopIndexConfig.fourthNavList){
				if(dto.linkType == 2){
					dto.img = API.getObjectAccessUrlSimple(dto.imgkey);
				}
			}
			for(ShopNavDto dto : shopIndexConfig.fiveNavList){
				if(dto.linkType == 2){
					dto.img = API.getObjectAccessUrlSimple(dto.imgkey);
				}
			}
			
			Map<String,Object> result = new HashMap<String,Object>();
			result.put("config", shopIndexConfig);
			
			//兼容小程序吧
			result.put("shopName", shopIndex.getName());
			result.put("shopAvatar",API.getObjectAccessUrlSimple(shopIndex.getAvatar()));
			result.put("follow", shopIndex.getFollow());			
			
			//获取可领取的代金券
			List<ShopCouponMngDDL> couponList = ShopCouponMngService.selectCouponActivities(shopId,null,0, 10);
 			if(couponList!=null && couponList.size()>0){
				List<Map<String,Object>> coupons = new ArrayList<Map<String,Object>>(); 
				for( ShopCouponMngDDL c : couponList ){
					//是否有优惠券领取
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("valid",1);					 
					map.put("id",c.getId());
					map.put("amount",AmountUtil.f2y(c.getAmount()));
					map.put("name",c.getCouponName());
					map.put("desc","活动时间范围："+DateUtil.format(c.getStartTime())+" 至 "+DateUtil.format(c.getEndTime()));
					map.put("expireTime",DateUtil.format(c.getExpireTime()));
					coupons.add(map); 
				}
				result.put("coupons", coupons);
			} 
			renderJSON(RtnUtil.returnSuccess("OK",result));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail(e.getMessage()));
		}
	} 
	
	
	public static void listWeTao(int page,int pageSize,int id,String shopId){
		try{
			 
			List<Map<String,Object>> weTaos = new ArrayList<Map<String,Object>>();
			Map<String,Object> response = new HashMap<String,Object>();
			
			int total = ShopWeTaoService.countWeTao(shopId,"",0);
			response.put("total", total);
			response.put("pageTotal", Math.ceil(total/(double)pageSize));
			 
			if(total == 0){
				response.put("list", weTaos);
				renderJSON(RtnUtil.returnSuccess("OK",response));
			}			
			
			String ip = request.remoteAddress;
			
			if(id>0){		
				String key = ip+"_"+id;
				if(Cache.safeAdd(key, "1", "1d")){
					ShopWeTaoService.zan(id);
				}else{
					Cache.delete(key);
					ShopWeTaoService.cancelZan(id);
				}
			}
			
			page = page==0?1:page;
			pageSize = pageSize==0?10:pageSize;
			
			
			List<ShopWetaoDDL>  list = ShopWeTaoService.listWeTao(shopId,"",0, page, pageSize);			
 			
			for(ShopWetaoDDL weTao : list){
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("id",weTao.getId());
				map.put("content", weTao.getContent());
				map.put("createTime", DateUtil.timeDesc(weTao.getCreateTime()));
				map.put("comment", weTao.getComment());
				String key = ip+"_"+weTao.getId();
				if(Cache.get(key)!=null){
					map.put("isZan",true);
				}
				//deal Image
				if(!StringUtils.isEmpty(weTao.getImages())){
					List<Map<String,Object>> imgs = new ArrayList<Map<String,Object>>();
					for(String ossKey : weTao.getImages().split(",")){
						Map<String,Object> imgOne = new HashMap<String,Object>();
						imgOne.put("remoteUrl", API.getObjectAccessUrlSimple(ossKey));
						imgOne.put("osskey", ossKey);
						imgs.add(imgOne);
					}	
					map.put("images", imgs);
				}
				
				map.put("view", weTao.getView());
				map.put("zan", weTao.getZan());			
				 
				weTaos.add(map);
			}
			response.put("list", weTaos);
			renderJSON(RtnUtil.returnSuccess("OK", response));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail("服务器异常"));
		}
	}
	
	public static void zanOnDetailPage(int id){
		try{
			Map<String,Object> detail = new HashMap<String,Object>();
			ShopWetaoDDL weTao = ShopWeTaoService.get(id);
			if(weTao==null){
				renderJSON(RtnUtil.returnFail("点赞失败"));
			}
			String ip = request.remoteAddress;
			String key = ip+"_"+id;
			if(Cache.safeAdd(key, "1", "1d")){
				ShopWeTaoService.zan(id);
				detail.put("isZan", true);
			}else{
				Cache.delete(key);
				ShopWeTaoService.cancelZan(id);
				detail.put("isZan", false);
			}
			weTao = ShopWeTaoService.get(id);
			detail.put("zan", weTao.getZan());
			renderJSON(RtnUtil.returnSuccess("OK", detail));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail("服务器异常"));
		}
	}
	public static void weTaoDetail(int id){
		try{
			Map<String,Object> detail = new HashMap<String,Object>();
			ShopWetaoDDL weTao = ShopWeTaoService.get(id);
			if(weTao==null){
				renderText("找不到网页");
			}
			
			detail.put("id", weTao.getId());
			detail.put("seoTitle", weTao.getSeoTitle());
			detail.put("seoKey", weTao.getSeoKey());
			detail.put("seoDesc", weTao.getSeoDesc());
			detail.put("content", weTao.getContent());
			detail.put("zan", weTao.getZan());
			detail.put("shopId", weTao.getShopId());
			detail.put("comment", weTao.getComment());			
			 
			
			String ip = request.remoteAddress;
			String key = ip+"_"+id;
			if(Cache.get(key)!=null){
				detail.put("isZan",true);
			}else{
				detail.put("isZan",false);
			}
			
			
			List<String> images = new ArrayList<String>();
			if(!StringUtils.isEmpty(weTao.getImages())){
				for(String ossKey : weTao.getImages().split(",")){
					images.add(API.getObjectAccessUrlSimple(ossKey));
				}
			}
			
			Map<String,Object> comments = wrapComments(1,5,id,null);
			ShopWeTaoService.view(id);
			renderTemplate("weTaoDetailTmp.html",detail,images,comments);
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderText("服务器异常");
		}
	}
	
	
	public static void delWeTaoComment(int commentId,int weTaoId,String session,int page,int pageSize){
		try{
			UsersDDL user = UserService.findBySession(session);
			if(user==null){
				renderJSON(RtnUtil.returnLoginFail());
			} 
 			ShopWeTaoService.deleteComment(weTaoId,commentId);
			Map<String,Object> response = wrapComments(page,pageSize,weTaoId,user.getId().intValue());
			renderJSON(RtnUtil.returnSuccess("OK", response));
			
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail("服务器异常"));
		}
	}
	
	
	public static void addWeTaoComment(int weTaoId,String session,String comment,int page,int pageSize){
		try{
			UsersDDL user = UserService.findBySession(session);
			if(user==null){
				renderJSON(RtnUtil.returnLoginFail());
			} 
			ShopWeTaoService.comment(weTaoId, user.getId().intValue(), user.getAvatarUrl(), user.getNickName(), comment, request.remoteAddress);
			
			
			Map<String,Object> response = wrapComments(page,pageSize,weTaoId,user.getId().intValue());
			renderJSON(RtnUtil.returnSuccess("OK", response));
			
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail("服务器异常"));
		}
	}
	
	public static void listWeTaoComment(String session,int weTaoId,int page,int pageSize){
		try{ 
			Integer userId = null;
			UsersDDL user = UserService.findBySession(session);
			if(user!=null){
				 userId = user.getId().intValue();
			} 
			Map<String,Object> response = wrapComments(page,pageSize,weTaoId,userId);
			renderJSON(RtnUtil.returnSuccess("OK", response));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail("服务器异常"));
		}
	}
	
	private static Map<String,Object> wrapComments(int page,int pageSize,int weTaoId,Integer userId){
		List<Map<String,Object>> comments = new ArrayList<Map<String,Object>>();
		Map<String,Object> response = new HashMap<String,Object>();
		
		int total = ShopWeTaoCommentService.countComment(weTaoId);
		response.put("total", total);
		response.put("pageTotal", Math.ceil(total/(double)pageSize));
		 
		if(total == 0){
			response.put("list", comments);
			return response;
		}	
		
		page = page==0?1:page;
		pageSize = pageSize==0?10:pageSize;
		
		 List<ShopWetaoCommentDDL>  list = ShopWeTaoCommentService.list(weTaoId,page, pageSize);
		 
			
		for(ShopWetaoCommentDDL comment : list){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("id",comment.getId());
			map.put("weTaoId", comment.getWetaoId());
			map.put("createTime", DateUtil.timeDesc(comment.getCreateTime()));
			map.put("comment", comment.getComment());
			map.put("nickName", comment.getNickName());
			map.put("avatar", comment.getAvatar());
			map.put("userId", comment.getUserId());
			if(userId!=null && comment.getUserId() == userId.intValue()){
				map.put("isAdmin", true);
			}
			comments.add(map);
		}
		response.put("list", comments);
		return response;
	}
	
	public static void categoryALL(String shopId){
		try{ 
			SelectSourceDto selectSource =  ShopCategoryService.reflushCategoryALL(shopId,false);
			renderJSON(RtnUtil.returnSuccess("OK", selectSource));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail("服务器异常"));
		}
	}
	 
}
