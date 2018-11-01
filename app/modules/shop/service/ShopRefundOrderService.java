package modules.shop.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import jws.dal.sqlbuilder.Sort;
import modules.shop.ddl.ShopRefundOrderDDL;

public class ShopRefundOrderService {

    public static void replaceRefundOrder(ShopRefundOrderDDL refundOrder) {
        Dal.replace(refundOrder);
    }

    /**
     * @param trancationId 微信订单号
     * @param outTradeNo   商户订单号
     * @param refundId     微信退款单号
     * @param outRefundNo  商户退款单号
     * @return
     */
    public static List<ShopRefundOrderDDL> listByNo(String shpoId, String transactionId, String outTradeNo,
                                                    String refundId, String outRefundNo, int page, int pageSize) {
        Condition cond = new Condition("ShopRefundOrderDDL.shopId", "=", shpoId);
        if (!StringUtils.isEmpty(transactionId)) {
            cond.add(new Condition("ShopRefundOrderDDL.transactionId", "=", transactionId), "and");
        }
        if (!StringUtils.isEmpty(outTradeNo)) {
            cond.add(new Condition("ShopRefundOrderDDL.outTradeNo", "=", outTradeNo), "and");
        }
        if (!StringUtils.isEmpty(refundId)) {
            cond.add(new Condition("ShopRefundOrderDDL.refundId", "=", refundId), "and");
        }

        if (!StringUtils.isEmpty(outRefundNo)) {
            cond.add(new Condition("ShopRefundOrderDDL.outRefundNo", "=", outRefundNo), "and");
        }

        return Dal.select("ShopRefundOrderDDL.*", cond, new Sort("ShopRefundOrderDDL.id", false), (page - 1) * pageSize, pageSize);
    }


    public static int countByNo(String shpoId, String transactionId, String outTradeNo,
                                String refundId, String outRefundNo) {
        Condition cond = new Condition("ShopRefundOrderDDL.shopId", "=", shpoId);
        if (!StringUtils.isEmpty(transactionId)) {
            cond.add(new Condition("ShopRefundOrderDDL.transactionId", "=", transactionId), "and");
        }
        if (!StringUtils.isEmpty(outTradeNo)) {
            cond.add(new Condition("ShopRefundOrderDDL.outTradeNo", "=", outTradeNo), "and");
        }
        if (!StringUtils.isEmpty(refundId)) {
            cond.add(new Condition("ShopRefundOrderDDL.refundId", "=", refundId), "and");
        }

        if (!StringUtils.isEmpty(outRefundNo)) {
            cond.add(new Condition("ShopRefundOrderDDL.outRefundNo", "=", outRefundNo), "and");
        }
        return Dal.count(cond);
    }
}
