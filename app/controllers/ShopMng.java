package controllers;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import controllers.dto.ProductInfoDto;
import controllers.dto.ProductInfoDto.Category;
import controllers.dto.ProductInfoDto.Group;
import controllers.dto.ProductInfoDto.Image;
import controllers.dto.ProductInfoDto.TextDetail;
import controllers.dto.ProductInfoDto.TogetherInfo;
import controllers.dto.SelectSourceDto;
import dto.shop.ShopIndexDto;
import jws.Logger;
import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import jws.mvc.Controller;
import jws.mvc.Http;
import modules.shop.ddl.UsersDDL;
import modules.shop.ddl.ShopApplyInfoDDL;
import modules.shop.ddl.ShopCouponMngDDL;
import modules.shop.ddl.ShopExpressCodeDDL;
import modules.shop.ddl.ShopExpressDDL;
import modules.shop.ddl.ShopMngSessionDDL;
import modules.shop.ddl.ShopOrderDDL;
import modules.shop.ddl.ShopProductAttrDDL;
import modules.shop.ddl.ShopProductAttrRelDDL;
import modules.shop.ddl.ShopProductCategoryChildDDL;
import modules.shop.ddl.ShopProductCategoryDDL;
import modules.shop.ddl.ShopProductCategoryRelDDL;
import modules.shop.ddl.ShopProductDDL;
import modules.shop.ddl.ShopProductGroupDDL;
import modules.shop.ddl.ShopProductImagesDDL;
import modules.shop.service.ApplyService;
import modules.shop.service.ShopCategoryService;
import modules.shop.service.ShopCouponMngService;
import modules.shop.service.ShopExpressService;
import modules.shop.service.ShopIndexService;
import modules.shop.service.ShopMngService;
import modules.shop.service.ShopOrderService;
import modules.shop.service.ShopProductAttrService;
import modules.shop.service.ShopProductGroupService;
import modules.shop.service.ShopProductImageService;
import modules.shop.service.ShopProductService;
import modules.shop.service.SmsService;
import modules.shop.service.UserService;
import util.API;
import util.AmountUtil;
import util.DateUtil;
import util.RtnUtil;

public class ShopMng extends Controller{
	private static Gson gson = new GsonBuilder().disableHtmlEscaping().create(); 
	public static void attrSource(String keyword){
		List<Object> sources = new ArrayList<Object>();
		try{ 
			List<ShopProductAttrDDL> list = ShopProductAttrService.searchByName(keyword);
			if(list==null || list.size() == 0){
				renderJSON(sources); 
			}
			for(ShopProductAttrDDL attr : list){
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("id", attr.getAttrId());
				map.put("value", attr.getAttrName());
				map.put("sort", 0);
				map.put("selected",false);
				sources.add(map);
			}
			renderJSON(sources);
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(sources);
		}
	}
	
	public static void categoryFirSource(String keyword){
		try{ 
			List<ShopProductCategoryDDL> list = ShopCategoryService.searchByPCategoryName(keyword);
			List<Object> sources = new ArrayList<Object>();
			if(list==null || list.size() == 0){
				renderJSON(sources); 
			}
			for(ShopProductCategoryDDL cat : list){
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("id", cat.getCategoryId());
				map.put("value",cat.getCategoryName());
				map.put("sort", 0);
				map.put("selected",false);
				sources.add(map);
			}
			renderJSON(sources);
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(null);
		}
	}
	
	public static void categorySecSource(String keyword){
		try{ 
			List<ShopProductCategoryChildDDL> list = ShopCategoryService.searchBySubCategoryName(keyword);
			List<Object> sources = new ArrayList<Object>();
			if(list==null || list.size() == 0){
				renderJSON(sources); 
			}
			for(ShopProductCategoryChildDDL cat : list){
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("id", cat.getCategoryId());
				map.put("value",cat.getCategoryName());
				map.put("sort", 0);
				map.put("selected",false);
				sources.add(map);
			}
			renderJSON(sources);
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(null);
		}
	}
	/**
	 * 枚举源
	 * @param type
	 */
	public static void listSource(int type){
		try{ 
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			if(type == 1){
				for(ShopProductService.Type t : ShopProductService.Type.values()){
					Map<String,Object> one = new HashMap<String,Object>();
					one.put("id",t.getValue());
					one.put("value", t.getText());
					list.add(one);
				}
			}
			
			if(type == 2){
				for(ShopProductService.Status t : ShopProductService.Status.values()){
					Map<String,Object> one = new HashMap<String,Object>();
					one.put("id",t.getValue());
					one.put("value", t.getText());
					list.add(one);
				}
			}
			if(type == 3){
				List<ShopExpressCodeDDL>  expressList = ShopExpressService.listExpress();
				for(ShopExpressCodeDDL express : expressList){
					Map<String,Object> one = new HashMap<String,Object>();
					one.put("id",express.getShipperCode());
					one.put("value",express.getShipperName());
					list.add(one);
				}
			}
			renderJSON(list);
			 
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(null);
		}
	}
	
	
	/**
	 * 	{
			"title": "【测试】我商品标题",
			"p_category_id": "CAT-20171225155134-145353",
			"sub_category_id": "CAT_SUB-20171226145637-101671",
			"attrs_1": "ATTR-20171226145402-196377",
			"attrs_2": "ATTR-20171225160551-181366",
			"attrs_3": "ATTR-20171225160455-141030",
			"play_pics": ["d9883fb70d9b45779085179d1cd80add", "db6d15dcd99644bfb38e11271a1dfe17", "db6d15dcd99644bfb38e11271a1dfe17", "929c47677cfc461086309a2593caba39", "7b32f69c5dc94c808d1ffe267596c74b"],
			"banner_pic": "69d749ebec5f49c89713d92a6fac2e9d",
			"price": ["99", "88"],
			"join_together": true,
			"together_info": {
				"price": "66",
				"num": "5",
				"hour": "8",
				"vcount": "50000"
			},
			"groups": [{
				"title": "规格1",
				"price1": "66",
				"price2": "44",
				"logo": "http://tmp/wx1aebecae797c6598.o6zAJs4t197FbNSzH8Eev4ZO_ETk.65196005472a5bd971a0d6ef5ce8f08b.jpg",
				"logoKey": "e1137cc427f24eaa91ef8f1f578e57c7"
			}, {
				"title": "规格2",
				"price1": "77",
				"price2": "33",
				"logo": "http://tmp/wx1aebecae797c6598.o6zAJs4t197FbNSzH8Eev4ZO_ETk.7e9b9583a5f1ec932b0c9c21747fda7a.jpg",
				"logoKey": "db6d15dcd99644bfb38e11271a1dfe17"
			}],
			"contact_mobile": "13726759844",
			"contact_wx": "55555",
			"text_details": [{
				"value": "文字详情111"
			}, {
				"value": "文字详情22"
			}, {
				"value": "文字详情33"
			}],
			"pic_details": ["13a7bed6e60b4a5783c19b32401f4c43", "07467e15c5554808b64eb413cb030aa7"]
		}
	 * @param session
	 * @param productInfo
	 */
	public static void saveProduct(){
		try{
			String bodyStr = Http.Request.current().params.get("body");
			JsonObject requestBody = (new JsonParser()).parse(bodyStr).getAsJsonObject();
			String session = requestBody.has("session")?requestBody.get("session").getAsString():"";
			String productInfo = requestBody.has("productInfo")?requestBody.get("productInfo").getAsString():"";
			UsersDDL user = UserService.findBySession(session);
			if(user==null){
				renderJSON(RtnUtil.returnLoginFail());
			}
			if(user.getIsSeller()==null || user.getIsSeller() !=1){
				renderJSON(RtnUtil.returnFail("您还不是店小二"));
			}
		
			String decodeProductInfo = URLDecoder.decode(productInfo, "utf-8");
			
			Logger.info("开始建立商品数据库，%s", decodeProductInfo);
			ProductInfoDto product = gson.fromJson(decodeProductInfo, ProductInfoDto.class);
			String productId = ShopProductService.saveProduct(product, user);
			Map<String,Object> result = new HashMap<String,Object>();
			result.put("productId",productId);
			Logger.info("建立商品数据库完成，%s", decodeProductInfo);
			
			renderJSON(RtnUtil.returnSuccess("ok",result));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail(e.getMessage()));
		}
	}
	
	/**
	 * 旧的查询接口,废弃
	 * @param session
	 * @param productId
	 */
	
	public static void findProduct(String session,String productId){
		try{
			UsersDDL user = UserService.findBySession(session);
			if(user==null){
				renderJSON(RtnUtil.returnLoginFail());
			}
			if(user.getIsSeller()==null || user.getIsSeller() !=1){
				renderJSON(RtnUtil.returnFail("您还不是店小二"));
			}
			ShopProductDDL product = ShopProductService.getByProductId(productId);
			if(product==null || product.getSellerUserId().intValue() != user.getId()){
				throw new Exception("纳尼，非法");
			}
 			//组装商品信息
			ProductInfoDto dto = new ProductInfoDto();
			
			Image banner = new ProductInfoDto().new Image();			
			banner.remoteUrl =  API.getObjectAccessUrlSimple( product.getProductBanner());
			dto.banner_pic = banner;
			
 			dto.contact_mobile = product.getSellerTelNumber();
			dto.contact_wx = product.getSellerWxNumber();
			dto.join_together = (product.getJoinTogether()!=null && product.getJoinTogether()==1)?true:false;
			dto.price[0] = String.valueOf(AmountUtil.f2y(product.getProductOriginAmount()));
			dto.price[1] = String.valueOf(AmountUtil.f2y(product.getProductNowAmount()));
			dto.productId = product.getProductId();
			dto.productType = product.getProductType();
			dto.store = product.getStore();
			dto.title = product.getProductName();
			//处理团信息
			if(product.getJoinTogether() != null && product.getJoinTogether() == 1){
				TogetherInfo tinfo = new ProductInfoDto().new TogetherInfo();
				tinfo.hour = product.getTogetherExpirHour();
				tinfo.num = product.getTogetherNumber();
				tinfo.price = String.valueOf(AmountUtil.f2y( product.getProductTogetherAmount()));
				tinfo.vcount = product.getTogetherSales();
				dto.together_info = tinfo;
			}
			//处理商品详情
			List<TextDetail> textDetails = new ArrayList<TextDetail>();
			if(!StringUtils.isEmpty(product.getProductDesc())){
				for(String desc : product.getProductDesc().split("`")){
					TextDetail text = new ProductInfoDto().new TextDetail();
					text.value = desc;
					textDetails.add(text);
				}
			}
			dto.text_details = textDetails;
			//处理商品组
			List<Group> groupList = new ArrayList<Group>();
			List<ShopProductGroupDDL> groups = ShopProductGroupService.findByProductId(productId);
			for(ShopProductGroupDDL g : groups){
				Group group = new ProductInfoDto().new Group();
				group.remoteUrl = API.getObjectAccessUrlSimple( g.getGroupImage());
				group.osskey = g.getGroupImage();
				group.price1 = String.valueOf(AmountUtil.f2y( g.getGroupPrice()));
				group.price2 = String.valueOf(AmountUtil.f2y( g.getGroupTogetherPrice()));
				group.title = g.getGroupName();
				groupList.add(group);
			}
			dto.groups = groupList;
			//处理分类
			ShopProductCategoryRelDDL categoryRel = ShopCategoryService.listByProductId(productId).get(0);
			dto.p_category_id = categoryRel.getPCategoryId();
			dto.sub_category_id = categoryRel.getSubCategoryId();
			//处理轮播图片
			List<Image> playList = new ArrayList<Image>();
			List<ShopProductImagesDDL> plays = ShopProductImageService.listImages(productId, ShopProductImageService.SCREENSHOT_TYPE, 0, 6);
			for(ShopProductImagesDDL img : plays){
				Image ig = new ProductInfoDto().new Image();
				ig.remoteUrl = API.getObjectAccessUrlSimple( img.getImageKey());
				playList.add(ig);  
			}
			dto.play_pics = playList;
			//处理详情图片
			List<Image> detailPicsList = new ArrayList<Image>();
			List<ShopProductImagesDDL> details = ShopProductImageService.listImages(productId, ShopProductImageService.DETAIL_TYPE, 0, 12);
			if(details!=null && details.size()>0){
				for(ShopProductImagesDDL img : details){
					Image ig = new ProductInfoDto().new Image();
					ig.remoteUrl = API.getObjectAccessUrlSimple( img.getImageKey());
					detailPicsList.add(ig); 
				}
			}
			dto.pic_details = detailPicsList;	
			//处理属性
			List<ShopProductAttrRelDDL> attrs = ShopProductAttrService.listByProduct(productId);
			if(attrs!=null && attrs.size() > 0){
				dto.attrs_1 = attrs.get(0).getAttrId();
			}
			if(attrs!=null && attrs.size() > 1){
				dto.attrs_2 = attrs.get(1).getAttrId();
			}
			if(attrs!=null && attrs.size() > 2){
				dto.attrs_3 = attrs.get(2).getAttrId();
			}
			Logger.info("查询商品结果：%s", gson.toJson(dto));
			renderJSON(RtnUtil.returnSuccess("ok",dto));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail(e.getMessage()));
		}
	}
	
	public static void upProductOnSell(String session,String productId){
		try{
			
			UsersDDL user = UserService.findBySession(session);
			if(user==null){
				renderJSON(RtnUtil.returnLoginFail());
			}
			if(user.getIsSeller()==null || user.getIsSeller() !=1){
				renderJSON(RtnUtil.returnFail("您还不是店小二"));
			}
			ShopProductDDL product = ShopProductService.getByProductId(productId);
			if(product==null || product.getSellerUserId().intValue() != user.getId()){
				throw new Exception("纳尼，非法");
			}
			//上架商品
			ShopProductService.updateStatus(product.getProductId(), ShopProductService.Status.PRODUCT_STATUS_SELL.getValue());
			renderJSON(RtnUtil.returnSuccess("ok"));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail(e.getMessage()));
		}
	}
	
	public static void listMyProduct(String session,String keyword,int status,int page,int pageSize){
		try{
			UsersDDL user = UserService.findBySession(session);
			if(user==null){
				renderJSON(RtnUtil.returnLoginFail());
			}
			if(user.getIsSeller()==null || user.getIsSeller() !=1){
				renderJSON(RtnUtil.returnNotSeller("还没成为商家"));
			}
			List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
			List<ShopProductDDL>  list = ShopProductService.listMyProduct(user.getId().intValue(), keyword, status, page, pageSize);
			if( list == null || list.size() == 0 ) {
				renderJSON(RtnUtil.returnSuccess("ok", result));
			}
			for(ShopProductDDL p : list){
				Map<String,Object> one = new HashMap<String,Object>();
				one.put("productId", p.getProductId());
				one.put("produtName", p.getProductName());
				one.put("status", p.getStatus());
				one.put("createTime", DateUtil.format(p.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
				result.add(one);
			}
			renderJSON(RtnUtil.returnSuccess("ok",result));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail(e.getMessage()));
		}
	}
	
	public static void apply(/*String session,String frontCardKey,String backCardKey,String mobile,int code,String sellerWx*/){
	
		try{
			String bodyStr = Http.Request.current().params.get("body");
			JsonObject requestBody = (new JsonParser()).parse(bodyStr).getAsJsonObject();
			String session = requestBody.has("session")?requestBody.get("session").getAsString():"";
			String frontCardKey = requestBody.has("frontCardKey")?requestBody.get("frontCardKey").getAsString():"";
			String backCardKey = requestBody.has("backCardKey")?requestBody.get("backCardKey").getAsString():"";
			String mobile = requestBody.has("mobile")?requestBody.get("mobile").getAsString():"";
			int code = requestBody.has("code")?requestBody.get("code").getAsInt():0;
			String sellerWx = requestBody.has("sellerWx")?requestBody.get("sellerWx").getAsString():"";
			
			
			UsersDDL user = UserService.findBySession(session);
			
			if(user==null){
				renderJSON(RtnUtil.returnLoginFail());
			}
			
			if(user.getIsSeller() !=null && user.getIsSeller() ==1){
				renderJSON(RtnUtil.returnFail("你已经入住过"));
			}
			
			boolean authSms = SmsService.validateSmsCode(mobile, code);
			if( !authSms ){
				renderJSON(RtnUtil.returnFail("短信验证失败"));
			}
			
			ApplyService.apply(frontCardKey, backCardKey, mobile, sellerWx, user.getId().intValue(), 2);
			renderJSON(RtnUtil.returnSuccess());
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail("服务器异常"));
		}
	}
	
	public static void applyInfo(String session){
		try{
			 
			UsersDDL user = UserService.findBySession(session);
			
			if(user==null){
				renderJSON(RtnUtil.returnLoginFail());
			} 
			Map<String,Object> result = new HashMap<String,Object>();
			ShopApplyInfoDDL applyInfo = ApplyService.getApplyInfo(user.getId().intValue());
			if(applyInfo==null){
				result.put("isSeller", false);
				renderJSON(RtnUtil.returnSuccess("ok",result));
			}			
			result.put("isSeller", true);
			result.put("backCardUrl",API.getObjectAccessUrlSimple( applyInfo.getBackCardKey()));
			result.put("frontCardUrl",API.getObjectAccessUrlSimple( applyInfo.getFrontCardKey()));
			result.put("mobile", applyInfo.getMobile());
			result.put("wx", applyInfo.getWx());
			renderJSON(RtnUtil.returnSuccess("ok",result));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail("服务器异常"));
		}
	}
	
	public static void deliver(String session,String orderId,String expressName,String expressCode,String expressOrderCode){
		try{
			 
			UsersDDL user = UserService.findBySession(session);
			
			if(user==null){
				renderJSON(RtnUtil.returnLoginFail());
			} 
			
			ShopOrderDDL order = ShopOrderService.findByOrderId(orderId);
			
			if(order==null){
				renderJSON(RtnUtil.returnFail("订单不存在"));
			}
			ShopExpressDDL express = ShopExpressService.getByOrderId(orderId);
			boolean result = ShopExpressService.commitShipper(express, expressOrderCode, expressCode, expressName);
			if(result){
				order.setStatus(ShopOrderService.ORDER_DELIVERED);
				if(Dal.update(order, "ShopOrderDDL.status", new Condition("ShopOrderDDL.orderId","=",orderId))>0){
					renderJSON(RtnUtil.returnSuccess());
				}else{
					renderJSON(RtnUtil.returnFail("订单更新状态失败"));
				}
				
			}else{
				renderJSON(RtnUtil.returnFail());
			}
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail("服务器异常"));
		}
	}
	
	public static void categoryALL(){
		try{
			SelectSourceDto selectSource = new SelectSourceDto();
			selectSource.selected="";
			
 			List<ShopProductCategoryDDL> pList = ShopCategoryService.searchByPCategoryName(null);
			if(pList!=null && pList.size()>0){
				for(ShopProductCategoryDDL pcat : pList){
					
					SelectSourceDto.Soruce source = new  SelectSourceDto().new Soruce(); 
					source.value = pcat.getCategoryId();
					source.text = pcat.getCategoryName(); 
					
 					List<ShopProductCategoryChildDDL> childCats = ShopCategoryService.listByParentId(pcat.getCategoryId());
					if(childCats!=null && childCats.size()>0){		
						SelectSourceDto subSelectSource = new SelectSourceDto();
						for(ShopProductCategoryChildDDL child : childCats){							
							subSelectSource.selected="0";
							SelectSourceDto.Soruce subSource = new  SelectSourceDto().new Soruce(); 
							subSource.value = child.getCategoryId();
							subSource.text = child.getCategoryName();  
							subSelectSource.options.add(subSource); 
						}
						source.subCategory = subSelectSource;
					} 
					selectSource.options.add(source);
				}
			}
			renderJSON(RtnUtil.returnSuccess("OK", selectSource));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail("服务器异常"));
		}
	}
	
	public static void loginMng(String userName,String password){
		try{
			if(StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)){
				renderJSON(RtnUtil.returnFail("用户或密码不正确"));
			}
			ShopMngSessionDDL session = ShopMngService.login(userName, password);
			if(session!=null){
				response.setCookie("shop_sid", session.getSession(), null, null,6*60*60, false, true);
				renderJSON(RtnUtil.returnSuccess("OK", session));
			}else{
				renderJSON(RtnUtil.returnFail("用户或密码不正确"));
			}
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail("服务器异常"));
		}
	}
	
	public static void checkSession(){
		try{
			if(request.cookies==null || !request.cookies.containsKey("shop_sid")){
				renderJSON(RtnUtil.returnFail("未登录"));
			}
			String sessionStr = request.cookies.get("shop_sid").value;
			ShopMngSessionDDL session = ShopMngService.checkSession(sessionStr);
			if(session!=null){				 
				renderJSON(RtnUtil.returnSuccess("OK", session));
			}else{
				renderJSON(RtnUtil.returnFail("未登录"));
			}
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail("服务器异常"));
		}
	}
	
	public static void quitLogin(){
		try{
			if(request.cookies==null || !request.cookies.containsKey("shop_sid")){
				renderJSON(RtnUtil.returnSuccess("OK"));
			}
			response.setCookie("shop_sid", "", null, null,0, false, true);
			renderJSON(RtnUtil.returnSuccess("OK"));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail("服务器异常"));
		}
	}
	
	public static void saveShopIndex(){
		try{
			if(request.cookies==null || !request.cookies.containsKey("shop_sid")){
				renderJSON(RtnUtil.returnFail("未登录"));
			}			
			String sessionStr = request.cookies.get("shop_sid").value;
			ShopMngSessionDDL session = ShopMngService.checkSession(sessionStr);
			if(session==null){				 
				renderJSON(RtnUtil.returnFail("未登录"));
			}
			String bodyStr = Http.Request.current().params.get("body");			
			String decodeBodyStr = URLDecoder.decode(bodyStr, "utf-8");	 

			Logger.info("解析店铺配置，%s", decodeBodyStr);
			ShopIndexDto index = gson.fromJson(decodeBodyStr, ShopIndexDto.class);
			if(ShopIndexService.update(index.shopId, index.shopName, index.shopAvatarKey,decodeBodyStr)){
				renderJSON(RtnUtil.returnSuccess("OK"));
			}
			Logger.info("解析店铺配置完成，%s", decodeBodyStr);
			renderJSON(RtnUtil.returnFail("商铺配置失败"));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail("服务器异常"));
		}
	}
	
	/**
	 * 店铺后台
	 */
	public static void saveProductInfo(){
		try{
			if(request.cookies==null || !request.cookies.containsKey("shop_sid")){
				renderJSON(RtnUtil.returnFail("未登录"));
			}			
			String sessionStr = request.cookies.get("shop_sid").value;
			ShopMngSessionDDL session = ShopMngService.checkSession(sessionStr);
			if(session==null){				 
				renderJSON(RtnUtil.returnFail("未登录"));
			}
			String bodyStr = Http.Request.current().params.get("body");			
			String decodeBodyStr = URLDecoder.decode(bodyStr, "utf-8");	 

			Logger.info("解析商品数据，%s", decodeBodyStr);
			ProductInfoDto product = gson.fromJson(decodeBodyStr, ProductInfoDto.class);
			
			String productId = ShopProductService.saveProductInfo(product);
			Map<String,Object> result = new HashMap<String,Object>();
			result.put("productId",productId);
			Logger.info("解析商品数据完成，%s", decodeBodyStr);
			
			renderJSON(RtnUtil.returnSuccess("OK", result));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail("服务器异常"));
		}
	}
	/**
	 * PC后台查商品
	 * @param productId
	 */
	public static void getOneProduct(String productId){
		try{
			
			if(request.cookies==null || !request.cookies.containsKey("shop_sid")){
				renderJSON(RtnUtil.returnFail("未登录"));
			}			
			String sessionStr = request.cookies.get("shop_sid").value;
			ShopMngSessionDDL session = ShopMngService.checkSession(sessionStr);
			if(session==null){				 
				renderJSON(RtnUtil.returnFail("未登录"));
			} 
			 
			ShopProductDDL product = ShopProductService.getByProductId(productId);			
 			//组装商品信息
			ProductInfoDto dto = new ProductInfoDto();
			dto.isHot = product.getIsHot()==1?true:false;
			dto.isSale = product.getIsSale()==1?true:false;
			
			Image banner = new ProductInfoDto().new Image();			
			banner.remoteUrl =  API.getObjectAccessUrlSimple(product.getProductBanner());
			banner.osskey = product.getProductBanner();
			dto.banner_pic = banner;
			
 			dto.contact_mobile = product.getSellerTelNumber();
			dto.contact_wx = product.getSellerWxNumber();
			
			dto.join_together = (product.getJoinTogether()!=null && product.getJoinTogether()==1)?true:false;
			dto.price[0] = String.valueOf(AmountUtil.f2y(product.getProductOriginAmount()));
			dto.price[1] = String.valueOf(AmountUtil.f2y(product.getProductNowAmount()));
			dto.productId = product.getProductId();
			dto.productType = product.getProductType();
			dto.store = product.getStore();
			dto.title = product.getProductName();
			//处理团信息
			if(dto.join_together ){
				TogetherInfo tinfo = new ProductInfoDto().new TogetherInfo();
				tinfo.hour = product.getTogetherExpirHour();
				tinfo.num = product.getTogetherNumber();
				tinfo.price = String.valueOf(AmountUtil.f2y( product.getProductTogetherAmount()));
				tinfo.vcount = product.getTogetherSales();
				dto.together_info = tinfo;
			}
			//处理商品详情
			List<TextDetail> textDetails = new ArrayList<TextDetail>();
			if(!StringUtils.isEmpty(product.getProductDesc())){
				for(String desc : product.getProductDesc().split("`")){
					TextDetail text = new ProductInfoDto().new TextDetail();
					text.value = desc;
					textDetails.add(text);
				}
			}
			dto.text_details = textDetails;
			//处理商品组
			List<Group> groupList = new ArrayList<Group>();
			List<ShopProductGroupDDL> groups = ShopProductGroupService.findByProductId(productId);
			 
			for(ShopProductGroupDDL g : groups){
				//过滤默认的商品分组数据
				if(g.getOrderBy() == 0){
					continue;
				}
				Group group = new ProductInfoDto().new Group();
				group.remoteUrl = API.getObjectAccessUrlSimple(g.getGroupImage());
				group.osskey = g.getGroupImage();
				group.price1 = String.valueOf(AmountUtil.f2y( g.getGroupPrice()));
				group.price2 = String.valueOf(AmountUtil.f2y( g.getGroupTogetherPrice()));
				group.title = g.getGroupName();
				groupList.add(group);
			}
			dto.groups = groupList;
			//处理分类
			List<Category> selectedCategoryParams = new ArrayList<Category>();
			List<ShopProductCategoryRelDDL> categoryRelList = ShopCategoryService.listByProductId(productId);
			if(categoryRelList!=null && categoryRelList.size()>0){
				for(ShopProductCategoryRelDDL rel : categoryRelList){
					Category categroy = new ProductInfoDto().new Category();
					categroy.pCategoryId = rel.getPCategoryId();
					categroy.subCategoryId = rel.getSubCategoryId();
					selectedCategoryParams.add(categroy);
				}
			}
			dto.selectedCategoryParams = selectedCategoryParams;
			 
			
			//处理轮播图片
			List<Image> playList = new ArrayList<Image>();
			List<ShopProductImagesDDL> plays = ShopProductImageService.listImages(productId, ShopProductImageService.SCREENSHOT_TYPE, 0, 6);
			for(ShopProductImagesDDL img : plays){
				Image ig = new ProductInfoDto().new Image();
				ig.remoteUrl = API.getObjectAccessUrlSimple(img.getImageKey());
				ig.osskey=img.getImageKey();
				playList.add(ig);  
			}
			dto.play_pics = playList;
			//处理详情图片
			List<Image> detailPicsList = new ArrayList<Image>();
			List<ShopProductImagesDDL> details = ShopProductImageService.listImages(productId, ShopProductImageService.DETAIL_TYPE, 0, 12);
			if(details!=null && details.size()>0){
				for(ShopProductImagesDDL img : details){
					Image ig = new ProductInfoDto().new Image();
					ig.remoteUrl = API.getObjectAccessUrlSimple(img.getImageKey());
					ig.osskey = img.getImageKey();
					detailPicsList.add(ig); 
				}
			}
			dto.pic_details = detailPicsList;	
			//处理属性
			List<String> selectedAttrs = new ArrayList<String>();
			List<ShopProductAttrRelDDL> attrs = ShopProductAttrService.listByProduct(productId);
			if(attrs!=null && attrs.size() > 0){
				for(ShopProductAttrRelDDL attr : attrs){
					selectedAttrs.add(attr.getAttrName());
				}
			}
			dto.selectedAttrs = selectedAttrs;
			 
			Logger.info("查询商品结果：%s", gson.toJson(dto));
			renderJSON(RtnUtil.returnSuccess("ok",dto));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail(e.getMessage()));
		}
	}
	
	public static void operatedProduct(int flag,String productIds){
		try{
			if(request.cookies==null || !request.cookies.containsKey("shop_sid")){
				renderJSON(RtnUtil.returnFail("未登录"));
			}			
			String sessionStr = request.cookies.get("shop_sid").value;
			ShopMngSessionDDL session = ShopMngService.checkSession(sessionStr);
			if(session==null){				 
				renderJSON(RtnUtil.returnFail("未登录"));
			}
			
			if(StringUtils.isEmpty(productIds)){
				renderJSON(RtnUtil.returnFail("商品id参数非法"));
			}
			
			for(String productId : productIds.split(",")){
				ShopProductDDL product = ShopProductService.getByProductId(productId);
				if(product==null){
					renderJSON(RtnUtil.returnFail("商品不存在"));
				} 
				
				if(flag==1){
					if(product.getStatus() == ShopProductService.Status.PRODUCT_STATUS_SELL.getValue()){
						ShopProductService.updateStatus(productId, ShopProductService.Status.PRODUCT_STATUS_UNSELL.getValue());
					}else{
						ShopProductService.updateStatus(productId, ShopProductService.Status.PRODUCT_STATUS_SELL.getValue());
					}
					 
				}else if(flag==2){
					if(product.getIsHot() == 1){
						product.setIsHot(0);
					}else{
						product.setIsHot(1);
					}
					ShopProductService.updateHot(product);					 
				}else if(flag==3){
					if(product.getIsSale() == 1){
						product.setIsSale(0);
					}else{
						product.setIsSale(1);
					}
					ShopProductService.updateSale(product);					
				}
			}
			
			renderJSON(RtnUtil.returnSuccess("OK"));
			
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail("服务器异常"));
		}
	}
	
	public static void addPCategory(String text){
		try{
			if(request.cookies==null || !request.cookies.containsKey("shop_sid")){
				renderJSON(RtnUtil.returnFail("未登录"));
			}			
			String sessionStr = request.cookies.get("shop_sid").value;
			ShopMngSessionDDL session = ShopMngService.checkSession(sessionStr);
			if(session==null){				 
				renderJSON(RtnUtil.returnFail("未登录"));
			}
			
			if(StringUtils.isEmpty(text)){
				renderJSON(RtnUtil.returnFail("分类名称为空"));
			}
			
			ShopProductCategoryDDL p = ShopCategoryService.createPCategory(text);
			if(p!=null)
				renderJSON(RtnUtil.returnSuccess("OK"));
			else
				renderJSON(RtnUtil.returnFail("新增分类失败"));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail("服务器异常"));
		}
	}
	
	public static void savePCategory(String text,String pid){
		try{
			if(request.cookies==null || !request.cookies.containsKey("shop_sid")){
				renderJSON(RtnUtil.returnFail("未登录"));
			}			
			String sessionStr = request.cookies.get("shop_sid").value;
			ShopMngSessionDDL session = ShopMngService.checkSession(sessionStr);
			if(session==null){				 
				renderJSON(RtnUtil.returnFail("未登录"));
			}
			
			if(StringUtils.isEmpty(text)){
				renderJSON(RtnUtil.returnFail("分类名称为空"));
			}
			if(StringUtils.isEmpty(pid)){
				renderJSON(RtnUtil.returnFail("分类ID为空"));
			}
			
			ShopCategoryService.updatePCategoryName(pid, text);
			renderJSON(RtnUtil.returnSuccess("OK"));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail("服务器异常"));
		}
	}
	
	public static void saveSubCategory(String text,String subId){
		try{
			if(request.cookies==null || !request.cookies.containsKey("shop_sid")){
				renderJSON(RtnUtil.returnFail("未登录"));
			}			
			String sessionStr = request.cookies.get("shop_sid").value;
			ShopMngSessionDDL session = ShopMngService.checkSession(sessionStr);
			if(session==null){				 
				renderJSON(RtnUtil.returnFail("未登录"));
			}
			
			if(StringUtils.isEmpty(text)){
				renderJSON(RtnUtil.returnFail("分类名称为空"));
			}
			if(StringUtils.isEmpty(subId)){
				renderJSON(RtnUtil.returnFail("分类ID为空"));
			}
			
			ShopCategoryService.updateSubCategoryName(subId, text);
			renderJSON(RtnUtil.returnSuccess("OK"));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail("服务器异常"));
		}
	}
	
	public static void delPCategory(String pid){
		try{
			if(request.cookies==null || !request.cookies.containsKey("shop_sid")){
				renderJSON(RtnUtil.returnFail("未登录"));
			}			
			String sessionStr = request.cookies.get("shop_sid").value;
			ShopMngSessionDDL session = ShopMngService.checkSession(sessionStr);
			if(session==null){				 
				renderJSON(RtnUtil.returnFail("未登录"));
			}
			
			if(StringUtils.isEmpty(pid)){
				renderJSON(RtnUtil.returnFail("分类为空"));
			}
			
			 ShopCategoryService.delPCategory(pid);
			 
			 renderJSON(RtnUtil.returnSuccess("OK"));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail("服务器异常"));
		}
	}
	
	public static void addSubCategory(String text,String pCategoryId){
		try{
			if(request.cookies==null || !request.cookies.containsKey("shop_sid")){
				renderJSON(RtnUtil.returnFail("未登录"));
			}			
			String sessionStr = request.cookies.get("shop_sid").value;
			ShopMngSessionDDL session = ShopMngService.checkSession(sessionStr);
			if(session==null){				 
				renderJSON(RtnUtil.returnFail("未登录"));
			}
			
			if(StringUtils.isEmpty(text)){
				renderJSON(RtnUtil.returnFail("分类名称为空"));
			}
			if(StringUtils.isEmpty(pCategoryId)){
				renderJSON(RtnUtil.returnFail("分类父ID为空"));
			}
			
			ShopProductCategoryChildDDL sub = ShopCategoryService.createChildCategory(text, pCategoryId);
			if(sub!=null)
				renderJSON(RtnUtil.returnSuccess("OK"));
			else
				renderJSON(RtnUtil.returnFail("新增分类失败"));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail("服务器异常"));
		}
	}
	
	public static void delSubCategory(String subId){
		try{
			if(request.cookies==null || !request.cookies.containsKey("shop_sid")){
				renderJSON(RtnUtil.returnFail("未登录"));
			}			
			String sessionStr = request.cookies.get("shop_sid").value;
			ShopMngSessionDDL session = ShopMngService.checkSession(sessionStr);
			if(session==null){				 
				renderJSON(RtnUtil.returnFail("未登录"));
			}
			
			if(StringUtils.isEmpty(subId)){
				renderJSON(RtnUtil.returnFail("分类为空"));
			}
			
			 ShopCategoryService.delSubCategory(subId);
			 
			 renderJSON(RtnUtil.returnSuccess("OK"));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail("服务器异常"));
		}
	}
	
	public static void changeCategoryOrder(String pid1,String pid2){
		try{
			if(request.cookies==null || !request.cookies.containsKey("shop_sid")){
				renderJSON(RtnUtil.returnFail("未登录"));
			}			
			String sessionStr = request.cookies.get("shop_sid").value;
			ShopMngSessionDDL session = ShopMngService.checkSession(sessionStr);
			if(session==null){				 
				renderJSON(RtnUtil.returnFail("未登录"));
			}
			
			 
			if(StringUtils.isEmpty(pid2) || StringUtils.isEmpty(pid1)){
				renderJSON(RtnUtil.returnFail("分类ID为空"));
			}
			
			ShopCategoryService.changePCategoryOrder(pid1, pid2);
			renderJSON(RtnUtil.returnSuccess("OK"));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail("服务器异常"));
		}
	}
	
	
	public static void listOrder(String orderId,String keyword,int status,int page,int pageSize){
		try{
			if(request.cookies==null || !request.cookies.containsKey("shop_sid")){
				renderJSON(RtnUtil.returnFail("未登录"));
			}			
			String sessionStr = request.cookies.get("shop_sid").value;
			ShopMngSessionDDL session = ShopMngService.checkSession(sessionStr);
			if(session==null){				 
				renderJSON(RtnUtil.returnFail("未登录"));
			}
			
			List<Map<String,Object>> orders = new ArrayList<Map<String,Object>>();
			Map<String,Object> response = new HashMap<String,Object>();
			
			int total = ShopOrderService.countMngOrder(orderId, keyword, status);
			response.put("total", total);
			response.put("pageTotal", Math.ceil(total/(double)pageSize));
			 
			if(total == 0){
				response.put("list", orders);
				renderJSON(RtnUtil.returnSuccess("OK",response));
			}			
			
			
			page = page==0?1:page;
			pageSize = pageSize==0?10:pageSize;
			
			List<ShopOrderDDL> list = ShopOrderService.listMngOrder(orderId, keyword, status, page, pageSize);
			
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
				map.put("togetherId", order.getTogetherId()==null?"":order.getTogetherId());
				if(order.getTogetherId()!=null){
					map.put("totalPay", AmountUtil.f2y(order.getGroupTogetherPrice()*order.getBuyNum()));
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
				map.put("payTime", order.getPayTime()!=null?DateUtil.format(order.getPayTime()):"");
				orders.add(map);
			}
			response.put("list", orders);
			renderJSON(RtnUtil.returnSuccess("OK", response));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail("服务器异常"));
		}
	}
	
	public static void listCoupon(String couponId,String keyword,int page,int pageSize){
		try{
			if(request.cookies==null || !request.cookies.containsKey("shop_sid")){
				renderJSON(RtnUtil.returnFail("未登录"));
			}			
			String sessionStr = request.cookies.get("shop_sid").value;
			ShopMngSessionDDL session = ShopMngService.checkSession(sessionStr);
			if(session==null){				 
				renderJSON(RtnUtil.returnFail("未登录"));
			}
			
			List<Map<String,Object>> coupons = new ArrayList<Map<String,Object>>();
			Map<String,Object> response = new HashMap<String,Object>();
			
			int total = ShopCouponMngService.countCoupon(couponId, keyword);
			response.put("total", total);
			response.put("pageTotal", Math.ceil(total/(double)pageSize));
			 
			if(total == 0){
				response.put("list", coupons);
				renderJSON(RtnUtil.returnSuccess("OK",response));
			}			
			
			
			page = page==0?1:page;
			pageSize = pageSize==0?10:pageSize;
			
			
			List<ShopCouponMngDDL>  list = ShopCouponMngService.listCoupon(couponId, keyword, page, pageSize);
			
 			
			for(ShopCouponMngDDL coupon : list){
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("couponId",coupon.getCouponId());
				map.put("couponName", coupon.getCouponName());
				map.put("createTime", DateUtil.format(coupon.getCreateTime()) );
				map.put("endTime", DateUtil.format(coupon.getEndTime()));
				map.put("expireTime", DateUtil.format(coupon.getExpireTime()));
				map.put("id", coupon.getId());
				map.put("amount", AmountUtil.f2y(coupon.getAmount()));
				map.put("limitPrice", coupon.getLimitPrice()==null?"":AmountUtil.f2y(coupon.getLimitPrice()));
				map.put("limitProductId", coupon.getLimitProductId());
				//map.put("limitSellerId", coupon.getLimitSellerId());
				map.put("limitTimes", coupon.getLimitTimes());
				map.put("startTime", DateUtil.format(coupon.getStartTime()));
				 
				coupons.add(map);
			}
			response.put("list", coupons);
			renderJSON(RtnUtil.returnSuccess("OK", response));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail("服务器异常"));
		}
	}
	
	
	public static void addCoupon(String couponId,String couponName,String amount,String limitProductId,
			String limitPrice,int limitTimes,String expireTime,String startTime,String endTime){
		try{
			if(request.cookies==null || !request.cookies.containsKey("shop_sid")){
				renderJSON(RtnUtil.returnFail("未登录"));
			}			
			String sessionStr = request.cookies.get("shop_sid").value;
			ShopMngSessionDDL session = ShopMngService.checkSession(sessionStr);
			if(session==null){				 
				renderJSON(RtnUtil.returnFail("未登录"));
			}
			
			ShopCouponMngService.replace(couponId, couponName,AmountUtil.y2f(amount) ,
					limitProductId, 0, AmountUtil.y2f(limitPrice), limitTimes, 
					DateUtil.getTime(expireTime), DateUtil.getTime(startTime), DateUtil.getTime(endTime));
			renderJSON(RtnUtil.returnSuccess("OK"));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail("服务器异常"));
		}
	}
	
	
	public static void deliverMng(String orderId,String expressCode,String expressOrderCode){
		try{
			 
			if(request.cookies==null || !request.cookies.containsKey("shop_sid")){
				renderJSON(RtnUtil.returnFail("未登录"));
			}			
			String sessionStr = request.cookies.get("shop_sid").value;
			ShopMngSessionDDL session = ShopMngService.checkSession(sessionStr);
			if(session==null){				 
				renderJSON(RtnUtil.returnFail("未登录"));
			}
			
			ShopOrderDDL order = ShopOrderService.findByOrderId(orderId);
			
			if(order==null){
				renderJSON(RtnUtil.returnFail("订单不存在"));
			}
			ShopExpressDDL express = ShopExpressService.getByOrderId(orderId);
			
			if(express==null){
				renderJSON(RtnUtil.returnFail("不存快递初始化信息"));
			}
			 
			ShopExpressCodeDDL shipper = ShopExpressService.getByCode(expressCode);
			boolean result = ShopExpressService.commitShipper(express, expressOrderCode, expressCode, shipper.getShipperName());
			if(result){
				order.setStatus(ShopOrderService.ORDER_DELIVERED);
				if(Dal.update(order, "ShopOrderDDL.status", new Condition("ShopOrderDDL.orderId","=",orderId))>0){
					renderJSON(RtnUtil.returnSuccess());
				}else{
					renderJSON(RtnUtil.returnFail("订单更新状态失败"));
				}
				
			}else{
				renderJSON(RtnUtil.returnFail("更新shipper失败"));
			}
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail("服务器异常"));
		}
	}
	
	
}
