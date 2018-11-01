package modules.shop.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import jws.cache.Cache;
import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import jws.dal.sqlbuilder.Sort;
import modules.shop.ddl.ShopCouponMngDDL;
import util.IDUtil;

public class ShopCouponMngService {
    public static final int COUPON_PLATFROM = 0;
    public static final int COUPON_NONE_PLATFROM = 1;

    public static ShopCouponMngDDL get(int id) {
        return Dal.select("ShopCouponMngDDL.*", id);
    }

    public static boolean getCoupon(int couponId, int userId) {
        ShopCouponMngDDL coupon = get(couponId);
        if (coupon == null) return false;
        String key = "GET_COUPON_" + userId + "_" + couponId;
        Object value = Cache.get(key);
        int gets = value == null ? 0 : Integer.parseInt(String.valueOf(value));
        if (gets >= coupon.getLimitTimes()) {
            return false;
        }
		/*{ 
		    "productId":"product_1"
		    "sellerId":1156,
		    "price": 8888
		}
		*/
        String ruleTemp = "{\"productId\":\"%s\",\"sellerId\":%s,\"price\":%s}";
        String rule = String.format(ruleTemp,
                coupon.getLimitProductId() == null ? "" : coupon.getLimitProductId(),
                coupon.getLimitSellerId() == null ? 0 : coupon.getLimitSellerId(),
                coupon.getLimitPrice() == null ? 0 : coupon.getLimitPrice());

        boolean result = UserAccountService.createCouponAccount(userId, coupon.getCouponName(), coupon.getAmount(),
                rule, coupon.getExpireTime());
        Cache.set(key, gets + 1, "28d");
        return result;
    }

    /**
     * 优先选择可领取的券活动
     *
     * @param productId
     * @return
     */
    public static List<ShopCouponMngDDL> selectCouponActivities(String shopId, String productId, int sellerId, int count) {

        List<ShopCouponMngDDL> result = new ArrayList<ShopCouponMngDDL>();
        if (count < 0) {
            return result;
        }

        //优先根据店铺过滤符合条件的代金券


        //优先根据商品ID过滤符合条件的
        if (!StringUtils.isEmpty(productId)) {
            count -= result.size();
            if (count <= 0) {
                return result;
            }

            Condition cond = new Condition("ShopCouponMngDDL.startTime", "<=", System.currentTimeMillis());
            cond.add(new Condition("ShopCouponMngDDL.endTime", ">", System.currentTimeMillis()), "and");
            cond.add(new Condition("ShopCouponMngDDL.expireTime", ">", System.currentTimeMillis()), "and");
            Sort sort = new Sort("ShopCouponMngDDL.expireTime", false);
            cond.add(new Condition("ShopCouponMngDDL.couponType", "=", COUPON_NONE_PLATFROM), "and");
            cond.add(new Condition("ShopCouponMngDDL.limitProductId", "=", productId), "and");

            if (!StringUtils.isEmpty(shopId)) {
                cond.add(new Condition("ShopCouponMngDDL.shopId", "=", shopId), "and");
            }


            List<ShopCouponMngDDL> list = Dal.select("ShopCouponMngDDL.*", cond, sort, 0, count);
            if (list != null && list.size() >= 0) {
                result.addAll(list);
            }

        }

        if (sellerId != 0) {

            count -= result.size();
            if (count <= 0) {
                return result;
            }
            Condition cond = new Condition("ShopCouponMngDDL.startTime", "<=", System.currentTimeMillis());
            cond.add(new Condition("ShopCouponMngDDL.endTime", ">", System.currentTimeMillis()), "and");
            cond.add(new Condition("ShopCouponMngDDL.expireTime", ">", System.currentTimeMillis()), "and");
            Sort sort = new Sort("ShopCouponMngDDL.expireTime", false);
            cond.add(new Condition("ShopCouponMngDDL.couponType", "=", COUPON_NONE_PLATFROM), "and");
            cond.add(new Condition("ShopCouponMngDDL.limitSellerId", "=", sellerId), "and");

            if (!StringUtils.isEmpty(shopId)) {
                cond.add(new Condition("ShopCouponMngDDL.shopId", "=", shopId), "and");
            }

            List<ShopCouponMngDDL> list = Dal.select("ShopCouponMngDDL.*", cond, sort, 0, count);
            if (list != null && list.size() >= 0) {
                result.addAll(list);
            }
        }


        count -= result.size();
        if (count <= 0) {
            return result;
        }
        Condition cond = new Condition("ShopCouponMngDDL.startTime", "<=", System.currentTimeMillis());
        cond.add(new Condition("ShopCouponMngDDL.endTime", ">", System.currentTimeMillis()), "and");
        cond.add(new Condition("ShopCouponMngDDL.expireTime", ">", System.currentTimeMillis()), "and");

        if (!StringUtils.isEmpty(shopId)) {
            cond.add(new Condition("ShopCouponMngDDL.shopId", "=", shopId), "and");
        }

        Sort sort = new Sort("ShopCouponMngDDL.expireTime", false);
        //cond.add(new Condition("ShopCouponMngDDL.couponType","=",COUPON_PLATFROM), "and");
        List<ShopCouponMngDDL> list = Dal.select("ShopCouponMngDDL.*", cond, sort, 0, count);
        if (list != null && list.size() > 0) {
            result.addAll(list);
        }
        return result;
    }

    public static ShopCouponMngDDL getCouponByCouponId(String couponId) {
        Condition cond = new Condition("ShopCouponMngDDL.couponId", "=", couponId);
        List<ShopCouponMngDDL> list = Dal.select("ShopCouponMngDDL.*", cond, null, 0, -1);
        if (list == null || list.size() == 0) return null;
        return list.get(0);
    }

    public static List<ShopCouponMngDDL> listCoupon(String shopId, String couponId, String keyword, int page, int pageSize) {
        Condition cond = new Condition("ShopCouponMngDDL.id", ">", 0);
        if (!StringUtils.isEmpty(shopId)) {
            cond.add(new Condition("ShopCouponMngDDL.shopId", "=", shopId), "and");
        }

        if (!StringUtils.isEmpty(couponId)) {
            cond.add(new Condition("ShopCouponMngDDL.couponId", "=", couponId), "and");
        }
        if (!StringUtils.isEmpty(keyword)) {
            cond.add(new Condition("ShopCouponMngDDL.couponName", "like", "%" + keyword + "%"), "and");
        }
        Sort sort = new Sort("ShopCouponMngDDL.id", false);
        return Dal.select("ShopCouponMngDDL.*", cond, sort, (page - 1) * pageSize, pageSize);
    }

    public static int countCoupon(String shopId, String couponId, String keyword) {
        Condition cond = new Condition("ShopCouponMngDDL.id", ">", 0);
        if (!StringUtils.isEmpty(shopId)) {
            cond.add(new Condition("ShopCouponMngDDL.shopId", "=", shopId), "and");
        }

        if (!StringUtils.isEmpty(couponId)) {
            cond.add(new Condition("ShopCouponMngDDL.couponId", "=", couponId), "and");
        }
        if (!StringUtils.isEmpty(keyword)) {
            cond.add(new Condition("ShopCouponMngDDL.couponName", "like", "%" + keyword + "%"), "and");
        }
        return Dal.count(cond);
    }

    public static void replace(String shopId, String couponId, String couponName, int amount, String limitProductId,
                               int limitSellerId, int limitPrice, int limitTimes, long expireTime, long startTime, long endTime) {
        ShopCouponMngDDL coupon = new ShopCouponMngDDL();
        coupon.setCouponId(couponId);
        coupon.setShopId(shopId);
        if (!StringUtils.isEmpty(couponId)) {
            List<ShopCouponMngDDL> olds = listCoupon(shopId, couponId, null, 1, 1);
            if (olds != null && olds.size() > 0) {
                coupon = olds.get(0);
            }
        }
        if (StringUtils.isEmpty(coupon.getCouponId())) {
            coupon.setCreateTime(System.currentTimeMillis());
            coupon.setCouponId(IDUtil.gen("COP"));
        }
        coupon.setCouponName(couponName);
        coupon.setAmount(amount);
        coupon.setLimitPrice(limitPrice == 0 ? null : limitPrice);
        coupon.setLimitSellerId(limitSellerId == 0 ? null : limitSellerId);
        coupon.setLimitProductId(StringUtils.isEmpty(limitProductId) ? null : limitProductId);
        coupon.setLimitTimes(limitTimes);
        coupon.setExpireTime(expireTime);
        coupon.setStartTime(startTime);
        coupon.setEndTime(endTime);
        coupon.setCouponType(1);
        Dal.replace(coupon);
    }
}
