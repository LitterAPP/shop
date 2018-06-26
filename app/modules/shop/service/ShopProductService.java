package modules.shop.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import controllers.dto.ProductInfoDto;
import controllers.dto.ProductInfoDto.Group;
import controllers.dto.ProductInfoDto.Image;
import controllers.dto.ProductInfoDto.TextDetail;
import eventbus.EventBusCenter;
import eventbus.event.params.ShopIndexProChangedParams;
import jws.Logger;
import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import jws.dal.sqlbuilder.Sort;
import modules.shop.ddl.ShopProductAttrDDL;
import modules.shop.ddl.ShopProductAttrRelDDL;
import modules.shop.ddl.ShopProductCategoryChildDDL;
import modules.shop.ddl.ShopProductCategoryDDL;
import modules.shop.ddl.ShopProductCategoryRelDDL;
import modules.shop.ddl.ShopProductDDL;
import modules.shop.ddl.ShopProductGroupDDL;
import modules.shop.ddl.ShopProductImagesDDL;
import modules.shop.ddl.UsersDDL;
import util.AmountUtil;
import util.IDUtil;

public class ShopProductService { 
	
	public enum Type{
		PRODUCT_TYPE_ENTITY(0,"实物"),PRODUCT_TYPE_PRIZE(1,"抽奖"),PRODUCT_TYPE_RENT(2,"虚拟");
		private int value;
		private String text;
		Type(int v,String t){
			value = v;
			text = t;
		}
		public int getValue() {
			return value;
		}
		public void setValue(int value) {
			this.value = value;
		}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
	}
	
	public enum Status{
		PRODUCT_STATUS_INIT(0,"预览"),PRODUCT_STATUS_SELL(1,"上架"),PRODUCT_STATUS_UNSELL(2,"下架");
		private int value;
		private String text;
		Status(int v,String t){
			value = v;
			text = t;
		}
		public int getValue() {
			return value;
		}
		public void setValue(int value) {
			this.value = value;
		}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
	} 
	public static ShopProductDDL get(int id){
		return Dal.select("ShopProductDDL.*", id);
	}
	
	public static ShopProductDDL getByProductId(String productId){
		if(StringUtils.isEmpty(productId) || !productId.startsWith("PRO-")){
			return null;
		}
		List<ShopProductDDL> list = Dal.select("ShopProductDDL.*", new Condition("ShopProductDDL.productId","=",productId) , null, 0, 1);
		if( list==null || list.size() == 0) return null;
		return list.get(0);
	}
	
	public static boolean updateHot(ShopProductDDL product){
		if(product==null)return false;
		product.setUpdateTime(System.currentTimeMillis());
		return Dal.update(product, "ShopProductDDL.isHot,ShopProductDDL.updateTime", new Condition("ShopProductDDL.id","=",product.getId()))>0;
	}
	public static boolean updateSale(ShopProductDDL product){
		if(product==null)return false;
		return Dal.update(product, "ShopProductDDL.isSale,ShopProductDDL.updateTime", new Condition("ShopProductDDL.id","=",product.getId()))>0;
	}
	
	public static boolean updateSeckilling(ShopProductDDL product){
		if(product==null)return false;
		return Dal.update(product, "ShopProductDDL.joinSeckilling,ShopProductDDL.updateTime", new Condition("ShopProductDDL.id","=",product.getId()))>0;
	}
	
	public static List<ShopProductDDL> listShopIndexProduct(int page,int pageSize){
		Condition cond = new Condition("ShopProductDDL.showIndex","=",1);
		cond.add(new Condition("ShopProductDDL.store",">",100), "and");
		cond.add(new Condition("ShopProductDDL.status","=",1), "and");
		Sort sort = new Sort("ShopProductDDL.orderBy",true);
		return Dal.select("ShopProductDDL.*", cond, sort, (page-1)*pageSize, pageSize);
	}
	
	public static boolean updateStatus(String productId,int status){
		ShopProductDDL old = getByProductId(productId);
		old.setStatus(status);
		old.setUpdateTime(System.currentTimeMillis());
		
		//下架，更新用户购物商品为下架状态
		if(status == ShopProductService.Status.PRODUCT_STATUS_UNSELL.getValue() ){
			ShopCarService.productUnSell(productId);
		}
		
		return Dal.update(old, "ShopProductDDL.status,ShopProductDDL.updateTime",
				new Condition("ShopProductDDL.productId","=",productId))>0;
	}
	
	public static String saveProduct(ProductInfoDto product,UsersDDL user) throws Exception{
		try{
			ShopProductDDL p = new ShopProductDDL();
			if(!StringUtils.isEmpty(product.productId)){
				ShopProductDDL old = getByProductId(product.productId);
				if(old.getSellerUserId().intValue() != user.getId()){
					throw new Exception("无权限操作");
				}
				p.setCreateTime(old.getCreateTime());
				//下架原商品
				if(old.getStatus() == Status.PRODUCT_STATUS_INIT.getValue()){
					//不新建商品，继续编辑
					product.productId = old.getProductId(); 
				}else{
					//新建商品
					updateStatus(product.productId,Status.PRODUCT_STATUS_UNSELL.getValue());
					product.productId = IDUtil.gen("PRO");
				}
								
			}else{//新建商品
				product.productId = IDUtil.gen("PRO");
				p.setCreateTime(System.currentTimeMillis());
			}
			 
			//1处理分类关系			
			ShopProductCategoryDDL pCategory = ShopCategoryService.getByPCategoryId(product.p_category_id);
			ShopProductCategoryChildDDL subCategory = ShopCategoryService.getBySubCategoryId(product.sub_category_id);
			ShopProductCategoryRelDDL categoryRel = new ShopProductCategoryRelDDL();
			categoryRel.setPCategoryId(pCategory.getCategoryId());
			categoryRel.setSubCategoryId(subCategory.getCategoryId());
			categoryRel.setPCategoryName(pCategory.getCategoryName());
			categoryRel.setSubCategoryName(subCategory.getCategoryName());
			categoryRel.setProductId(product.productId);
			categoryRel.setOrderBy(0);
			Dal.replace(categoryRel);
			
			//2商品属性关系再建立
			String[] attrs = new String[]{null,null,null};
			if(!StringUtils.isEmpty(product.attrs_1)){
				attrs[0] = product.attrs_1;
			}
			if(!StringUtils.isEmpty(product.attrs_2)){
				attrs[1] = product.attrs_2;
			}
			if(!StringUtils.isEmpty(product.attrs_3)){
				attrs[2] = product.attrs_3;
			}
			ShopProductAttrService.delAttrRelByProductId(product.productId);
			for(int i=0;i<attrs.length;i++){
				if(!StringUtils.isEmpty(attrs[i])){
					ShopProductAttrDDL attr = ShopProductAttrService.getByAttrId(attrs[i]);
					ShopProductAttrRelDDL attrRel = new ShopProductAttrRelDDL();
					attrRel.setAttrId(attrs[i]);
					attrRel.setOrderBy(i);
					attrRel.setAttrName(attr.getAttrName());
					attrRel.setProductId(product.productId);
					Dal.insert(attrRel);
				}
			}
			//3建立商品分组关系
			int gOrderBy = 0;
			ShopProductGroupService.delGroupByProductId(product.productId);
			for(Group g : product.groups){
				ShopProductGroupDDL group = new ShopProductGroupDDL();
				group.setGroupId(IDUtil.gen("GRP"));
				group.setProductId(product.productId);
				group.setGroupImage(g.osskey);
				group.setGroupName(g.title);
				group.setGroupPrice(AmountUtil.y2f(g.price1));
				group.setGroupTogetherPrice(AmountUtil.y2f(g.price2));
				group.setOrderBy(gOrderBy);
				Dal.insert(group);
				gOrderBy++;
			}
			//4商品图片关系
			ShopProductImageService.delImageByProductIdAndType(product.productId, ShopProductImageService.SCREENSHOT_TYPE);
			for(Image pic : product.play_pics){
				ShopProductImagesDDL imgRel = new ShopProductImagesDDL();
				imgRel.setImageKey(pic.osskey);
				imgRel.setProductId(product.productId);
				imgRel.setType(ShopProductImageService.SCREENSHOT_TYPE);
				Dal.insert(imgRel);
			}
			ShopProductImageService.delImageByProductIdAndType(product.productId, ShopProductImageService.DETAIL_TYPE);
			if(product.pic_details!=null && product.pic_details.size()>0){
				for(Image pic : product.pic_details){
					ShopProductImagesDDL imgRel = new ShopProductImagesDDL();
					imgRel.setImageKey(pic.osskey);
					imgRel.setProductId(product.productId);
					imgRel.setType(ShopProductImageService.DETAIL_TYPE);
					Dal.insert(imgRel);
				}
			}
			
			p.setStore(product.store);
			p.setProductType(product.productType);
			p.setSellerUserId(user.getId().intValue());
			p.setProductId(product.productId);
			p.setProductBanner(product.banner_pic.osskey);
			p.setProductName(product.title);
			p.setProductCategory(categoryRel.getPCategoryName()+":"+categoryRel.getSubCategoryName()); 
			
			//处理文字详情
			if(product.text_details != null && product.text_details.size() > 0){
				StringBuffer sb = new StringBuffer();
				for(TextDetail td : product.text_details){
					td.value = td.value.replaceAll("`", "'");
					sb.append(td.value).append("`");
				}
				sb.substring(0, sb.length()-1);
				p.setProductDesc(sb.toString());
			}
			
			p.setProductOriginAmount(AmountUtil.y2f(product.price[0]));
			p.setProductNowAmount(AmountUtil.y2f(product.price[1]));
			//拼团
			p.setJoinTogether(product.join_together?1:0);
			if(product.join_together){
				p.setTogetherExpirHour(product.together_info.hour);
				p.setTogetherNumber(product.together_info.num);
				p.setTogetherSales(product.together_info.vcount);
				p.setProductTogetherAmount(AmountUtil.y2f(product.together_info.price));
			} 
			p.setStatus(ShopProductService.Status.PRODUCT_STATUS_INIT.getValue());
			
			p.setSellerTelNumber(user.getMobile());
			p.setSellerWxNumber(user.getSellerWx());
			if(!StringUtils.isEmpty(product.contact_mobile)){
				p.setSellerTelNumber(product.contact_mobile);
			}
			if(!StringUtils.isEmpty(product.contact_wx)){
				p.setSellerWxNumber(product.contact_wx);
			} 
			p.setUpdateTime(System.currentTimeMillis());
			 
			Dal.replace(p);
			
			return product.productId;
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			throw new Exception("保存失败");
		}
	} 
	
	/**
	 * 商品PC后台
	 * @param product
	 * @return
	 * @throws Exception
	 */
	public static String saveProductInfo(ProductInfoDto product,String shopId) throws Exception{
		try{
			ShopProductDDL p = new ShopProductDDL();
			
			p.setShopId(shopId);
			p.setStore(product.store);
			p.setProductType(product.productType);
			p.setSellerUserId(0);
			p.setProductId(product.productId);
			p.setProductBanner(product.banner_pic.osskey);
			p.setProductName(product.title);
			p.setProductCategory(""); 
			p.setPv(0);
			p.setDeal(product.dealNum);
			p.setIsHot(product.isHot?1:0);
			p.setIsSale(product.isSale?1:0);
			p.setProductOriginAmount(AmountUtil.y2f(product.price[0]));
			p.setProductNowAmount(AmountUtil.y2f(product.price[1]));
			p.setUpdateTime(System.currentTimeMillis()); 
			//拼团
			p.setJoinTogether(product.join_together?1:0);
			//秒杀活动
			p.setJoinSeckilling(product.joinSeckilling);
			p.setSeckillingPrice(AmountUtil.y2f(product.seckillingPrice));
			p.setSeckillingTime(product.seckillingTime);
			
			if(product.join_together){
				p.setTogetherExpirHour(product.together_info.hour);
				p.setTogetherNumber(product.together_info.num);
				p.setTogetherSales(product.together_info.vcount);
				p.setProductTogetherAmount(AmountUtil.y2f(product.together_info.price));
			} 
			ShopProductDDL old = getByProductId(product.productId);
			
			if(old==null){
				product.productId = IDUtil.gen("PRO");
				p.setCreateTime(System.currentTimeMillis());
				
			}else{
				p.setPv(old.getPv());
				p.setCreateTime(old.getCreateTime());				
				p.setDeal(product.dealNum>0?product.dealNum:old.getDeal());
				p.setIsHot(old.getIsHot());
				p.setIsSale(old.getIsSale());
				//未上架商品，直接编辑
				if(old.getStatus() != Status.PRODUCT_STATUS_SELL.getValue()){
					//不新建商品，继续编辑
					product.productId = old.getProductId(); 
					Logger.info("商品非上架状态，继续在此商品基础上编辑，%s", product.productId);
				}else{
					//下架旧商品，并新建商品ID 
					updateStatus(product.productId,Status.PRODUCT_STATUS_UNSELL.getValue());
					String newProductId = IDUtil.gen("PRO");	
					//下架旧商品，上架新商品，通知需要更新商品ID的地方
					EventBusCenter.post(new ShopIndexProChangedParams(shopId,newProductId,product.productId));
					Logger.info("商品上架状态， 新建一商品，旧商品ID：%s，新商品ID：%s", product.productId,newProductId);
					product.productId = newProductId; 
				}
			}
			p.setProductId(product.productId);
			 
			//1处理分类关系	
			ShopCategoryService.deleteCategoryRel(product.productId);
			if(product.selectedCategoryParams != null && product.selectedCategoryParams.size() > 0){
				int orderBy = 0;
				for(ProductInfoDto.Category cat:product.selectedCategoryParams){
					ShopProductCategoryDDL pCategory = ShopCategoryService.getByPCategoryId(cat.pCategoryId);
					ShopProductCategoryChildDDL subCategory = ShopCategoryService.getBySubCategoryId(cat.subCategoryId);
					
					if(pCategory==null){
						throw new Exception("一级分类不存在，不可能的啦，"+cat.pCategoryId);
					}
					
					ShopProductCategoryRelDDL categoryRel = new ShopProductCategoryRelDDL();
					
					categoryRel.setPCategoryId(pCategory.getCategoryId());
					categoryRel.setPCategoryName(pCategory.getCategoryName());
					
					categoryRel.setSubCategoryId(subCategory==null?"":subCategory.getCategoryId());					
					categoryRel.setSubCategoryName(subCategory==null?"":subCategory.getCategoryName());
					categoryRel.setProductId(product.productId);
					categoryRel.setOrderBy(orderBy);
					Dal.insert(categoryRel);
					orderBy++;
				}
			} 
			
			//2商品属性关系再建立
			ShopProductAttrService.delAttrRelByProductId(product.productId);
			if(product.selectedAttrs!=null && product.selectedAttrs.size() > 0 ){
				int orderBy = 0;
				for(String attr : product.selectedAttrs){
					ShopProductAttrDDL attrDDL = ShopProductAttrService.createAttr(attr);
					ShopProductAttrRelDDL attrRel = new ShopProductAttrRelDDL();
					attrRel.setAttrId(attrDDL.getAttrId());
					attrRel.setOrderBy(orderBy);
					attrRel.setAttrName(attrDDL.getAttrName());
					attrRel.setProductId(product.productId);
					Dal.insert(attrRel);
					orderBy++;
				}
			} 
			
			//3建立商品分组关系
			int gOrderBy = 1;
			ShopProductGroupService.delGroupByProductId(product.productId);
			
			//建立默认分组
			ShopProductGroupDDL defalutGroup = new ShopProductGroupDDL();
			defalutGroup.setGroupId(IDUtil.gen("GRP"));
			defalutGroup.setProductId(product.productId);
			defalutGroup.setGroupImage(p.getProductBanner());
			defalutGroup.setGroupName("【单件】"+p.getProductName());
			defalutGroup.setGroupPrice(p.getProductNowAmount());
			if(p.getJoinTogether() == 1){
				defalutGroup.setGroupTogetherPrice(p.getProductTogetherAmount());
			}else{
				defalutGroup.setGroupTogetherPrice(0);
			}
			defalutGroup.setOrderBy(0);
			Dal.insert(defalutGroup);
			
			//建立分组
			if(product.groups!=null && product.groups.size()>0){
				for(Group g : product.groups){
					ShopProductGroupDDL group = new ShopProductGroupDDL();
					group.setGroupId(IDUtil.gen("GRP"));
					group.setProductId(product.productId);
					group.setGroupImage(g.osskey);
					group.setGroupName(g.title);
					group.setGroupPrice(AmountUtil.y2f(g.price1));
					group.setGroupTogetherPrice(AmountUtil.y2f(g.price2));
					group.setOrderBy(gOrderBy);
					Dal.insert(group);
					gOrderBy++;
				}
			}
			
			//4商品图片关系
			ShopProductImageService.delImageByProductIdAndType(product.productId, ShopProductImageService.SCREENSHOT_TYPE);
			if(product.play_pics!=null && product.play_pics.size()>0){
				for(Image pic : product.play_pics){
					ShopProductImagesDDL imgRel = new ShopProductImagesDDL();
					imgRel.setImageKey(pic.osskey);
					imgRel.setProductId(product.productId);
					imgRel.setType(ShopProductImageService.SCREENSHOT_TYPE);
					Dal.insert(imgRel);
				}
			}
			ShopProductImageService.delImageByProductIdAndType(product.productId, ShopProductImageService.DETAIL_TYPE);
			if(product.pic_details!=null && product.pic_details.size()>0){
				for(Image pic : product.pic_details){
					ShopProductImagesDDL imgRel = new ShopProductImagesDDL();
					imgRel.setImageKey(pic.osskey);
					imgRel.setProductId(product.productId);
					imgRel.setType(ShopProductImageService.DETAIL_TYPE);
					Dal.insert(imgRel);
				}
			} 
			//处理文字详情
			if(product.text_details != null && product.text_details.size() > 0){
				StringBuffer sb = new StringBuffer();
				for(TextDetail td : product.text_details){
					td.value = td.value.replaceAll("`", "'");
					sb.append(td.value).append("`");
				}
				sb.substring(0, sb.length()-1);
				p.setProductDesc(sb.toString());
			}
			
			//直接发布上线了
			p.setStatus(ShopProductService.Status.PRODUCT_STATUS_SELL.getValue());
			
			p.setSellerTelNumber("");
			p.setSellerWxNumber("");
			if(!StringUtils.isEmpty(product.contact_mobile)){
				p.setSellerTelNumber(product.contact_mobile);
			}
			if(!StringUtils.isEmpty(product.contact_wx)){
				p.setSellerWxNumber(product.contact_wx);
			} 
			
			Dal.replace(p); 
			
			return product.productId;
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			throw new Exception("保存失败");
		}
	} 

	public static List<ShopProductDDL> listMyProduct(int sellerUid,String keyword,int status,int page,int pageSize){
		if(sellerUid <=0 ) return null;
		Condition condition = new Condition("ShopProductDDL.sellerUserId","=",sellerUid);
		if( status>=0 ){
			condition.add(new Condition("ShopProductDDL.status","=",status), "and");
		}
		if(!StringUtils.isEmpty(keyword)){
			condition.add(new Condition("ShopProductDDL.productName","like","%"+keyword+"%"), "and");
		}
		Sort sort = new Sort("ShopProductDDL.id",false);
		return Dal.select("ShopProductDDL.*", condition, sort, (page-1)*pageSize, pageSize);
	}
	
	//orderBy 1=时间降序 2=销量降序 3=价格降序 4=价格升序 5=综合排序
	public static  List<ShopProductDDL> listProduct(String shopId,String productId,String keyword,String pCategoryId,
			String subCategoryId,int sale,int hot,int status,int orderBy,int joinSeckilling,int seckillingTime,
			int page,int pageSize){
		page = page <=0 ?1 : page;
		pageSize = (pageSize > 30 || pageSize <=0 ) ?10 : pageSize;
		
		
		Condition condition = new Condition("ShopProductDDL.id",">",0);
		
		if(!StringUtils.isEmpty(shopId)){
			condition.add(new Condition("ShopProductDDL.shopId","=",shopId), "and");
		}
		
		if(!StringUtils.isEmpty(productId)){
			condition.add(new Condition("ShopProductDDL.productId","=",productId), "and");
		}

		if(status>=0){
			condition.add(new Condition("ShopProductDDL.status","=",status), "and");
		} 
		if(!StringUtils.isEmpty(keyword)){
			condition.add(new Condition("ShopProductDDL.productName","like","%"+keyword+"%"), "and");
		}
		
		if(joinSeckilling!=-1){
			condition.add(new Condition("ShopProductDDL.joinSeckilling","=",joinSeckilling), "and");
		}
		
		if(seckillingTime!=-1){
			condition.add(new Condition("ShopProductDDL.seckillingTime","=",seckillingTime), "and");
		}
		
		//如果2个分类ID，则查询出来		
		if( !StringUtils.isEmpty(pCategoryId) || !StringUtils.isEmpty(subCategoryId) ){
			List<ShopProductCategoryRelDDL> rels= ShopCategoryService.listByCategory(pCategoryId, subCategoryId);
			if(rels!=null && rels.size()>0){
				Set<String> productIds = new HashSet<String>();			
				for(ShopProductCategoryRelDDL rel : rels){
					productIds.add(rel.getProductId());
				}
				List<String> productIdList = Arrays.asList(productIds.toArray(new String[]{}));
				condition.add(new Condition("ShopProductDDL.productId","in",productIdList), "and");
			}else{
				return null;
			}
		}
		
		if(hot == 1){
			condition.add(new Condition("ShopProductDDL.isHot","=",1), "and");
		}else if(sale==1){
			condition.add(new Condition("ShopProductDDL.isSale","=",1), "and");
		}
		//处理Order By
		//orderBy 1=时间降序 2=销量降序 3=价格降序 4=价格升序 5=综合排序
		Sort sort = null;
		if(orderBy==1){
			sort = new Sort("ShopProductDDL.updateTime",false);
		}else if(orderBy == 2){
			sort = new Sort("ShopProductDDL.deal",false);
		}else if(orderBy == 3){
			sort = new Sort("ShopProductDDL.productNowAmount",false);
		}else if(orderBy == 4){
			sort = new Sort("ShopProductDDL.productNowAmount",true);
		}else if(orderBy == 5){
			sort = new Sort("ShopProductDDL.pv",false);
		}
		
		return Dal.select("ShopProductDDL.*", condition, sort, (page-1)*pageSize, pageSize);
	}
	
	public static  int countProduct(String shopId,String productId,String keyword,String pCategoryId,
			String subCategoryId,int sale,int hot,int status,int joinSeckilling,int seckillingTime){
		Condition condition = new Condition("ShopProductDDL.id",">",0);
		
		if(!StringUtils.isEmpty(shopId)){
			condition.add(new Condition("ShopProductDDL.shopId","=",shopId), "and");
		}
		
		if(!StringUtils.isEmpty(productId)){
			condition.add(new Condition("ShopProductDDL.productId","=",productId), "and");
		}
		
		if(status>=0){
			condition.add(new Condition("ShopProductDDL.status","=",status), "and");
		} 
		if(!StringUtils.isEmpty(keyword)){
			condition.add(new Condition("ShopProductDDL.productName","like","%"+keyword+"%"), "and");
		}
		
		if(joinSeckilling!=-1){
			condition.add(new Condition("ShopProductDDL.joinSeckilling","=",joinSeckilling), "and");
		}
		
		if(seckillingTime!=-1){
			condition.add(new Condition("ShopProductDDL.seckillingTime","=",seckillingTime), "and");
		}
		
		//如果2个分类ID，则查询出来
		if( !StringUtils.isEmpty(pCategoryId) || !StringUtils.isEmpty(subCategoryId) ){
			List<ShopProductCategoryRelDDL> rels= ShopCategoryService.listByCategory(pCategoryId, subCategoryId);
			if(rels!=null && rels.size()>0){
				Set<String> productIds = new HashSet<String>();			
				for(ShopProductCategoryRelDDL rel : rels){
					productIds.add(rel.getProductId());
				}
				List<String> productIdList = Arrays.asList(productIds.toArray(new String[]{}));
				condition.add(new Condition("ShopProductDDL.productId","in",productIdList), "and");
			}else{
				return 0;
			}
		}
		
		
		if(hot == 1){
			condition.add(new Condition("ShopProductDDL.isHot","=",1), "and");
		}else if(sale==1){
			condition.add(new Condition("ShopProductDDL.isSale","=",1), "and");
		}
		 
		
		return Dal.count(condition);
	}
}
