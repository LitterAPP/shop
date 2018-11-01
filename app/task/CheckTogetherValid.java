package task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jws.Jws;
import jws.Logger;
import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import modules.shop.ddl.UsersDDL;
import modules.shop.ddl.ShopOrderDDL;
import modules.shop.ddl.ShopTogetherDDL;
import modules.shop.ddl.ShopTogetherJoinerDDL;
import modules.shop.ddl.UserAccountDDL;
import modules.shop.service.ShopOrderService;
import modules.shop.service.ShopTogetherService;
import modules.shop.service.UserAccountService;
import modules.shop.service.UserService;
import util.API;
import util.AmountUtil;

public class CheckTogetherValid implements Runnable {

    @Override
    public void run() {
        try {
            List<ShopTogetherDDL> list = ShopTogetherService.listUnDone();
            if (list == null || list.size() == 0) return;
            for (ShopTogetherDDL together : list) {

                ShopTogetherService.expireTogether(together);

                List<ShopTogetherJoinerDDL> joners = ShopTogetherService.listJoinerByTogetherId(together.getTogetherId());
                if (joners == null || joners.size() == 0) continue;

                Map<String, Map> dataMap = new HashMap<String, Map>();


                Map<String, String> k1 = new HashMap<String, String>();
                k1.put("value", "[拼团未成功]" + together.getProductName());
                k1.put("color", "#000033");

                Map<String, String> k3 = new HashMap<String, String>();
                k3.put("value", together.getTogetherNumber() + "");
                k3.put("color", "#000033");

                Map<String, String> k4 = new HashMap<String, String>();
                k4.put("value", (together.getTogetherNumber() - together.getTogetherNumberResidue()) + "");
                k4.put("color", "#000033");

                Map<String, String> k5 = new HashMap<String, String>();
                k5.put("value", "过期时间到任未成团");
                k5.put("color", "#000033");


                Map<String, String> k6 = new HashMap<String, String>();
                k6.put("value", "拼团失败已退款到平台账户(代金券不退款)");
                k6.put("color", "#000033");

                for (ShopTogetherJoinerDDL joner : joners) {
                    ShopOrderDDL order = ShopOrderService.findByOrderId(joner.getOrderId());
                    if (order == null) continue;
                    order.setStatus(ShopOrderService.ORDER_TOGETHER_NOT_FULL);
                    //更新订单状态
                    Dal.update(order, "ShopOrderDDL.status", new Condition("ShopOrderDDL.orderId", "=", order.getOrderId()));

                    UsersDDL buyer = UserService.get(order.getBuyerUserId());

                    //退款现金
                    Logger.info("用户[%s]平台未成功，退款，现金支付 [%s],余额支付[%s]", buyer.getId(), order.getUseCash(), order.getUseUserAmount());
                    UserAccountDDL account = UserAccountService.getBasicAccount(buyer.getId().intValue());
                    if (account == null) {
                        Logger.info("用户[%s]平台未成功，退款失败，账户不存，现金支付 [%s],余额支付[%s]", buyer.getId(), order.getUseCash(), order.getUseUserAmount());
                    } else {
                        int back = (order.getUseCash() == null ? 0 : order.getUseCash()) + (order.getUseUserAmount() == null ? 0 : order.getUseUserAmount());
                        boolean result = UserAccountService.backBalance(account.getAccountId(), back);
                        Logger.info("用户[%s]平台未成功，退款[%s]，账户不存，现金支付 [%s],余额支付[%s]", buyer.getId(), result, order.getUseCash(), order.getUseUserAmount());
                    }

                    Map<String, String> k2 = new HashMap<String, String>();
                    int amount = (order.getUseCash() == null ? 0 : order.getUseCash()) + (order.getUseCouponAmount() == null ? 0 : order.getUseCouponAmount()) + (order.getUseUserAmount() == null ? 0 : order.getUseUserAmount());
                    k2.put("value", AmountUtil.f2y(amount) + "元");
                    k2.put("color", "#000033");


                    dataMap.put("keyword1", k1);
                    dataMap.put("keyword2", k2);
                    dataMap.put("keyword3", k3);
                    dataMap.put("keyword4", k4);
                    dataMap.put("keyword5", k5);
                    dataMap.put("keyword6", k6);

                    JsonObject parsms = new JsonParser().parse(order.getLitterAppParams()).getAsJsonObject();
                    String packagestr = parsms.get("package").getAsString();
                    String page = "pages/shop/orderdetail?orderId=" + order.getOrderId();

                    API.sendWxMessage(parsms.get("appId").getAsString(), buyer.getOpenId(), Jws.configuration.getProperty("wx.msg.template.id.jspt"), page, packagestr.split("=")[1], dataMap);
                }
            }
        } catch (Exception e) {
            Logger.error(e, e.getMessage());
        }
    }

}
