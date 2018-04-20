package modules.shop.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.qcloud.cos.utils.DateUtils;

import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import jws.dal.sqlbuilder.Sort;
import modules.shop.ddl.ShopWetaoDDL;
import util.DateUtil;

public class ShopWeTaoService {

	public static void zan(int id){
		ShopWetaoDDL weTao = get(id);
		if(weTao==null)return ;
		weTao.setZan(weTao.getZan()+1);
		Dal.update(weTao, "ShopWetaoDDL.zan", new Condition("ShopWetaoDDL.id","=",id));
	}
	
	public static void cancelZan(int id){
		ShopWetaoDDL weTao = get(id);
		if(weTao==null)return ;
		if(weTao.getZan()-1>0){
			weTao.setZan(weTao.getZan()-1);
		}		
		Dal.update(weTao, "ShopWetaoDDL.zan", new Condition("ShopWetaoDDL.id","=",id));
	}
	
	public static void view(int id){
		ShopWetaoDDL weTao = get(id);
		if(weTao==null)return ;
		weTao.setView(weTao.getView()+1);
		Dal.update(weTao, "ShopWetaoDDL.view", new Condition("ShopWetaoDDL.id","=",id));
	}
	
	public static void comment(int id,String avatar,String nickName){
		ShopWetaoDDL weTao = get(id);
		if(weTao==null)return ;
		weTao.setComment(weTao.getComment()+1);
		Dal.update(weTao, "ShopWetaoDDL.view", new Condition("ShopWetaoDDL.id","=",id));
	}
	
	public static ShopWetaoDDL get(int id){
		return Dal.select("ShopWetaoDDL.*", id);
	}
	public static int countWeTao(String keyword){
		Condition cond = new Condition("ShopWetaoDDL.id",">",0);
		 
		if(!StringUtils.isEmpty(keyword)){
			cond.add(new Condition("ShopWetaoDDL.content","like","%"+keyword+"%"), "and");
		}
		return Dal.count(cond);
	}
	
	public static List<ShopWetaoDDL> listWeTao(String keyword,int page,int pageSize){
	 
		Condition cond = new Condition("ShopWetaoDDL.id",">",0);
		if(!StringUtils.isEmpty(keyword)){
			cond.add(new Condition("ShopWetaoDDL.content","like","%"+keyword+"%"), "and");
		}
		
		Sort sort = new Sort("ShopWetaoDDL.id",false);
		return Dal.select("ShopWetaoDDL.*", cond, sort, (page-1)*pageSize, pageSize);
	}
	
	public static void replace(int id,String content,String images,String seoTitle,String seoKey,String seoDesc){
		ShopWetaoDDL old = get(id);
		if(old==null){
			old = new ShopWetaoDDL();
			old.setCreateTime(System.currentTimeMillis());
			old.setView(0);
			old.setZan(0);
			old.setComment(0);			 
		}
		old.setContent(content);
		old.setImages(images);
		old.setSeoDesc(seoDesc);
		old.setSeoKey(seoKey);
		old.setSeoTitle(seoTitle);
		Dal.replace(old);
	}
}
