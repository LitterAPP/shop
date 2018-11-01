package modules.shop.service;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jws.dal.Dal;
import jws.dal.ResultsetHandler;
import jws.dal.sqlbuilder.Condition;
import modules.shop.ddl.ShopCarDDL;

public class ShopCarService {

    public static final int SOLD_OUT = -2;//商品下架
    public static final int DELETED = -1;//用户删除
    public static final int UN_PAYED = 1;//未结算
    public static final int PAYED = 2;//已结算

    /**
     * 将商品加入购物车
     */
    public static void addToShopCar(ShopCarDDL shopCar) {
        if (shopCar == null) return;
        Dal.replace(shopCar);
    }


    public static ShopCarDDL get(int id) {
        return Dal.select("ShopCarDDL.*", id);
    }

    /**
     * 根据唯一索引查询
     *
     * @param userId
     * @param productId
     * @param groupId
     * @param shopId
     * @return
     */
    public static ShopCarDDL find(int userId, String productId) {
        Condition cond = new Condition("ShopCarDDL.userId", "=", userId);
        cond.add(new Condition("ShopCarDDL.productId", "=", productId), "and");
        cond.add(new Condition("ShopCarDDL.status", "=", UN_PAYED), "and");
        List<ShopCarDDL> list = Dal.select("ShopCarDDL.*", cond, null, 0, 1);
        if (list == null || list.size() == 0) return null;
        return list.get(0);
    }

    /**
     * 我的购物车
     *
     * @param userId
     * @return
     */
    public static List<ShopCarDDL> myCarList(int userId, int page, int pageSize) {
        Condition cond = new Condition("ShopCarDDL.userId", "=", userId);
        cond.add(new Condition("ShopCarDDL.status", "in", Arrays.asList(UN_PAYED)), "and");
        List<ShopCarDDL> list = Dal.select("ShopCarDDL.*", cond, null, (page - 1) * pageSize, pageSize);
        return list;
    }

    /**
     * 更新状态
     *
     * @param id
     * @param userId
     * @param status
     */
    public static void updateStatus(int id, int userId, int status) {
        ShopCarDDL car = get(id);
        if (car == null) return;
        car.setStatus(status);
        Condition cond = new Condition("ShopCarDDL.id", "=", id);
        cond.add(new Condition("ShopCarDDL.userId", "=", userId), "and");
        Dal.update(car, "ShopCarDDL.status", cond);
    }

    public static void productUnSell(String productId) {
        Dal.executeNonQuery(ShopCarDDL.class, "update shop_car set status = " + SOLD_OUT + " where product_id='" + productId + "'");
    }


    public static void updateBuyNum(int id, int userId, int buyNum) {
        ShopCarDDL car = get(id);
        if (car == null) return;
        car.setBuyNum(buyNum);
        Condition cond = new Condition("ShopCarDDL.id", "=", id);
        cond.add(new Condition("ShopCarDDL.userId", "=", userId), "and");
        Dal.update(car, "ShopCarDDL.buyNum", cond);
    }

    public static List<ShopCarDDL> findByCarIds(String[] ids) {
        List<Integer> carIdList = new ArrayList<Integer>();
        for (String idstr : ids) {
            carIdList.add(Integer.parseInt(idstr));
        }
        Condition cond = new Condition("ShopCarDDL.id", "in", carIdList);
        return Dal.select("ShopCarDDL.*", cond, null, 0, -1);
    }
}
