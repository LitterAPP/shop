package modules.shop.service;

import java.util.List;

import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import jws.dal.sqlbuilder.Sort;
import modules.shop.ddl.ShopWetaoCommentDDL;

public class ShopWeTaoCommentService {

	public static ShopWetaoCommentDDL get(int id){
		return Dal.select("ShopWetaoCommentDDL.*", id);
	}
	
	public static List<ShopWetaoCommentDDL> list(int weTaoId,int page,int pageSize){
		Condition cond = new Condition("ShopWetaoCommentDDL.deleted","=",0);
		cond.add(new Condition("ShopWetaoCommentDDL.wetaoId","=",weTaoId), "and");
		return Dal.select("ShopWetaoCommentDDL.*", cond, new Sort("ShopWetaoCommentDDL.id",false), (page-1)*pageSize, pageSize);
	}
	
	public static void delComment(int id){
		ShopWetaoCommentDDL comment = get(id);
		if(comment==null)return ;
		comment.setDeleted(1);
		Dal.update(comment, "ShopWetaoCommentDDL.deleted", new Condition("ShopWetaoCommentDDL.id","=",id));
	}
	
	public static boolean addComment(int weTaoId,String commentStr,int userId,String nickName,String avatar,String clientIp){
		ShopWetaoCommentDDL comment = new ShopWetaoCommentDDL();
		comment.setAvatar(avatar);
		comment.setNickName(nickName);
		comment.setClientIp(clientIp);
		comment.setComment(commentStr);
		comment.setCreateTime(System.currentTimeMillis());
		comment.setDeleted(0);
		comment.setUserId(userId);
		comment.setWetaoId(weTaoId);
		return Dal.insert(comment)>0;
	}
	
	public static int countComment(int wetaoId){
		Condition cond = new Condition("ShopWetaoCommentDDL.deleted","=",0);
		cond.add(new Condition("ShopWetaoCommentDDL.wetaoId","=",wetaoId), "and");
		return Dal.count(cond);
	}
}
