package modules.shop.service;

import java.util.List;

import jws.Logger;
import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import modules.shop.ddl.ShopApplyInfoDDL;

public class ApplyService {

	public static boolean apply(String frontCardKey,String backCardKey,String mobile,String sellerWx,int userId,int feeRate){
		ShopApplyInfoDDL apply = new ShopApplyInfoDDL();
		apply.setBackCardKey(backCardKey);
		apply.setFrontCardKey(frontCardKey);
		apply.setCreateTime(System.currentTimeMillis());
		apply.setFeeRate(feeRate);
		apply.setMobile(mobile);
		apply.setUserId(userId);
		apply.setWx(sellerWx);
		if(Dal.insert(apply)>0){
			try {
				UserService.becomeSeller(userId, mobile, sellerWx);
			} catch (Exception e) {
				Logger.error(e, e.getMessage());
				return false;
			}
			return true;
		}else{
			return false;
		}
	}
	
	public static ShopApplyInfoDDL getApplyInfo(int userId){
		Condition condition = new Condition("ShopApplyInfoDDL.userId","=",userId);
		List<ShopApplyInfoDDL> list = Dal.select("ShopApplyInfoDDL.*", condition, null, 0, 1);
		if( null == list || list.size() == 0 ) return null;
		return list.get(0);
	}
}
