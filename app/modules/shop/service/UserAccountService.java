package modules.shop.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import modules.shop.ddl.ShopCouponMngDDL;
import modules.shop.ddl.UserAccountDDL;
import util.IDUtil;

public class UserAccountService {

    public static final int ACCOUNT_TYPE_COUPON = 2;
    public static final int ACCOUNT_TYPE_BASIC = 1;

    public static void createBasicAccount(int userId) {
        UserAccountDDL account = getBasicAccount(userId);
        if (account != null) {
            return;
        }
        UserAccountDDL newAccount = new UserAccountDDL();
        newAccount.setAccountId(IDUtil.gen("ACC"));
        newAccount.setAccountName("账户");
        newAccount.setAccountType(ACCOUNT_TYPE_BASIC);
        newAccount.setAmount(0);
        newAccount.setCreateTime(System.currentTimeMillis());
        newAccount.setUserId(userId);
        newAccount.setLimitRule("");
        Dal.insert(newAccount);
    }

    public static boolean createCouponAccount(int userId, String name, int amount, String rule, long expireTime) {
        UserAccountDDL newAccount = new UserAccountDDL();
        newAccount.setAccountId(IDUtil.gen("COUOPN"));
        newAccount.setAccountName(name);
        newAccount.setAccountType(ACCOUNT_TYPE_COUPON);
        newAccount.setAmount(amount);
        newAccount.setExpireTime(expireTime);
        newAccount.setCreateTime(System.currentTimeMillis());
        newAccount.setUserId(userId);
        newAccount.setLimitRule(rule);
        return Dal.insert(newAccount) > 0;
    }

    public static List<UserAccountDDL> listALLByUser(int userId) {
        Condition condition = new Condition("UserAccountDDL.userId", "=", userId);
        return Dal.select("UserAccountDDL.*", condition, null, 0, -1);
    }

    public static UserAccountDDL getBasicAccount(int userId) {
        Condition condition1 = new Condition("UserAccountDDL.userId", "=", userId);
        condition1.add(new Condition("UserAccountDDL.accountType", "=", ACCOUNT_TYPE_BASIC), "and");
        List<UserAccountDDL> list = Dal.select("UserAccountDDL.*", condition1, null, 0, 1);
        if (list == null || list.size() == 0) return null;
        return list.get(0);
    }


    public static int countValidateCoupons(int userId) {
        Condition condition1 = new Condition("UserAccountDDL.userId", "=", userId);
        condition1.add(new Condition("UserAccountDDL.expireTime", ">", System.currentTimeMillis()), "and");
        condition1.add(new Condition("UserAccountDDL.accountType", "=", ACCOUNT_TYPE_COUPON), "and");
        condition1.add(new Condition("UserAccountDDL.amount", ">", 0), "and");
        return Dal.count(condition1);
    }

    public static List<UserAccountDDL> listValidateCoupons(int userId, int page, int pageSize) {
        Condition condition1 = new Condition("UserAccountDDL.userId", "=", userId);
        condition1.add(new Condition("UserAccountDDL.expireTime", ">", System.currentTimeMillis()), "and");
        condition1.add(new Condition("UserAccountDDL.accountType", "=", ACCOUNT_TYPE_COUPON), "and");
        condition1.add(new Condition("UserAccountDDL.amount", ">", 0), "and");
        return Dal.select("UserAccountDDL.*", condition1, null, (page - 1) * pageSize, pageSize);
    }

    public static List<UserAccountDDL> listInvalidateCoupons(int userId, int page, int pageSize) {
        Condition condition1 = new Condition("UserAccountDDL.userId", "=", userId);
        condition1.add(new Condition("UserAccountDDL.expireTime", "<=", System.currentTimeMillis()), "and");
        condition1.add(new Condition("UserAccountDDL.accountType", "=", ACCOUNT_TYPE_COUPON), "and");

        Condition condition2 = new Condition("UserAccountDDL.userId", "=", userId);
        condition2.add(new Condition("UserAccountDDL.amount", "<=", 0), "and");
        condition2.add(new Condition("UserAccountDDL.accountType", "=", ACCOUNT_TYPE_COUPON), "and");

        condition1.add(condition2, "or");
        return Dal.select("UserAccountDDL.*", condition1, null, (page - 1) * pageSize, pageSize);
    }

    public static UserAccountDDL getByAccountId(String accountId) {
        if (StringUtils.isEmpty(accountId)) return null;
        Condition condition = new Condition("UserAccountDDL.accountId", "=", accountId);
        List<UserAccountDDL> list = Dal.select("UserAccountDDL.*", condition, null, 0, -1);
        if (list == null || list.size() == 0) return null;
        return list.get(0);
    }

    public static boolean reduceBalance(String accountId, int reduceBalance) {
        UserAccountDDL account = getByAccountId(accountId);
        if (account == null) return false;
        if (account.getAmount() < reduceBalance) return false;
        account.setAmount(account.getAmount() - reduceBalance);
        return Dal.update(account, "UserAccountDDL.amount",
                new Condition("UserAccountDDL.accountId", "=", accountId)
        ) > 0;
    }

    public static boolean backBalance(String accountId, int backBalance) {
        UserAccountDDL account = getByAccountId(accountId);
        if (account == null) return false;
        if (backBalance <= 0) return true;
        account.setAmount(account.getAmount() + backBalance);
        return Dal.update(account, "UserAccountDDL.amount",
                new Condition("UserAccountDDL.accountId", "=", accountId)
        ) > 0;
    }

    /**
     * 根据用户消费，获取一张满额扣减的优惠券
     *
     * @param userId
     * @param spentAmount
     * @return
     */
    public static UserAccountDDL fullReduce(int userId, int spentAmount) {
        //列出所有有效代金券
        List<UserAccountDDL> allCoupons = listValidateCoupons(userId, 1, -1);
        if (allCoupons == null || allCoupons.size() == 0) {
            return null;
        }
        for (UserAccountDDL coupon : allCoupons) {
            ShopCouponMngDDL couponMng = ShopCouponMngService.getCouponByCouponId(coupon.getAccountId());
            if (couponMng != null && StringUtils.isEmpty(couponMng.getLimitProductId())
                    && couponMng.getAmount() <= spentAmount && (couponMng.getLimitPrice() == null || couponMng.getLimitPrice() <= spentAmount)
            ) {
                return coupon;
            }
        }
        return null;
    }

    public static UserAccountDDL canUse(String accountId, String productId, int sellerId, int price) throws Exception {
        UserAccountDDL account = getByAccountId(accountId);
        if (account == null) {
            throw new Exception("账户不存在accountId=" + accountId + ",productId=" + productId);
        }

        if (account.getExpireTime() != null && account.getExpireTime() < System.currentTimeMillis()) {
            throw new Exception("账户过期accountId=" + accountId + ",productId=" + productId);
        }

        if (account.getAmount() <= 0) {
            throw new Exception("账户无余额accountId=" + accountId + ",productId=" + productId);
        }

        if (StringUtils.isEmpty(account.getLimitRule())) {
            return account;
        }

        //
		/*{ 
		    "productId":"product_1"
		    "sellerId":1156,
		    "price": 8888
		}
		*/
        ShopCouponMngDDL couponMng = ShopCouponMngService.getCouponByCouponId(account.getAccountId());
        //判断商品ID规则
        boolean productRule = true;
        String limitProductId = couponMng == null || couponMng.getLimitProductId() == null ? null : couponMng.getLimitProductId();

        if (!StringUtils.isEmpty(limitProductId) && !limitProductId.equals(productId)) {
            productRule = false;
        }
        if (!productRule) {
            throw new Exception("账户不满足商品使用条件accountId=" + accountId + ",productId=" + productId);
        }

        //判断商品ID规则
        boolean sellerRule = true;
        int limitSellerId = couponMng == null || couponMng.getLimitSellerId() == null ? null : couponMng.getLimitSellerId();
        if (limitSellerId != 0 && limitSellerId != sellerId) {
            sellerRule = false;
        }
        if (!sellerRule) {
            throw new Exception("账户不满足商家使用条件accountId=" + accountId + ",sellerId=" + sellerId);
        }
        //判断商品价格规则 满减
        int limitPrice = couponMng == null || couponMng.getLimitPrice() == null ? null : couponMng.getLimitPrice();
        if (limitPrice != 0 && price <= limitPrice) {
            throw new Exception("账户不满足额度使用条件accountId=" + accountId + ",productId=" + productId);
        }
        return account;
    }
}
