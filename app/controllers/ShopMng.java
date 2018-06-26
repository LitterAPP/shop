package controllers;

import java.io.ByteArrayInputStream;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import dto.shop.AddressDto;
import dto.shop.ShopIndexDto;
import dto.shop.ShopIndexDto.ShopNavWrap;
import dto.shop.ShopNavDto;
import jws.Jws;
import jws.Logger;
import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import jws.mvc.Controller;
import jws.mvc.Http;
import modules.common.ddl.FormIdsDDL;
import modules.common.service.FormIdService;
import modules.shop.ddl.ShopApplyInfoDDL;
import modules.shop.ddl.ShopCouponMngDDL;
import modules.shop.ddl.ShopExpressCodeDDL;
import modules.shop.ddl.ShopExpressDDL;
import modules.shop.ddl.ShopIndexDDL;
import modules.shop.ddl.ShopMngSessionDDL;
import modules.shop.ddl.ShopMngUserDDL;
import modules.shop.ddl.ShopOrderDDL;
import modules.shop.ddl.ShopProductAttrDDL;
import modules.shop.ddl.ShopProductAttrRelDDL;
import modules.shop.ddl.ShopProductCategoryChildDDL;
import modules.shop.ddl.ShopProductCategoryDDL;
import modules.shop.ddl.ShopProductCategoryRelDDL;
import modules.shop.ddl.ShopProductDDL;
import modules.shop.ddl.ShopProductGroupDDL;
import modules.shop.ddl.ShopProductImagesDDL;
import modules.shop.ddl.ShopRefundOrderDDL;
import modules.shop.ddl.ShopTogetherDDL;
import modules.shop.ddl.ShopWetaoDDL;
import modules.shop.ddl.UsersDDL;
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
import modules.shop.service.ShopRefundOrderService;
import modules.shop.service.ShopTogetherService;
import modules.shop.service.ShopWeTaoService;
import modules.shop.service.SmsService;
import modules.shop.service.UserService;
import modules.shop.service.dto.AutoCompleteDto;
import modules.shop.service.dto.OverViewDatasDto;
import util.API;
import util.AmountUtil;
import util.DateUtil;
import util.ExportData;
import util.RtnUtil;
import util.ThreadUtil;

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
	
	/*public static void categoryFirSource(String keyword){
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
	}*/
	
	/**
	 * 查询店铺首页配置
	 * @param shopId
	 */
	public static void getShopIndexConfig(){
		try{
			
			if(request.cookies==null || !request.cookies.containsKey("shop_sid")){
				renderJSON(RtnUtil.returnFail("未登录"));
			}			
			String sessionStr = request.cookies.get("shop_sid").value;
			ShopMngSessionDDL session = ShopMngService.checkSession(sessionStr);
			if(session==null){				 
				renderJSON(RtnUtil.returnFail("未登录"));
			}
			
			ShopIndexDDL shopIndex = ShopIndexService.getByShopId(session.getShopId());
			 
			if(shopIndex==null || StringUtils.isEmpty(shopIndex.getConfig())){
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
			
			for(ShopNavWrap wrap : shopIndexConfig.shopNavWrapList){
				for(ShopNavDto dto : wrap.list){
					dto.img = API.getObjectAccessUrlSimple(dto.imgkey);
				}
			}
			
			Map<String,Object> result = new HashMap<String,Object>();
			result.put("config", shopIndexConfig); 
			result.put("shopQRCode", API.getObjectAccessUrlSimple(session.getShopQrcodeKey()));
			renderJSON(RtnUtil.returnSuccess("OK",result));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail(e.getMessage()));
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
	
	public static void categoryALL(boolean force){
		try{ 
			if(request.cookies==null || !request.cookies.containsKey("shop_sid")){
				renderJSON(RtnUtil.returnFail("未登录"));
			}
			
			String sessionStr = request.cookies.get("shop_sid").value;
			ShopMngSessionDDL session = ShopMngService.checkSession(sessionStr);
			if(session==null){				 
				renderJSON(RtnUtil.returnFail("未登录"));
			}
			SelectSourceDto selectSource =  ShopCategoryService.reflushCategoryALL(session.getShopId(),force);
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
				response.setCookie("shop_sid", session.getSession(), null, "/",6*60*60, false, true);
				renderJSON(RtnUtil.returnSuccess("OK", session));
			}else{
				renderJSON(RtnUtil.returnFail("用户或密码不正确"));
			}
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail("服务器异常"));
		}
	}
	
	public static void reg(String userName,String password,String repassword,String mobile,String code){
		try{
			if(StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)){
				renderJSON(RtnUtil.returnFail("用户或密码不正确"));
			}
			
			if(!password.equals(repassword)){
				renderJSON(RtnUtil.returnFail("两次输入的密码不一致"));
			}
			ShopMngUserDDL user = ShopMngService.getByUserName(userName);
			if(user!=null){
				renderJSON(RtnUtil.returnFail("用户名已经存在"));
			}
			
			boolean validateSms = SmsService.validateSmsCode(mobile, Integer.parseInt(code));
			if(!validateSms){
				renderJSON(RtnUtil.returnFail("短信验证码错误"));
			}
			boolean created = ShopMngService.createUser(userName, mobile, password);
			 
			if(created){				 
				response.setCookie("shop_sid", "", null, null,6*60*60, false, true);
				renderJSON(RtnUtil.returnSuccess("OK"));
			}else{
				renderJSON(RtnUtil.returnFail("注册失败"));
			}
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail("服务器异常"));
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
	public static void listProduct(String productId,String keyword,String pCategoryId,String subCategoryId,
			boolean isSale,boolean isHot,int status,int orderBy,int joinSeckilling,int page,int pageSize){
		try{
			List<Map<String,Object>> mapList = new ArrayList<Map<String,Object>>();
			Map<String,Object> response = new HashMap<String,Object>();
			
			if(request.cookies==null || !request.cookies.containsKey("shop_sid")){
				renderJSON(RtnUtil.returnFail("未登录"));
			}
			
			String sessionStr = request.cookies.get("shop_sid").value;
			ShopMngSessionDDL session = ShopMngService.checkSession(sessionStr);
			if(session==null){				 
				renderJSON(RtnUtil.returnFail("未登录"));
			}
			
			int total = ShopProductService.countProduct(session.getShopId(),productId,keyword, pCategoryId, subCategoryId, isSale?1:0, isHot?1:0, status,joinSeckilling,-1);
			response.put("total", total);
			response.put("pageTotal", Math.ceil(total/(double)pageSize));
			 
			if(total == 0){
				response.put("list", mapList);
				renderJSON(RtnUtil.returnSuccess("OK",response));
			}			
			List<ShopProductDDL> list = ShopProductService.listProduct(session.getShopId(),productId,keyword, pCategoryId, subCategoryId, isSale?1:0, isHot?1:0, status,orderBy, joinSeckilling,-1,page<=0?1:page, pageSize<=0?10:pageSize);
			
			
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
				result.put("updateTime", DateUtil.format(p.getUpdateTime()));

				result.put("pv", p.getPv());
				result.put("deal", p.getDeal());
				result.put("isHot", p.getIsHot()==1);
				result.put("isSale", p.getIsSale()==1);
				
				if(p.getJoinSeckilling()!=null && p.getJoinSeckilling()==1){
					result.put("seckillingTime",p.getSeckillingTime());
					result.put("seckillingPrice", AmountUtil.f2y(p.getSeckillingPrice()));
				}
				result.put("joinSeckilling", p.getJoinSeckilling()==null?0:p.getJoinSeckilling());
				
				List<ShopTogetherDDL> togethers = ShopTogetherService.listByProductId(p.getProductId(), 1, 2);
				if( togethers != null && togethers.size() == 2){
					String[] together = new String[]{togethers.get(0).getMasterAvatar(),togethers.get(1).getMasterAvatar()};
					result.put("togethers", together);
				}	
				mapList.add(result);				
			}
			
			response.put("list", mapList);
			renderJSON(RtnUtil.returnSuccess("OK", response));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail(e.getMessage()));
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
			response.setCookie("shop_sid", "", null, "/",0, false, true);
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
			
			//处理链接，带上自己店铺的ID
			for(ShopNavDto dto : index.firstNavList){
				dto.url = appendShopId(dto.url,session.getShopId());
			}
			for(ShopNavDto dto : index.secondNavList){
				dto.url = appendShopId(dto.url,session.getShopId());
			}
			for(ShopNavDto dto : index.thirdNavList){
				dto.url = appendShopId(dto.url,session.getShopId());
			}
			for(ShopNavDto dto : index.fourthNavList){
				dto.url = appendShopId(dto.url,session.getShopId());
			}
			for(ShopNavDto dto : index.fiveNavList){
				dto.url = appendShopId(dto.url,session.getShopId());
			}
			for(ShopNavDto dto : index.swiperList){
				dto.url = appendShopId(dto.url,session.getShopId());
			}
			boolean createShop = ShopIndexService.update(session.getShopId(), 
					index.shopName, index.shopAvatarKey,
					index.contactMobile,index.contactWx,
					gson.toJson(index));
			if(createShop){
				renderJSON(RtnUtil.returnSuccess("OK"));
			}
			Logger.info("解析店铺配置完成，%s", decodeBodyStr);
			renderJSON(RtnUtil.returnFail("商铺配置失败"));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail("服务器异常"));
		}
	}
	
	private static String appendShopId(String url,String shopId){
		if(StringUtils.isEmpty(url)) return "";
		if(StringUtils.isEmpty(shopId) || url.contains("shopId=")) return url;
	 
		if(url.indexOf("?")>0){		
			url = url + "&";
		}else{
			url = url + "?";
		}
		
		if(!url.contains("shopId=")){
			url = url+ "shopId="+shopId;
		}
		return url;
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
			
			String productId = ShopProductService.saveProductInfo(product,session.getShopId());
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
			
			//秒杀
			if(product.getJoinSeckilling() !=null){
				dto.joinSeckilling =product.getJoinSeckilling(); 
			}
			if(product.getSeckillingTime()!=null){
				dto.seckillingTime = product.getSeckillingTime();
			}
			if(product.getSeckillingPrice()!=null){
				dto.seckillingPrice = String.valueOf(AmountUtil.f2y(product.getSeckillingPrice()));
			}
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
			dto.dealNum = product.getDeal();
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
				
				else if(flag==4){ 
					product.setJoinSeckilling(0);
					ShopProductService.updateSeckilling(product);					
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
			
			ShopProductCategoryDDL p = ShopCategoryService.createPCategory(session.getShopId(),text);
			if(p!=null)
				renderJSON(RtnUtil.returnSuccess("OK"));
			else
				renderJSON(RtnUtil.returnFail("新增分类失败"));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail("服务器异常"));
		}
	}
	
	public static void listAttrs(){
		try{
			if(request.cookies==null || !request.cookies.containsKey("shop_sid")){
				renderJSON(RtnUtil.returnFail("未登录"));
			}			
			String sessionStr = request.cookies.get("shop_sid").value;
			ShopMngSessionDDL session = ShopMngService.checkSession(sessionStr);
			if(session==null){				 
				renderJSON(RtnUtil.returnFail("未登录"));
			}
			List<AutoCompleteDto> result = ShopProductAttrService.listAttrs();
			renderJSON(result);
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
			
			ShopProductCategoryChildDDL sub = ShopCategoryService.createChildCategory(session.getShopId(),text, pCategoryId);
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
			
			 ShopCategoryService.delSubCategory(session.getShopId(),subId);
			 
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
	
	public static void exoprtOrderData(String orderId,String keyword,String startTime,String endTime,int status,String referScene,String appid,String channel){
		try{
			if(request.cookies==null || !request.cookies.containsKey("shop_sid")){
				renderJSON(RtnUtil.returnFail("未登录"));
			}			
			String sessionStr = request.cookies.get("shop_sid").value;
			ShopMngSessionDDL session = ShopMngService.checkSession(sessionStr);
			if(session==null){				 
				renderJSON(RtnUtil.returnFail("未登录"));
			}
			ByteArrayInputStream is = ExportData.reportOrderByCondition(session.getShopId(), orderId, keyword, startTime, endTime, status,referScene,appid, channel);
			renderBinary(is, "report_order.xls");
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail("服务器异常"));
		}
	}
	public static void listOrder(String orderId,String keyword,String startTime,String endTime,int status,
			String referScene,String appid,String channel,
			int page,int pageSize){
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
			
			int total = ShopOrderService.countMngOrder(session.getShopId(),orderId, keyword, 
					startTime,endTime,status,
					referScene,appid,channel);
			response.put("total", total);
			response.put("pageTotal", Math.ceil(total/(double)pageSize));
			 
			if(total == 0){
				response.put("list", orders);
				renderJSON(RtnUtil.returnSuccess("OK",response));
			}			
			
			
			page = page==0?1:page;
			pageSize = pageSize==0?10:pageSize;
			
			List<ShopOrderDDL> list = ShopOrderService.listMngOrder(session.getShopId(),orderId,
					keyword, startTime,endTime,status,
					referScene,appid,channel,
					page, pageSize);
			
			for(ShopOrderDDL order : list){
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("orderId",order.getOrderId());
				map.put("groupImg", API.getObjectAccessUrlSimple( order.getGroupImg()));
				map.put("groupName", order.getGroupName());
				map.put("productName", order.getProductName());
				map.put("groupId", order.getGroupId());
				map.put("productId", order.getProductId());
				map.put("orderStatus", order.getStatus());
				map.put("orderType", order.getOrderType());
				map.put("prize", order.getPrizeLevel());
				map.put("buyNum", order.getBuyNum());
				map.put("memo", order.getMemo());
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
				map.put("address", gson.fromJson(order.getAddress(), AddressDto.class));

				map.put("orderTime", DateUtil.format(order.getOrderTime()));
				map.put("payTime", order.getPayTime()!=null?DateUtil.format(order.getPayTime()):"");
				
				map.put("referAppId", order.getReferAppid());
				map.put("referChannel", order.getReferChannel());
				map.put("referScene", order.getReferScene());
				
				orders.add(map);
			}
			response.put("list", orders);
			renderJSON(RtnUtil.returnSuccess("OK", response));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail("服务器异常"));
		}
	}
	
	
	public static void listRefundOrder(String transactionId,String outTradeNo,String refundId,String outRefundNo,int page,int pageSize){
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
			
			int total = ShopRefundOrderService.countByNo(session.getShopId(),transactionId, outTradeNo, refundId, outRefundNo);
			response.put("total", total);
			response.put("pageTotal", Math.ceil(total/(double)pageSize));
			 
			if(total == 0){
				response.put("list", orders);
				renderJSON(RtnUtil.returnSuccess("OK",response));
			}			
			
			
			page = page==0?1:page;
			pageSize = pageSize==0?10:pageSize;
			
 			List<ShopRefundOrderDDL> list = ShopRefundOrderService.listByNo(session.getShopId(),transactionId, outTradeNo, refundId, outRefundNo, page, pageSize);

			for(ShopRefundOrderDDL order : list){
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("id",order.getId());
				map.put("outRefundNo", order.getOutRefundNo());
				map.put("outTradeNo", order.getOutTradeNo());
				map.put("refundId", order.getRefundId());
				map.put("totalFee", AmountUtil.f2y(order.getTotalFee()));
				map.put("refundAccount", order.getRefundAccount());
				map.put("refundFee", AmountUtil.f2y(order.getRefundFee()));
				map.put("refundRecvAccount", order.getRefundRecvAccout());
				map.put("refundRequestSource", order.getRefundRequestSource());
				map.put("refundStatus", order.getRefundStatus());
				map.put("settlementRefundFee",AmountUtil.f2y(order.getSettlementRefundFee()));
				map.put("settlementTotalFee", order.getSettlementTotalFee()==null?0:AmountUtil.f2y(order.getSettlementTotalFee()));
				map.put("shopId",order.getShopId());
				map.put("successTime", order.getSuccessTime());
				map.put("transactionId", order.getTransactionId()); 
				orders.add(map);
			}
			response.put("list", orders);
			renderJSON(RtnUtil.returnSuccess("OK", response));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail("服务器异常"));
		}
	}
	
	
	public static void refundAudit(String orderId,String refundFee,int auditStatus,String memo){
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
			ShopOrderService.refund_audit(order, auditStatus, AmountUtil.y2f(refundFee), memo);
			renderJSON(RtnUtil.returnSuccess("OK"));
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
			
			int total = ShopCouponMngService.countCoupon(session.getShopId(),couponId, keyword);
			response.put("total", total);
			response.put("pageTotal", Math.ceil(total/(double)pageSize));
			 
			if(total == 0){
				response.put("list", coupons);
				renderJSON(RtnUtil.returnSuccess("OK",response));
			}			
			
			
			page = page==0?1:page;
			pageSize = pageSize==0?10:pageSize;
			
			
			List<ShopCouponMngDDL>  list = ShopCouponMngService.listCoupon(session.getShopId(),couponId, keyword, page, pageSize);
			
 			
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
	
	
	public static void addCoupon(String couponId,final String couponName,String amount,String limitProductId,
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
			
			ShopCouponMngService.replace(session.getShopId(),couponId, couponName,AmountUtil.y2f(amount) ,
					limitProductId, 0, AmountUtil.y2f(limitPrice), limitTimes, 
					DateUtil.getTime(expireTime), DateUtil.getTime(startTime), DateUtil.getTime(endTime));
			
			//发送消息提醒
			ThreadUtil.sumbit(new Runnable(){
				@Override
				public void run() {
					Map<String,Map> dataMap = new HashMap<String,Map>();
					
					Map<String,String> k1 = new HashMap<String,String>();
					k1.put("value",couponName);
					k1.put("color", "#CD3333"); 
					
					Map<String,String> k2 = new HashMap<String,String>();
					k2.put("value", "点击立即前往领取");
					k2.put("color", "#3300cc"); 
					
					dataMap.put("keyword1", k1);
					dataMap.put("keyword2", k2); 
					List<FormIdsDDL> forms = FormIdService.listDistinct(Jws.configuration.getProperty("shop.appId"));
					if(forms!=null && forms.size()>0){
						for(FormIdsDDL form:forms){
							API.sendWxMessage(Jws.configuration.getProperty("shop.appId"),
									form.getOpenId(), 
									Jws.configuration.getProperty("wx.msg.templage.id.dktx"), 
									"pages/shop/shopIndex?forceShow=1", 
									form.getFormId(), 
									dataMap,
									"keyword1.DATA");
						}
					}
					
				} 
			});
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
					//通知收货人					 
					if(!StringUtils.isEmpty(order.getLitterAppParams())){
						UsersDDL buyer = UserService.get(order.getBuyerUserId());	
						AddressDto address = gson.fromJson(order.getAddress(), AddressDto.class);
						JsonObject parsms = new JsonParser().parse(order.getLitterAppParams()).getAsJsonObject();
						String packagestr = parsms.get("package").getAsString();
						String page="pages/shop/orderdetail?orderId="+order.getOrderId();
						Map<String,Map> dataMap = new HashMap<String,Map>();
						Map<String,String> k1 = new HashMap<String,String>();
						k1.put("value",address.provinceName+address.cityName+address.countyName+address.detailInfo);
						k1.put("color", "#CDC0B0");
						
						Map<String,String> k2 = new HashMap<String,String>();
 						k2.put("value",order.getGroupName());
						k2.put("color", "#000"); 
						
						
						Map<String,String> k3 = new HashMap<String,String>();
						k3.put("value", order.getOrderId());
						k3.put("color", "#000");
						
						Map<String,String> k4 = new HashMap<String,String>();
						k4.put("value", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getPayTime()));
						k4.put("color", "#3300cc");
						
						
						Map<String,String> k5 = new HashMap<String,String>();
						k5.put("value", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
						k5.put("color", "#3300cc");
						
						
						Map<String,String> k6 = new HashMap<String,String>();
						k6.put("value", shipper.getShipperName());
						k6.put("color", "#CD0000");
						
						Map<String,String> k7 = new HashMap<String,String>();
						k7.put("value", expressOrderCode);
						k7.put("color", "#D15FEE");
						
						dataMap.put("keyword1", k1);
						dataMap.put("keyword2", k2);
						dataMap.put("keyword3", k3); 
						dataMap.put("keyword4", k4); 
						dataMap.put("keyword5", k5); 
						dataMap.put("keyword6", k6); 
						dataMap.put("keyword7", k7); 
						
						API.sendWxMessage(parsms.get("appId").getAsString(), 
								buyer.getOpenId(), Jws.configuration.getProperty("wx.msg.templage.id.fhtz"), 
								page,packagestr.split("=")[1] , dataMap);
					} 
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
	
	
	public static void listWeTao(String keyword,int deleted,int page,int pageSize){
		try{
			if(request.cookies==null || !request.cookies.containsKey("shop_sid")){
				renderJSON(RtnUtil.returnFail("未登录"));
			}
			String sessionStr = request.cookies.get("shop_sid").value;
			ShopMngSessionDDL session = ShopMngService.checkSession(sessionStr);
			if(session==null){				 
				renderJSON(RtnUtil.returnFail("未登录"));
			}
			
			List<Map<String,Object>> weTaos = new ArrayList<Map<String,Object>>();
			Map<String,Object> response = new HashMap<String,Object>();
			
			int total = ShopWeTaoService.countWeTao(session.getShopId(),keyword,deleted);
			response.put("total", total);
			response.put("pageTotal", Math.ceil(total/(double)pageSize));
			 
			if(total == 0){
				response.put("list", weTaos);
				renderJSON(RtnUtil.returnSuccess("OK",response));
			}			
			
			
			page = page==0?1:page;
			pageSize = pageSize==0?10:pageSize;
			
			
			List<ShopWetaoDDL>  list = ShopWeTaoService.listWeTao(session.getShopId(),keyword,deleted, page, pageSize);
			
 			
			for(ShopWetaoDDL weTao : list){
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("id",weTao.getId());
				map.put("content", weTao.getContent());
				map.put("deleted", weTao.getDeleted());
				map.put("createTime", DateUtil.timeDesc(weTao.getCreateTime()));
				map.put("comment", weTao.getComment());
				
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
	
	public static void addWeTao(int id,String content,String images,String seoTitle,String seoKey,String seoDesc){
		try{
			if(request.cookies==null || !request.cookies.containsKey("shop_sid")){
				renderJSON(RtnUtil.returnFail("未登录"));
			}			
			String sessionStr = request.cookies.get("shop_sid").value;
			ShopMngSessionDDL session = ShopMngService.checkSession(sessionStr);
			if(session==null){				 
				renderJSON(RtnUtil.returnFail("未登录"));
			}
			ShopWeTaoService.replace(id, session.getShopId(),content, images, seoTitle, seoKey, seoDesc);
			renderJSON(RtnUtil.returnSuccess("OK"));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail("服务器异常"));
		}
	}
	
	public static void offLineWeTao(int id){
		try{
			if(request.cookies==null || !request.cookies.containsKey("shop_sid")){
				renderJSON(RtnUtil.returnFail("未登录"));
			}			
			String sessionStr = request.cookies.get("shop_sid").value;
			ShopMngSessionDDL session = ShopMngService.checkSession(sessionStr);
			if(session==null){				 
				renderJSON(RtnUtil.returnFail("未登录"));
			}
			ShopWeTaoService.offLineWeTao(id);
			renderJSON(RtnUtil.returnSuccess("OK"));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail("服务器异常"));
		}
	}
	
	public static void onLineWeTao(int id){
		try{
			if(request.cookies==null || !request.cookies.containsKey("shop_sid")){
				renderJSON(RtnUtil.returnFail("未登录"));
			}			
			String sessionStr = request.cookies.get("shop_sid").value;
			ShopMngSessionDDL session = ShopMngService.checkSession(sessionStr);
			if(session==null){				 
				renderJSON(RtnUtil.returnFail("未登录"));
			}
			ShopWeTaoService.onLineWeTao(id);
			renderJSON(RtnUtil.returnSuccess("OK"));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail("服务器异常"));
		}
	}
	
	public static void getOneWeTao(int id){
		try{
			if(request.cookies==null || !request.cookies.containsKey("shop_sid")){
				renderJSON(RtnUtil.returnFail("未登录"));
			}			
			String sessionStr = request.cookies.get("shop_sid").value;
			ShopMngSessionDDL session = ShopMngService.checkSession(sessionStr);
			if(session==null){				 
				renderJSON(RtnUtil.returnFail("未登录"));
			}
			
			ShopWetaoDDL weTao = ShopWeTaoService.get(id);
			if(weTao==null){
				renderJSON(RtnUtil.returnFail("不存在或已刪除"));
			}
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("id",weTao.getId());
			map.put("content", weTao.getContent());
			//map.put("createTime", weTao.getCreateTimeDesc());
			//map.put("comment", weTao.getComment());	
			map.put("shopId", weTao.getShopId());
			map.put("seoDesc", weTao.getSeoDesc());
			map.put("seoTitle", weTao.getSeoTitle());
			map.put("seoKey", weTao.getSeoKey());
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
			//map.put("view", weTao.getView());
			//map.put("zan", weTao.getZan());	
			
			renderJSON(RtnUtil.returnSuccess("OK",map));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail("服务器异常"));
		}
	}
	
	/**
	 * 数据概览
	 * @param flag 1=今天 2=昨天 3=近7天 4=近15天 5=近30天 0=根据日期查询	 
	 * @param beginTime
	 * @param endTime
	 */
	public static void dataOverView(int flag,String beginTime,String endTime){
		try{
			if(request.cookies==null || !request.cookies.containsKey("shop_sid")){
				renderJSON(RtnUtil.returnFail("未登录"));
			}			
			String sessionStr = request.cookies.get("shop_sid").value;
			ShopMngSessionDDL session = ShopMngService.checkSession(sessionStr);
			if(session==null){				 
				renderJSON(RtnUtil.returnFail("未登录"));
			}
			
			
			long todayLastMills = DateUtil.getTime(DateUtil.format(System.currentTimeMillis(),"yyyy-MM-dd")+" 23:59:59");
			
			if(flag==1){
				beginTime = DateUtil.format(todayLastMills,"yyyy-MM-dd")+" 00:00:00";
				endTime = DateUtil.format(todayLastMills,"yyyy-MM-dd")+" 23:59:59";
			}else if(flag == 2){
				beginTime = DateUtil.format(todayLastMills-24*60*60*1000l,"yyyy-MM-dd")+" 00:00:00";
				endTime = DateUtil.format(todayLastMills-24*60*60*1000l,"yyyy-MM-dd")+" 23:59:59";
			}else if(flag==3){
				beginTime = DateUtil.format(todayLastMills-7*24*60*60*1000l,"yyyy-MM-dd")+" 00:00:00";
				endTime = DateUtil.format(todayLastMills,"yyyy-MM-dd")+" 23:59:59";
			}else if(flag==4){
				beginTime = DateUtil.format(todayLastMills-15*24*60*60*1000l,"yyyy-MM-dd")+" 00:00:00";
				endTime = DateUtil.format(todayLastMills,"yyyy-MM-dd")+" 23:59:59";
			}else if(flag==5){
				beginTime = DateUtil.format(todayLastMills-30*24*60*60*1000l,"yyyy-MM-dd")+" 00:00:00";
				endTime = DateUtil.format(todayLastMills,"yyyy-MM-dd")+" 23:59:59";
			}else if(!StringUtils.isEmpty(beginTime) && !StringUtils.isEmpty(endTime)){
				beginTime = beginTime.substring(0, 10)+" 00:00:00";
				endTime = endTime.substring(0, 10)+" 00:00:00";
			}
			
			
			Logger.info("beginTime %s,endTime %s", beginTime,endTime);
			
			List<ShopOrderDDL> list = ShopOrderService.listOrder(session.getShopId(), 
					DateUtil.getTime(beginTime), 
					DateUtil.getTime(endTime));
			
			OverViewDatasDto x = new OverViewDatasDto(
					DateUtil.getTime(beginTime),
					DateUtil.getTime(endTime));
			
			if(list!=null && list.size()>0){
				for(ShopOrderDDL order : list){
					Integer index = x.getIndex(DateUtil.format(order.getOrderTime(), "yyyy-MM-dd"));
					if(index==null) continue;					
					//设置成交量
					if(order.getStatus() == ShopOrderService.ORDER_DELIVERED  || 
							order.getStatus() == ShopOrderService.ORDER_PAYED_TOGETHER_1  || 
							order.getStatus() == ShopOrderService.ORDER_SIGNED  || 
							order.getStatus() == ShopOrderService.ORDER_DONE  || 
							order.getStatus() == ShopOrderService.ORDER_UNLUCKY_MAN  || 
							order.getStatus() == ShopOrderService.ORDER_LUCKY_MAN
					){
						x.orderCount.set(index, x.orderCount.get(index)+1);
						double amount = 0d;
						if(StringUtils.isEmpty(order.getTogetherId())){
							amount = AmountUtil.f2y(order.getGroupPrice());
						}else{
							amount = AmountUtil.f2y(order.getGroupTogetherPrice());
							x.orderTogetherCount.set(index, x.orderTogetherCount.get(index)+1);
						}
 						x.orderAmountSum.set(index, x.orderAmountSum.get(index)+amount);
 						x.productCount.set(index, x.productCount.get(index)+order.getBuyNum());
 						
 						if(order.getStatus() == ShopOrderService.ORDER_DELIVERED ){
 							x.deliverCount.set(index, x.deliverCount.get(index)+1);
 						}
					}
				}
			} 
			renderJSON(RtnUtil.returnSuccess("OK",x));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail("服务器异常"));
		}
	}
	
}
