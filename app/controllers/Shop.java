package controllers;

import java.io.IOException;
import java.io.StringWriter;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import controllers.dto.SelectSourceDto;
import dto.shop.ShopIndexDto;
import dto.shop.ShopIndexDto.ShopNavWrap;
import dto.shop.ShopNavDto;
import jws.Jws;
import jws.Logger;
import jws.cache.Cache;
import jws.dal.Dal;
import jws.mvc.Controller;
import modules.shop.ddl.ShopCarDDL;
import modules.shop.ddl.ShopCouponMngDDL;
import modules.shop.ddl.ShopExpressDDL;
import modules.shop.ddl.ShopIndexDDL;
import modules.shop.ddl.ShopOrderDDL;
import modules.shop.ddl.ShopProductAttrRelDDL;
import modules.shop.ddl.ShopProductCommunityRelDDL;
import modules.shop.ddl.ShopProductDDL;
import modules.shop.ddl.ShopProductGroupDDL;
import modules.shop.ddl.ShopProductImagesDDL;
import modules.shop.ddl.ShopTogetherDDL;
import modules.shop.ddl.ShopTogetherJoinerDDL;
import modules.shop.ddl.ShopWetaoCommentDDL;
import modules.shop.ddl.ShopWetaoDDL;
import modules.shop.ddl.UserAccountDDL;
import modules.shop.ddl.UsersDDL;
import modules.shop.service.ShopCarService;
import modules.shop.service.ShopCategoryService;
import modules.shop.service.ShopCommunityService;
import modules.shop.service.ShopCouponMngService;
import modules.shop.service.ShopExpressService;
import modules.shop.service.ShopIndexService;
import modules.shop.service.ShopOrderService;
import modules.shop.service.ShopProductAttrService;
import modules.shop.service.ShopProductGroupService;
import modules.shop.service.ShopProductImageService;
import modules.shop.service.ShopProductService;
import modules.shop.service.ShopTogetherService;
import modules.shop.service.ShopWeTaoCommentService;
import modules.shop.service.ShopWeTaoService;
import modules.shop.service.UserAccountService;
import modules.shop.service.UserService;
import util.API;
import util.AmountUtil;
import util.DateUtil;
import util.EncryptUtil;
import util.IDUtil;
import util.MD5Util;
import util.RtnUtil;

public class Shop extends Controller {
    private static Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    public static void createCarOrder(String session, String carIds,
                                      String referScene, String referAppId, String referChannel) {
        try {
            UsersDDL user = UserService.findBySession(session);
            if (user == null) {
                renderJSON(RtnUtil.returnLoginFail());
            }
            if (Cache.get("ORDER_USER_" + user.getId()) != null) {
                renderJSON(RtnUtil.returnFail("操作频繁,请10秒后再试"));
            }
            Cache.set("ORDER_USER_" + user.getId(), "1", "10s");

            String orderId = IDUtil.gen("QL");
            boolean flag = ShopOrderService.createOrder(false, null, "", "", 0, 0, 0,
                    0, orderId, user.getId().intValue(), null, "", null,
                    null, referScene, referAppId, referChannel, carIds, 1);
            if (flag) {
                renderJSON(RtnUtil.returnSuccess("OK", orderId));
            }
            renderJSON(RtnUtil.returnFail("创建订单失败"));
        } catch (Exception e) {
            Logger.error(e, e.getMessage());
            renderJSON(RtnUtil.returnFail(e.getMessage()));
        }
    }

    public static void carPayPage(String session, String orderId) {
        try {
            UsersDDL user = UserService.findBySession(session);
            if (user == null) {
                renderJSON(RtnUtil.returnLoginFail());
            }
            ShopOrderDDL order = ShopOrderService.findByOrderId(orderId);
            if (order == null) {
                renderJSON(RtnUtil.returnFail("订单不存在"));
            }

            if (order.getStatus() != ShopOrderService.ORDER_PAYING) {
                renderJSON(RtnUtil.returnFail("订单暂不能支付"));
            }

            if (order.getOrderType() != 1) {
                renderJSON(RtnUtil.returnFail("非合并支付订单"));
            }

            if (StringUtils.isEmpty(order.getCarIds())) {
                renderJSON(RtnUtil.returnFail("不存在购物IDs"));
            }
            List<ShopCarDDL> carList = ShopCarService.findByCarIds(order.getCarIds().split(","));


            Map<String, Object> result = new HashMap<String, Object>();
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

            if (carList == null || carList.size() == 0) {
                result.put("list", list);
                renderJSON(RtnUtil.returnSuccess("OK", result));
            }
            int sumAmount = 0;
            for (ShopCarDDL car : carList) {
                Map<String, Object> one = new HashMap<String, Object>();
                ShopProductDDL product = ShopProductService.getByProductId(car.getProductId());
                ShopProductGroupDDL group = ShopProductGroupService.findByProductIdAndGroupId(car.getProductId(), car.getGroupId());
                if (group == null || product == null) continue;
                one.put("id", car.getId());
                one.put("productId", product.getProductId());
                one.put("groupId", group.getGroupId());
                one.put("productName", product.getProductName());
                one.put("groupImgage", API.getObjectAccessUrlSimple(group.getGroupImage()));
                one.put("buyNum", car.getBuyNum());
                one.put("status", car.getStatus());
                one.put("groupName", group.getGroupName());
                if (product.getJoinSeckilling() != null && product.getJoinSeckilling() == 1) {
                    one.put("totalAmount", AmountUtil.f2y(product.getSeckillingPrice() * car.getBuyNum()));//实时
                    one.put("singPrice", AmountUtil.f2y(product.getSeckillingPrice()));//实时
                    sumAmount += group.getGroupPrice() * car.getBuyNum();
                } else {
                    one.put("totalAmount", AmountUtil.f2y(group.getGroupPrice() * car.getBuyNum()));//实时
                    one.put("singPrice", AmountUtil.f2y(group.getGroupPrice()));//实时
                    sumAmount += group.getGroupPrice() * car.getBuyNum();
                }

                one.put("checked", true);


                list.add(one);
            }

            result.put("sumAmount", AmountUtil.f2y(sumAmount));
            result.put("list", list);

            UserAccountDDL account = UserAccountService.getBasicAccount(user.getId().intValue());
            Map<String, Object> accountMap = new HashMap<String, Object>();
            accountMap.put("accountId", account.getAccountId());
            accountMap.put("amount", AmountUtil.f2y(account.getAmount()));
            result.put("account", accountMap);

            renderJSON(RtnUtil.returnSuccess("OK", result));
        } catch (Exception e) {
            Logger.error(e, e.getMessage());
            renderJSON(RtnUtil.returnFail(e.getMessage()));
        }
    }

    public static void payCarOrder(String session, String appid, String couponAccountId, String userAccountId, String orderId, String address, String memo) {
        try {
            UsersDDL user = UserService.findBySession(session);
            if (user == null) {
                renderJSON(RtnUtil.returnLoginFail());
            }
            if (Cache.get("PAYING_USER_" + user.getId()) != null) {
                renderJSON(RtnUtil.returnFail("操作频繁,请10秒后再试"));
            }
            Cache.set("PAYING_USER_" + user.getId(), "1", "10s");

            ShopOrderDDL order = ShopOrderService.findByOrderId(orderId);
            if (order == null) {
                renderJSON(RtnUtil.returnFail("订单不存在"));
            }

            if (order.getStatus() != ShopOrderService.ORDER_PAYING) {
                renderJSON(RtnUtil.returnFail("订单暂不能支付"));
            }

            if (order.getOrderType() != 1) {
                renderJSON(RtnUtil.returnFail("非合并支付订单"));
            }

            if (StringUtils.isEmpty(order.getCarIds())) {
                renderJSON(RtnUtil.returnFail("不存在购物IDs"));
            }
            if (StringUtils.isEmpty(address)) {
                renderJSON(RtnUtil.returnFail("收货地址为空"));
            }
            order.setAddress(address);
            if (!StringUtils.isEmpty(memo)) {
                order.setMemo(ShopOrderService.genMemo(order, memo));
            }

            List<ShopCarDDL> carList = ShopCarService.findByCarIds(order.getCarIds().split(","));
            if (carList == null || carList.size() == 0) {
                renderJSON(RtnUtil.returnFail("数据异常"));
            }
            //总共需要支付
            int totalAmount = 0;
            int totalBuyNum = 0;
            StringBuffer productNames = new StringBuffer();
            StringBuffer groupNames = new StringBuffer();
            //商品组合
            for (ShopCarDDL car : carList) {
                ShopProductDDL product = ShopProductService.getByProductId(car.getProductId());
                if (product == null || product.getStatus() != 1 || product.getStore() <= 0 || product.getStore() < car.getBuyNum()) {
                    renderJSON(RtnUtil.returnFail("商品已下架或库存不足"));
                }

                ShopProductGroupDDL productGroup = ShopProductGroupService.findByProductIdAndGroupId(car.getProductId(), car.getGroupId());
                if (productGroup == null) {
                    renderJSON(RtnUtil.returnFail("商品组已下架"));
                }


                productNames.append(product.getProductName());
                groupNames.append(productGroup.getGroupName()).append(" x").append(car.getBuyNum()).append(" | ");

                int price = productGroup.getGroupPrice();

                if (product.getJoinSeckilling() != null && product.getJoinSeckilling() == 1
                        && getSecKillingEndTimes(product.getSeckillingTime()) > System.currentTimeMillis()
                ) {
                    price = product.getSeckillingPrice();
                }
                //
                totalAmount += price * car.getBuyNum();
                totalBuyNum += car.getBuyNum();
            }

            Map<String, Object> result = new HashMap<String, Object>();

            result.put("orderId", orderId);

            order.setProductName(productNames.toString());
            order.setGroupName(groupNames.toString());
            order.setBuyNum(totalBuyNum);

            //不需要任何支付
            if (0 == totalAmount) {
                result.put("needPay", false);
                result.put("useBalance", 0);

                order.setStatus(ShopOrderService.ORDER_PAYED);
                order.setPayTime(System.currentTimeMillis());

                if (Dal.replace(order) > 0) {
                    ShopOrderService.paySuccess(order);
                    result.put("order", order);
                    renderJSON(RtnUtil.returnSuccess("OK", result));
                } else {
                    renderJSON(RtnUtil.returnFail("支付失败"));
                }
            }

            int reduceCoupon = 0;
            int reduceUser = 0;
            int diffPay = totalAmount;

            UserAccountDDL couponAccount = null;
            if (!StringUtils.isEmpty(couponAccountId)) {
                //优先使用代金券类型账户
                couponAccount = UserAccountService.canUse(couponAccountId, "", 0, totalAmount);
                if (couponAccount == null) {
                    renderJSON(RtnUtil.returnFail("代金券无法使用编号:" + couponAccountId));
                }
            }

            boolean reduceCoupnd = true;
            //先扣代金券金额
            if (couponAccount != null) {
                diffPay = totalAmount - couponAccount.getAmount();

            }

            //代金券已经够扣了
            if (diffPay <= 0) {
                reduceCoupon = totalAmount;
                result.put("needPay", false);
                result.put("useUserBalance", 0);
                result.put("useCouponBalance", reduceCoupon);


                order.setStatus(ShopOrderService.ORDER_PAYED);
                order.setPayTime(System.currentTimeMillis());


                if (couponAccount != null) {
                    order.setUseCouponAccountId(couponAccountId);
                    order.setUseCouponAmount(reduceCoupon);
                    reduceCoupnd = UserAccountService.reduceBalance(couponAccountId, reduceCoupon);
                }

                if (reduceCoupnd && Dal.replace(order) > 0) {
                    ShopOrderService.paySuccess(order);
                    result.put("order", order);
                    renderJSON(RtnUtil.returnSuccess("OK", result));
                } else {
                    renderJSON(RtnUtil.returnFail("支付失败"));
                }
            }

            //说明使用了代金券，但不够支付,全部扣除
            if (couponAccount != null) {
                order.setUseCouponAccountId(couponAccountId);
                order.setUseCouponAmount(couponAccount.getAmount());//全部使用完代金券
                reduceCoupnd = UserAccountService.reduceBalance(couponAccountId, couponAccount.getAmount());
            }
            boolean reduceUserAccount = true;
            //使用用户余额账户扣减
            UserAccountDDL userAccount = UserAccountService.getByAccountId(userAccountId);
            if (userAccount != null) {
                diffPay = diffPay - (userAccount == null ? 0 : userAccount.getAmount());
                //余额扣够了
                if (diffPay <= 0) {
                    reduceUser = totalAmount - reduceCoupon;
                    result.put("needPay", false);
                    result.put("useUserBalance", reduceUser);
                    result.put("useCouponBalance", reduceCoupon);

                    order.setUseUserAccountId(userAccount.getAccountId());
                    order.setUseUserAmount(reduceUser);

                    order.setStatus(ShopOrderService.ORDER_PAYED);
                    order.setPayTime(System.currentTimeMillis());

                    reduceUserAccount = UserAccountService.reduceBalance(userAccount.getAccountId(), reduceUser);

                    if (reduceUserAccount && Dal.replace(order) > 0) {
                        ShopOrderService.paySuccess(order);
                        result.put("order", order);
                        renderJSON(RtnUtil.returnSuccess("OK", result));
                    } else {
                        renderJSON(RtnUtil.returnFail("支付失败"));
                    }
                }
            }

            //使用了余额 还不够扣除
            if (userAccount != null) {
                reduceUser = userAccount.getAmount();
                order.setUseUserAccountId(userAccount.getAccountId());
                order.setUseUserAmount(reduceUser);
                reduceUserAccount = UserAccountService.reduceBalance(userAccount.getAccountId(), reduceUser);
            }

            String body = Jws.configuration.getProperty(appid + ".body.prefix") + "-" + order.getOrderId();
            String spbill_create_ip = InetAddress.getLocalHost().getHostAddress();
            String notify_url = Jws.configuration.getProperty(appid + ".notify.url");
            String trade_type = "JSAPI";
            String key = Jws.configuration.getProperty(appid + ".pay.key");


            Map<String, Object> ext = new HashMap<String, Object>();
            ext.put("openid", user.getOpenId());
            Map<String, String> wxResult = API.weixin_unifiedorder(appid, Jws.configuration.getProperty(appid + ".mch_id"),
                    body, orderId, diffPay, spbill_create_ip,
                    notify_url, trade_type, key, ext);
            String prepay_id = wxResult.get("prepay_id");
            String nonce_str = wxResult.get("nonce_str");
            Map<String, String> litterPayParams = API.getLitterAppPayParams(appid, prepay_id, key, nonce_str);
            String jsonstr = litterPayParams != null ? gson.toJson(litterPayParams) : null;

            order.setUseCash(diffPay);
            order.setStatus(ShopOrderService.ORDER_PAYING);
            order.setPayTime(System.currentTimeMillis());

            //扣减用户余额支付部分
            if (reduceCoupnd && reduceUserAccount && Dal.replace(order) > 0) {
                ShopOrderService.paySuccess(order);
                result.put("useUserBalance", reduceUser);
                result.put("useCouponBalance", reduceCoupon);
                result.put("useCash", diffPay);
                result.put("needPay", true);
                result.put("order", order);
                result.put("litterPayParams", litterPayParams);
                renderJSON(RtnUtil.returnSuccess("OK", result));
            } else {
                renderJSON(RtnUtil.returnFail("支付失败"));
            }

        } catch (Exception e) {
            Logger.error(e, e.getMessage());
            renderJSON(RtnUtil.returnFail(e.getMessage()));
        }
    }

    public static void createOrder(String session, String productId, String groupId, String appid, int buyNum,
                                   String userAccountId, String couponAccountId, boolean together, String togetherId, String address,
                                   String referScene, String referAppId, String referChannel
    ) {
        try {
            if (buyNum == 0) buyNum = 1;

            UsersDDL user = UserService.findBySession(session);
            if (user == null) {
                renderJSON(RtnUtil.returnLoginFail());
            }

            if (StringUtils.isEmpty(address)) {
                renderJSON(RtnUtil.returnFail("请填写收货地址"));
            }

            if (Cache.get("PAYING_USER_" + user.getId()) != null) {
                renderJSON(RtnUtil.returnFail("操作频繁,请10秒后再试"));
            }
            Cache.set("PAYING_USER_" + user.getId(), "1", "10s");

            ShopProductDDL product = ShopProductService.getByProductId(productId);
            if (product == null || product.getStatus() != 1 || product.getStore() <= 0 || product.getStore() < buyNum) {
                renderJSON(RtnUtil.returnFail("商品已下架或库存不足"));
            }

            ShopProductGroupDDL productGroup = ShopProductGroupService.findByProductIdAndGroupId(productId, groupId);
            if (productGroup == null) {
                renderJSON(RtnUtil.returnFail("商品组已下架"));
            }

            if (together && product.getJoinTogether() != 1) {
                renderJSON(RtnUtil.returnFail("商品不支持拼团"));
            }
            //int balance = useBalance?user.getBalance():0;

            Map<String, Object> result = new HashMap<String, Object>();
            String out_trade_no = IDUtil.gen("QL");
            result.put("orderId", out_trade_no);

            //总共需要支付
            int totalAmount = 0;
            if (product.getJoinSeckilling() != null && product.getJoinSeckilling() == 1) {
                totalAmount = product.getSeckillingPrice() * buyNum;
            } else if (together && product.getJoinTogether() == 1) {
                totalAmount = productGroup.getGroupTogetherPrice() * buyNum;
            } else {
                totalAmount = productGroup.getGroupPrice() * buyNum;
            }

            //不需要任何支付
            if (0 == totalAmount) {
                result.put("needPay", false);
                result.put("useBalance", 0);
                boolean order = ShopOrderService.createOrder(false, null, null, null, 0, 0, 0,
                        buyNum, out_trade_no, user.getId().intValue(), null, address, product, productGroup,
                        referScene, referAppId, referChannel
                );
                result.put("order", order);
                renderJSON(RtnUtil.returnSuccess("OK", result));
            }
            int reduceCoupon = 0;
            int reduceUser = 0;
            int diffPay = totalAmount;
            UserAccountDDL couponAccount = null;

            if (!StringUtils.isEmpty(couponAccountId)) {
                //优先使用代金券类型账户
                couponAccount = UserAccountService.canUse(couponAccountId, productId, product.getSellerUserId(), totalAmount);
                if (couponAccount == null) {
                    renderJSON(RtnUtil.returnFail("代金券无法使用编号:" + couponAccountId));
                }
            }

            //先扣代金券金额
            if (couponAccount != null) {
                diffPay = totalAmount - couponAccount.getAmount();
            }
            //代金券已经够扣了
            if (diffPay <= 0) {
                reduceCoupon = totalAmount;
                result.put("needPay", false);
                result.put("useUserBalance", 0);
                result.put("useCouponBalance", reduceCoupon);
                boolean order = ShopOrderService.createOrder(together, togetherId, null,
                        couponAccountId, 0, reduceCoupon, 0, buyNum, out_trade_no, user.getId().intValue(), null, address, product, productGroup,
                        referScene, referAppId, referChannel
                );
                result.put("order", order);
                renderJSON(RtnUtil.returnSuccess("OK", result));
            }

            //说明使用了代金券，但不够支付,全部扣除
            if (couponAccount != null) {
                reduceCoupon = couponAccount.getAmount();
            }

            //使用用户余额账户扣减
            UserAccountDDL userAccount = UserAccountService.getByAccountId(userAccountId);
            if (userAccount != null) {
                diffPay = diffPay - (userAccount == null ? 0 : userAccount.getAmount());
                //余额扣够了
                if (diffPay <= 0) {
                    reduceUser = totalAmount - reduceCoupon;
                    result.put("needPay", false);
                    result.put("useUserBalance", reduceUser);
                    result.put("useCouponBalance", reduceCoupon);
                    boolean order = ShopOrderService.createOrder(together, togetherId, userAccountId, couponAccountId,
                            reduceUser, reduceCoupon, 0, buyNum, out_trade_no, user.getId().intValue(), null, address, product, productGroup,
                            referScene, referAppId, referChannel
                    );
                    result.put("order", order);
                    renderJSON(RtnUtil.returnSuccess("OK", result));
                }
            }

            //使用了余额 还不够扣除
            if (userAccount != null) {
                reduceUser = userAccount.getAmount();
            }

            String body = Jws.configuration.getProperty(appid + ".body.prefix") + "-" + product.getProductCategory();
            String spbill_create_ip = InetAddress.getLocalHost().getHostAddress();
            String notify_url = Jws.configuration.getProperty(appid + ".notify.url");
            String trade_type = "JSAPI";
            String key = Jws.configuration.getProperty(appid + ".pay.key");


            Map<String, Object> ext = new HashMap<String, Object>();
            ext.put("openid", user.getOpenId());
            Map<String, String> wxResult = API.weixin_unifiedorder(appid, Jws.configuration.getProperty(appid + ".mch_id"),
                    body, out_trade_no, diffPay, spbill_create_ip,
                    notify_url, trade_type, key, ext);
            String prepay_id = wxResult.get("prepay_id");
            String nonce_str = wxResult.get("nonce_str");
            Map<String, String> litterPayParams = API.getLitterAppPayParams(appid, prepay_id, key, nonce_str);
            String jsonstr = litterPayParams != null ? gson.toJson(litterPayParams) : null;
            boolean order = ShopOrderService.createOrder(together, togetherId, userAccountId, couponAccountId,
                    reduceUser, reduceCoupon, diffPay, buyNum, out_trade_no, user.getId().intValue(), jsonstr, address, product, productGroup,
                    referScene, referAppId, referChannel
            );
            result.put("useUserBalance", reduceUser);
            result.put("useCouponBalance", reduceCoupon);
            result.put("useCash", diffPay);
            result.put("needPay", true);
            result.put("order", order);
            result.put("litterPayParams", litterPayParams);
            renderJSON(RtnUtil.returnSuccess("OK", result));

        } catch (Exception e) {
            Logger.error(e, e.getMessage());
            renderJSON(RtnUtil.returnFail(e.getMessage()));
        }
    }

    public static void cancelPay(String session, String orderId) {
        try {
            UsersDDL user = UserService.findBySession(session);
            if (user == null) {
                renderJSON(RtnUtil.returnLoginFail());
            }
            ShopOrderDDL order = ShopOrderService.findByOrderId(orderId);
            ShopOrderService.payCanel(order);

            renderJSON(RtnUtil.returnSuccess());
        } catch (Exception e) {
            Logger.error(e, e.getMessage());
            renderJSON(RtnUtil.returnFail(e.getMessage()));
        }
    }

    public static void refundApply(String session, String orderId, String memo) {
        try {
            UsersDDL user = UserService.findBySession(session);
            if (user == null) {
                renderJSON(RtnUtil.returnLoginFail());
            }
            ShopOrderDDL order = ShopOrderService.findByOrderId(orderId);

            if (order == null) {
                renderJSON(RtnUtil.returnFail("订单不存在"));
            }

            ShopOrderService.refund_applay(order, memo);

            renderJSON(RtnUtil.returnSuccess());
        } catch (Exception e) {
            Logger.error(e, e.getMessage());
            renderJSON(RtnUtil.returnFail(e.getMessage()));
        }
    }

    public static void wxPaynotify() {
        try {
            String notifyBody = params.get("body");
            Logger.info("支付：微信回调，body=%s", notifyBody);

            Map<String, String> notifyTreeMap = new TreeMap<String, String>();
            Document reader = DocumentHelper.parseText(notifyBody);
            Iterator<Element> childIt = reader.getRootElement().elementIterator();
            String notifySign = "";
            while (childIt.hasNext()) {
                Element child = childIt.next();
                String name = child.getName();
                String value = child.getText();
                if (name.equals("sign")) {
                    notifySign = value;
                    continue;
                }
                notifyTreeMap.put(name, value);
            }
            if (!notifyTreeMap.containsKey("appid")) {
                throw new Exception("支付：微信回调时，没有appid参数,requestBody=" + notifyBody);
            }
            if (!notifyTreeMap.get("return_code").equals("SUCCESS")) {
                throw new Exception("支付：微信回调时return_code!=SUCCESS,requestBody=" + notifyBody);
            }
            //验证相应签名是否正确
            StringBuffer notifyParams = new StringBuffer();
            Iterator<Map.Entry<String, String>> respParamsIt = notifyTreeMap.entrySet().iterator();
            while (respParamsIt.hasNext()) {
                Map.Entry<String, String> entry = respParamsIt.next();
                String theKey = entry.getKey();
                String value = entry.getValue();
                if (StringUtils.isEmpty(value)) continue;
                notifyParams.append(theKey).append("=").append(value).append("&");
            }
            String appid = String.valueOf(notifyTreeMap.get("appid"));
            String key = Jws.configuration.getProperty(appid + ".pay.key");
            notifyParams.append("key=").append(key);
            String notifyStringA = notifyParams.toString();
            String mySign = MD5Util.md5(notifyStringA);
            if (!mySign.equals(notifySign)) {
                throw new Exception("支付：微信回调时，签名不正确，mySign=" + mySign + ",mySingString=" + notifyStringA);
            }

            String out_trade_no = String.valueOf(notifyTreeMap.get("out_trade_no"));
            String transaction_id = String.valueOf(notifyTreeMap.get("transaction_id"));
            int cash_fee = Integer.parseInt(String.valueOf(notifyTreeMap.get("cash_fee")));

            ShopOrderDDL order = ShopOrderService.findByOrderId(out_trade_no);
            if (order == null) {
                Logger.error("支付：微信回调时,找不到订单out_trade_no=%s", out_trade_no);
            } else {
                //order.setUseCash(Integer.parseInt(notifyTreeMap.get("cash_fee")));
                order.setTransactionId(transaction_id);
                order.setNotifyBody(notifyBody);
                if (order.getUseCash() <= cash_fee) {
                    ShopOrderService.paySuccess(order);
                } else {
                    ShopOrderService.payFail(order);
                }

            }
            Document document = DocumentHelper.createDocument();
            Element xmlElement = document.addElement("xml");
            Element return_code = xmlElement.addElement("return_code");
            return_code.addCDATA("SUCCESS");
            Element return_msg = xmlElement.addElement("return_msg");
            return_msg.addCDATA("OK");
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8");

            StringWriter sw = new StringWriter();
            XMLWriter writer = new XMLWriter(sw, format);
            try {
                writer.write(document);
                writer.flush();
                writer.close();
            } catch (IOException e1) {
                Logger.error(e1, e1.getMessage());
            }
            String rsp = sw.toString();
            renderText(rsp);
        } catch (Exception e) {
            Logger.error(e, e.getMessage());
            Document document = DocumentHelper.createDocument();
            Element xmlElement = document.addElement("xml");
            Element return_code = xmlElement.addElement("return_code");
            return_code.addCDATA("FAIL");
            Element return_msg = xmlElement.addElement("return_msg");
            return_msg.addCDATA(e.getMessage());
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8");

            StringWriter sw = new StringWriter();
            XMLWriter writer = new XMLWriter(sw, format);
            try {
                writer.write(document);
                writer.flush();
                writer.close();
            } catch (IOException e1) {
                Logger.error(e1, e1.getMessage());
            }
            String rsp = sw.toString();
            renderText(rsp);
        }

    }

    public static void main(String[] args) throws Exception {
        String body = "<xml><return_code>SUCCESS</return_code><appid><![CDATA[wx9ecd278ad19f6328]]></appid><mch_id><![CDATA[1503353991]]></mch_id><nonce_str><![CDATA[d2f8a97ebfc044af218f072f9668fb8f]]></nonce_str><req_info><![CDATA[hVb/LMzZtthMeo73OgNse7cZfM8wDs5qJ5tNairW0tibraJGdhh8T0RiMscBFn6KZeHxKReOYtt0UH7qx0hsG2R9FW6JytKr2Y1lxN7PMXkTaKBTWx4NQ1sDov5C727J/NXq4g++PK/aMMltrngFj3nYZppR4fjtB9nFtWoQ3Zk1HFxfbOB+aDRbY4bjCULZrk5+YMymvkGCfWWq147xRLPGr9jc20aNoK1BuoX1wQgAcmTHmIoh6u4a/f2ma5bmGOf0TOyy8hbWqwf4MAywARqO6DRe4PQaMwJOtCP55UZsGWRaUb0auOzlNK4fBHIdFxzmYLXZNZ1tshIsXOKvCnFu304YkXT/HORUc5eDn5AhQfVJrvP8yXKalBQx85gDm+wcQLhSB+gZbEXvOjuXv3taSvk44xUI5aUwq23yQVWPunenATXxynv0b4UO2x56FANyh8GTmJ51/fjB8VY5Pn3Hx3993Ly+LR7jDhalas4Ad850YPr9BJbIDpuQoix51vtX8IFLPdMcH+d1zx0D/4bq6BCOQdXSPffKMtCIEyV4jzBHtS13c5NhxQYix5aDiGmqdv5iCGU2i76tuM3uTE5PNGHQA7UN15TFXd5nO5zyS5gvNFt7Mw6dBpf1kmmwtp8TKbRcKiXBlRl0whpRtUs9kKaQfUqTkLMhbgpzHcu1uWWZzERirOn/JGDw63s5wroF1WUmn92F5BMDeXPcn2wkIu0mKNayc1KrtBwauYL6Co2u6mRB5dLIL1s91iK7fQA2FsyIl0pSfhXtw9VKHdpystMd6JP6QIiMZcDyPnV9zNs6d5m6j/4Kbm2jI1cw5hypuIEi2col5Rt4JFxW4DcTmMQSPrU8V0xUcOrTzHLEIvFHp6oYmxciAQXDR1dDDQVsz6y449xCiNqkX9E2y2UxxAGAow/+LOzw1JWeLUKGHynK77C/gkN5ErTQ0MdOM7EtgGSF2ABFOiuXetCLiqmAFo76oL+F/fu24VuXUQlTENgGjDxiNL9slmJjm1eSuJtG+F1Lb/oMzwzRafz12BDRMHsyJBoQHenPCA+dAUODu+ek6zr631TCms3jTRE9]]></req_info></xml>";
        Map<String, String> notifyTreeMap = new TreeMap<String, String>();
        Document reader = DocumentHelper.parseText(body);
        Iterator<Element> childIt = reader.getRootElement().elementIterator();
        while (childIt.hasNext()) {
            Element child = childIt.next();
            String name = child.getName();
            String value = child.getText();
            notifyTreeMap.put(name, value);
        }
        System.out.println(notifyTreeMap);
        System.out.println("Base64解码前字符串：" + notifyTreeMap.get("req_info"));
        //解密
        byte[] reqInfoEncode = Base64.decodeBase64(notifyTreeMap.get("req_info"));

        System.out.println("Base64解码后字符串:" + new String(reqInfoEncode));

        String key = "CYQZS5KG2CI3DX5N201FAUD9EXU0P1YL";
        String key1 = MD5Util.md5(key).toLowerCase();
        System.out.println("key1=" + key1);

        String reqInfoDecode = EncryptUtil.Aes256Decode(reqInfoEncode, key1.getBytes("UTF-8"));
        System.out.println("AES解密后:" + reqInfoDecode);
    }

    public static void wxRefundnotify() {
        try {
            String notifyBody = params.get("body");
            Logger.info("退款：微信回调，body=%s", notifyBody);

            Map<String, String> notifyTreeMap = new TreeMap<String, String>();
            Document reader = DocumentHelper.parseText(notifyBody);
            Iterator<Element> childIt = reader.getRootElement().elementIterator();
            while (childIt.hasNext()) {
                Element child = childIt.next();
                String name = child.getName();
                String value = child.getText();
                notifyTreeMap.put(name, value);
            }
            if (!notifyTreeMap.get("return_code").equals("SUCCESS")) {
                throw new Exception("退款：微信回调时return_code!=SUCCESS,requestBody=" + notifyBody);
            }

            if (!notifyTreeMap.containsKey("req_info")) {
                throw new Exception("退款：微信回调时req_info不存在,requestBody=" + notifyBody);
            }

            //解密
            byte[] reqInfoEncode = Base64.decodeBase64(notifyTreeMap.get("req_info"));
            String key = Jws.configuration.getProperty(Jws.configuration.getProperty("shop.appId") + ".pay.key");
            String key1 = MD5Util.md5(key).toLowerCase();
            Logger.info("退款：微信回调req_iinfo解密前%s", new String(reqInfoEncode));
            String reqInfoDecode = EncryptUtil.Aes256Decode(reqInfoEncode, key1.getBytes("UTF-8"));
            Logger.info("退款：微信回调req_iinfo解密后%s", reqInfoDecode);

            Map<String, String> reqInfoMap = new HashMap<String, String>();
            Document reqInfoReader = DocumentHelper.parseText(reqInfoDecode);
            Iterator<Element> reqInfoIt = reqInfoReader.getRootElement().elementIterator();
            while (reqInfoIt.hasNext()) {
                Element child = reqInfoIt.next();
                String name = child.getName();
                String value = child.getText();
                reqInfoMap.put(name, value);
            }

            Logger.info("退款：微信回调req_iinfo解密后to Map->%s", reqInfoDecode);

            String out_trade_no = String.valueOf(reqInfoMap.get("out_trade_no"));
            ShopOrderDDL order = ShopOrderService.findByOrderId(out_trade_no);

            if (order == null) {
                Logger.error("微信回调时,找不到订单out_trade_no=%s", out_trade_no);
            } else {
                ShopOrderService.refund_nofity(order, reqInfoMap);
            }
            //
            Document document = DocumentHelper.createDocument();
            Element xmlElement = document.addElement("xml");
            Element return_code = xmlElement.addElement("return_code");
            return_code.addCDATA("SUCCESS");
            Element return_msg = xmlElement.addElement("return_msg");
            return_msg.addCDATA("OK");
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8");

            StringWriter sw = new StringWriter();
            XMLWriter writer = new XMLWriter(sw, format);
            try {
                writer.write(document);
                writer.flush();
                writer.close();
            } catch (IOException e1) {
                Logger.error(e1, e1.getMessage());
            }
            String rsp = sw.toString();
            renderText(rsp);
        } catch (Exception e) {
            Logger.error(e, e.getMessage());
            Document document = DocumentHelper.createDocument();
            Element xmlElement = document.addElement("xml");
            Element return_code = xmlElement.addElement("return_code");
            return_code.addCDATA("FAIL");
            Element return_msg = xmlElement.addElement("return_msg");
            return_msg.addCDATA(e.getMessage());
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8");

            StringWriter sw = new StringWriter();
            XMLWriter writer = new XMLWriter(sw, format);
            try {
                writer.write(document);
                writer.flush();
                writer.close();
            } catch (IOException e1) {
                Logger.error(e1, e1.getMessage());
            }
            String rsp = sw.toString();
            renderText(rsp);
        }

    }

    //获取首页需要展现出售的商品
    public static void index(int page, int pageSize) {
        try {
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            page = page == 0 ? 1 : page;
            pageSize = pageSize == 0 ? 10 : pageSize;
            List<ShopProductDDL> products = ShopProductService.listShopIndexProduct(page, pageSize);
            if (products == null || products.size() == 0) {
                renderJSON(RtnUtil.returnSuccess("OK", list));
            }
            for (ShopProductDDL p : products) {
                Map<String, Object> result = new HashMap<String, Object>();
                result.put("productId", p.getProductId());
                result.put("productName", p.getProductName());
                result.put("productBanner", API.getObjectAccessUrlSimple(p.getProductBanner()));
                result.put("productOriginPrice", AmountUtil.f2y(p.getProductOriginAmount()));
                result.put("productNowPrice", AmountUtil.f2y(p.getProductNowAmount()));
                result.put("productTogetherPrice", AmountUtil.f2y(p.getProductTogetherAmount()));
                result.put("joinTogether", p.getJoinTogether());
                result.put("platformChecked", p.getPlatformChecked());

                result.put("togetherSales", String.format("%.1f", p.getTogetherSales() / 10000f));
                List<ShopTogetherDDL> togethers = ShopTogetherService.listByProductId(p.getProductId(), 1, 2);
                if (togethers != null && togethers.size() == 2) {
                    String[] together = new String[]{togethers.get(0).getMasterAvatar(), togethers.get(1).getMasterAvatar()};
                    result.put("togethers", together);
                }

                //小区合作
                List<ShopProductCommunityRelDDL> communityList = ShopCommunityService.listByProductId(p.getProductId());
                if (communityList != null && communityList.size() > 0) {
                    List<Map<String, Object>> communities = new ArrayList<Map<String, Object>>();
                    for (ShopProductCommunityRelDDL c : communityList) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("communityId", c.getCommunityId());
                        map.put("communityName", c.getCommunityName());
                        communities.add(map);
                    }
                    result.put("communities", communities);
                }

                list.add(result);

            }
            renderJSON(RtnUtil.returnSuccess("OK", list));
        } catch (Exception e) {
            Logger.error(e, e.getMessage());
            renderJSON(RtnUtil.returnFail(e.getMessage()));
        }
    }

    /**
     * @param pcategoryId
     * @param subCategoryId
     * @param sale
     * @param hot
     * @param orderBy       1=时间降序 2=销量降序 3=价格降序 4=价格升序 5=综合排序
     */
    public static void listProduct(String shopId, String productId, String keyword,
                                   String pCategoryId, String subCategoryId, boolean isSale,
                                   boolean isHot, int status, int orderBy, boolean isSeckilling, int seckillingTime,
                                   int page, int pageSize) {
        try {
            List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
            Map<String, Object> response = new HashMap<String, Object>();

            int total = ShopProductService.countProduct(shopId, productId, keyword, pCategoryId,
                    subCategoryId, isSale ? 1 : 0, isHot ? 1 : 0, status, isSeckilling ? 1 : -1, seckillingTime);
            response.put("total", total);
            response.put("pageTotal", Math.ceil(total / (double) pageSize));

            if (total == 0) {
                response.put("list", mapList);
                renderJSON(RtnUtil.returnSuccess("OK", response));
            }
            List<ShopProductDDL> list = ShopProductService.listProduct(shopId, productId, keyword, pCategoryId, subCategoryId,
                    isSale ? 1 : 0, isHot ? 1 : 0, status, orderBy,
                    isSeckilling ? 1 : -1, seckillingTime,
                    page <= 0 ? 1 : page, pageSize <= 0 ? 10 : pageSize);


            for (ShopProductDDL p : list) {
                Map<String, Object> result = new HashMap<String, Object>();
                result.put("productId", p.getProductId());
                result.put("productName", p.getProductName());
                result.put("productBanner", API.getObjectAccessUrlSimple(p.getProductBanner()));
                result.put("productOriginPrice", AmountUtil.f2y(p.getProductOriginAmount()));

                //正在秒杀活动
                if (p.getJoinSeckilling() != null && p.getJoinSeckilling() == 1 &&
                        getSecKillingEndTimes(p.getSeckillingTime()) > System.currentTimeMillis()
                ) {
                    result.put("joinSeckilling", p.getJoinSeckilling());
                    result.put("seckillingPrice", AmountUtil.f2y(p.getSeckillingPrice()));
                    String secKillingTime = DateUtil.format(getSecKillingEndTimes(p.getSeckillingTime()), "HH:mm");
                    result.put("secKillingTime", secKillingTime);
                }

                result.put("productNowPrice", AmountUtil.f2y(p.getProductNowAmount()));
                if (p.getJoinTogether() == 1) {
                    result.put("productTogetherPrice", AmountUtil.f2y(p.getProductTogetherAmount()));
                    result.put("togetherSales", String.format("%.1f", p.getTogetherSales() / 10000f));
                } else {
                    result.put("productTogetherPrice", "0.0");
                    result.put("togetherSales", "0");
                }
                result.put("joinTogether", p.getJoinTogether());
                result.put("platformChecked", p.getPlatformChecked());

                result.put("sotre", p.getStore());
                result.put("status", p.getStatus());
                result.put("createTime", DateUtil.format(p.getCreateTime()));
                result.put("pv", p.getPv());
                result.put("deal", p.getDeal());
                result.put("dealPercent", p.getDeal() / p.getStore());
                result.put("isHot", p.getIsHot() == 1);
                result.put("isSale", p.getIsSale() == 1);


                List<ShopTogetherDDL> togethers = ShopTogetherService.listByProductId(p.getProductId(), 1, 2);
                if (togethers != null && togethers.size() == 2) {
                    String[] together = new String[]{togethers.get(0).getMasterAvatar(), togethers.get(1).getMasterAvatar()};
                    result.put("togethers", together);
                }

                //小区合作
				/*List<ShopProductCommunityRelDDL>  communityList = ShopCommunityService.listByProductId(p.getProductId());
				if(communityList!=null && communityList.size()>0){
					List<Map<String,Object>> communities = new ArrayList<Map<String,Object>>();
					for( ShopProductCommunityRelDDL c : communityList ){
						Map<String,Object> map = new HashMap<String,Object>();
						map.put("communityId",c.getCommunityId());
						map.put("communityName",c.getCommunityName());
						communities.add(map);
					}
					result.put("communities", communities);
				}	*/
                mapList.add(result);
            }

            response.put("list", mapList);
            renderJSON(RtnUtil.returnSuccess("OK", response));
        } catch (Exception e) {
            Logger.error(e, e.getMessage());
            renderJSON(RtnUtil.returnFail(e.getMessage()));
        }
    }

    public static void productDetail(String productId) {
        try {
            Map<String, Object> result = new HashMap<String, Object>();
            ShopProductDDL p = ShopProductService.getByProductId(productId);
            if (p == null) {
                throw new Exception("商品不存在");
            }
            result.put("status", p.getStatus());
            result.put("productId", p.getProductId());
            result.put("productName", p.getProductName());
            result.put("productDesc", StringUtils.isEmpty(p.getProductDesc()) ? null : p.getProductDesc().split("`"));
            result.put("joinTogether", p.getJoinTogether());
            result.put("productOriginPrice", AmountUtil.f2y(p.getProductOriginAmount()));
            result.put("productNowPrice", AmountUtil.f2y(p.getProductNowAmount()));

            if (p.getJoinSeckilling() != null && p.getJoinSeckilling() == 1 &&
                    getSecKillingEndTimes(p.getSeckillingTime()) > System.currentTimeMillis()
            ) {
                result.put("joinSeckilling", p.getJoinSeckilling());
                result.put("seckillingPrice", AmountUtil.f2y(p.getSeckillingPrice()));

                String secKillingTime = DateUtil.format(getSecKillingEndTimes(p.getSeckillingTime()), "HH:mm");
                result.put("secKillingTime", secKillingTime);
            }

            //正在秒杀活动
            if (p.getJoinSeckilling() != null && p.getJoinSeckilling() == 1 &&
                    getSecKillingEndTimes(p.getSeckillingTime()) > System.currentTimeMillis()
            ) {
                result.put("joinSeckilling", p.getJoinSeckilling());
                result.put("seckillingPrice", AmountUtil.f2y(p.getSeckillingPrice()));
                String secKillingTime = DateUtil.format(getSecKillingEndTimes(p.getSeckillingTime()), "HH:mm");
                result.put("secKillingTime", secKillingTime);
            }

            if (p.getJoinTogether() != null && p.getJoinTogether() == 1) {
                result.put("productTogetherPrice", AmountUtil.f2y(p.getProductTogetherAmount()));
                result.put("joinTogether", p.getJoinTogether());
                result.put("togetherNumber", p.getTogetherNumber());
                result.put("togetherSales", p.getTogetherSales());
            }
            result.put("platformChecked", p.getPlatformChecked());
            //截图
            List<ShopProductImagesDDL> ssimages = ShopProductImageService.listImages(productId, ShopProductImageService.SCREENSHOT_TYPE, 1, 50);
            if (ssimages != null && ssimages.size() > 0) {
                List<String> screenshots = new ArrayList<String>();
                for (ShopProductImagesDDL img : ssimages) {
                    screenshots.add(API.getObjectAccessUrlSimple(img.getImageKey()));
                }
                result.put("screenshots", screenshots);
            }
            //详情图片
            List<ShopProductImagesDDL> detailimages = ShopProductImageService.listImages(productId, ShopProductImageService.DETAIL_TYPE, 1, 100);
            if (detailimages != null && detailimages.size() > 0) {
                List<String> detailImages = new ArrayList<String>();
                for (ShopProductImagesDDL img : detailimages) {
                    detailImages.add(API.getObjectAccessUrlSimple(img.getImageKey()));
                }
                result.put("detailImages", detailImages);
            }

            //详情图片
            List<ShopProductImagesDDL> showimages = ShopProductImageService.listImages(productId, ShopProductImageService.BUYER_SHOW, 1, 100);
            if (showimages != null && showimages.size() > 0) {
                List<String> showImages = new ArrayList<String>();
                for (ShopProductImagesDDL img : showimages) {
                    showImages.add(API.getObjectAccessUrlSimple(img.getImageKey()));
                }
                result.put("showImages", showImages);
            }

            //属性
            List<ShopProductAttrRelDDL> attributesList = ShopProductAttrService.listByProduct(productId);
            if (attributesList != null && attributesList.size() > 0) {
                List<String> attributes = new ArrayList<String>();
                for (ShopProductAttrRelDDL attr : attributesList) {
                    attributes.add(attr.getAttrName());
                }
                result.put("attributes", attributes);
            }

            //参团情况
            List<ShopTogetherDDL> togetherList = ShopTogetherService.listCanJoinByProductId(productId, 1, 5);
            int togetherCount = (ShopTogetherService.countByProductId(productId));
            result.put("togetherCount", togetherCount);
            if (togetherList != null && togetherList.size() > 0) {
                DateFormat df = new SimpleDateFormat("MM-dd HH:mm");
                List<Map<String, Object>> togetherJoiners = new ArrayList<Map<String, Object>>();
                for (ShopTogetherDDL st : togetherList) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("togetherId", st.getTogetherId());
                    map.put("masterAvatar", st.getMasterAvatar());
                    map.put("masterName", st.getMasterName());
                    map.put("expireTime", df.format(new Date(st.getExpireTime())));
                    map.put("togetherNumberRedius", st.getTogetherNumberResidue());
                    togetherJoiners.add(map);
                }
                result.put("togetherJoiners", togetherJoiners);
            }
            //商品组
            List<ShopProductGroupDDL> groupList = ShopProductGroupService.findByProductId(productId);
            if (groupList != null && groupList.size() > 0) {
                List<Map<String, Object>> groups = new ArrayList<Map<String, Object>>();
                int index = 0;
                for (ShopProductGroupDDL group : groupList) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("groupId", group.getGroupId());
                    map.put("groupIndex", index);
                    map.put("groupName", group.getGroupName());
                    map.put("groupImage", API.getObjectAccessUrlSimple(group.getGroupImage()));
                    map.put("groupPrice", AmountUtil.f2y(group.getGroupPrice()));
                    map.put("groupTogetherPrice", AmountUtil.f2y(group.getGroupTogetherPrice()));
                    groups.add(map);
                    index++;
                }
                result.put("groups", groups);
            }
            //小区合作
            List<ShopProductCommunityRelDDL> communityList = ShopCommunityService.listByProductId(productId);
            if (communityList != null && communityList.size() > 0) {
                List<Map<String, Object>> communities = new ArrayList<Map<String, Object>>();
                for (ShopProductCommunityRelDDL c : communityList) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("communityId", c.getCommunityId());
                    map.put("communityName", c.getCommunityName());
                    communities.add(map);
                }
                result.put("communities", communities);
            }

            //是否有优惠券领取
            List<ShopCouponMngDDL> couponList = ShopCouponMngService.selectCouponActivities(null, productId, p.getSellerUserId(), 3);
            if (couponList != null && couponList.size() > 0) {
                List<Map<String, Object>> coupons = new ArrayList<Map<String, Object>>();
                for (ShopCouponMngDDL c : couponList) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("id", c.getId());
                    map.put("amount", AmountUtil.f2y(c.getAmount()));
                    map.put("name", c.getCouponName());
                    map.put("valid", 1);
                    map.put("expireTime", DateUtil.format(c.getExpireTime()));
                    coupons.add(map);
                }
                result.put("coupons", coupons);
            }
            renderJSON(RtnUtil.returnSuccess("OK", result));
        } catch (Exception e) {
            Logger.error(e, e.getMessage());
            renderJSON(RtnUtil.returnFail(e.getMessage()));
        }

    }


    public static void listAccounts(String session, String productId, double price) {
        try {
            UsersDDL user = UserService.findBySession(session);
            if (user == null) {
                renderJSON(RtnUtil.returnLoginFail());
            }
            List<UserAccountDDL> accountList = UserAccountService.listALLByUser(user.getId().intValue());

            Map<String, Object> result = new HashMap<String, Object>();

            List<Map<String, Object>> validAccounts = new ArrayList<Map<String, Object>>();
            List<Map<String, Object>> invalidAccounts = new ArrayList<Map<String, Object>>();

            if (accountList == null || accountList.size() == 0) {
                renderJSON(RtnUtil.returnSuccess("OK", result));
            }

            ShopProductDDL product = ShopProductService.getByProductId(productId);
            if (product == null) {
                renderJSON(RtnUtil.returnFail("商品不存在"));
            }

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (UserAccountDDL account : accountList) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("accountId", account.getAccountId());
                map.put("accountName", account.getAccountName());
                map.put("accountAmount", AmountUtil.f2y(account.getAmount()));
                if (account.getAccountType() == 1) {
                    result.put("basicAccount", map);
                } else if (account.getAccountType() == 2) {
                    map.put("expireTime", df.format(new Date(account.getExpireTime())));
                    try {
                        UserAccountService.canUse(account.getAccountId(), productId, product.getSellerUserId(), AmountUtil.y2f(price));
                        map.put("canUse", true);
                        validAccounts.add(map);
                    } catch (Exception e) {
                        Logger.warn(e, e.getMessage());
                        map.put("canUse", false);
                        invalidAccounts.add(map);
                    }
                }
            }
            result.put("validAccounts", validAccounts);
            result.put("invalidAccounts", invalidAccounts);
            renderJSON(RtnUtil.returnSuccess("OK", result));

        } catch (Exception e) {
            Logger.error(e, e.getMessage());
            renderJSON(RtnUtil.returnFail(e.getMessage()));
        }
    }

    public static void getOrder(String session, String orderNo) {
        try {
            UsersDDL user = UserService.findBySession(session);
            if (user == null) {
                renderJSON(RtnUtil.returnLoginFail());
            }
            Map<String, Object> result = new HashMap<String, Object>();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ShopOrderDDL order = ShopOrderService.findByOrderId(orderNo);
            ShopProductDDL product = ShopProductService.getByProductId(order.getProductId());
            result.put("isTogether", false);
            if (order.getTogetherId() != null) {
                result.put("isTogether", true);
                ShopTogetherDDL together = ShopTogetherService.listByTogetherId(order.getTogetherId());
                Map<String, Object> togetherMap = new HashMap<String, Object>();
                togetherMap.put("masterAvatar", together.getMasterAvatar());
                togetherMap.put("masterName", together.getMasterName());
                togetherMap.put("createTime", df.format(new Date(together.getCreateTime())));
                togetherMap.put("expireTime", df.format(new Date(together.getExpireTime())));
                result.put("together", togetherMap);

                List<ShopTogetherJoinerDDL> togethers = ShopTogetherService.listJoinerByTogetherId(order.getTogetherId());
                List<Map<String, Object>> togetherList = new ArrayList<Map<String, Object>>();
                for (ShopTogetherJoinerDDL t : togethers) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("avatar", t.getUserAvatar());
                    map.put("name", t.getUserName());
                    map.put("joinTime", df.format(new Date(t.getJoinTime())));
                    togetherList.add(map);
                }
                result.put("togethers", togetherList);
                result.put("togetherPrice", AmountUtil.f2y(product.getProductTogetherAmount()));
                result.put("needTogetherNumber", product.getTogetherNumber() - togethers.size());
                result.put("togetherNumber", product.getTogetherNumber());
            }
            result.put("order", order);
            result.put("originPrice", AmountUtil.f2y(product.getProductOriginAmount()));
            result.put("shareImage", API.getObjectAccessUrlSimple("4d638917b143496a95bb83d3d935c7c1"));
            renderJSON(RtnUtil.returnSuccess("OK", result));
        } catch (Exception e) {
            Logger.error(e, e.getMessage());
            renderJSON(RtnUtil.returnFail(e.getMessage()));
        }
    }

    public static void listOrder(String session, int status, boolean imSeller, int page, int pageSize) {
        try {
            UsersDDL user = UserService.findBySession(session);
            if (user == null) {
                renderJSON(RtnUtil.returnLoginFail());
            }
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("isSeller", user.getIsSeller() != null && user.getIsSeller() == 1);
            //我的订单列表，需要显示什么呢？
            //商品组图片  商品组名称  商品名称  商品ID 商品组ID 订单状态  购买数量 商品组价格  现金付款额度  优惠券付款额度  下单时间
            List<ShopOrderDDL> list = ShopOrderService.listOrder(user.getId().intValue(), status, imSeller, page, pageSize);
            List<Map<String, Object>> orders = new ArrayList<Map<String, Object>>();
            if (list == null || list.size() == 0) {
                result.put("orders", orders);
                renderJSON(RtnUtil.returnSuccess("OK", result));
            }
            for (ShopOrderDDL order : list) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("orderId", order.getOrderId());
                map.put("groupImg", API.getObjectAccessUrlSimple(order.getGroupImg()));
                map.put("groupName", order.getGroupName());
                map.put("productName", order.getProductName());
                map.put("groupId", order.getGroupId());
                map.put("productId", order.getProductId());
                map.put("orderStatus", order.getStatus());
                map.put("prize", order.getPrizeLevel());
                map.put("buyNum", order.getBuyNum());
                map.put("groupPrice", AmountUtil.f2y(order.getGroupPrice()));
                map.put("totalPay", AmountUtil.f2y(order.getGroupPrice() * order.getBuyNum()));
                if (order.getGroupTogetherPrice() != null) {
                    map.put("groupTogetherPrice", AmountUtil.f2y(order.getGroupTogetherPrice()));
                }
                if (order.getUseCash() != null) {
                    map.put("cashPay", AmountUtil.f2y(order.getUseCash()));
                }
                if (order.getUseCouponAmount() != null) {
                    map.put("couponPay", AmountUtil.f2y(order.getUseCouponAmount()));
                }
                if (order.getUseUserAmount() != null) {
                    map.put("balancePay", AmountUtil.f2y(order.getUseUserAmount()));
                }
                map.put("orderTime", DateUtil.format(order.getOrderTime()));
                orders.add(map);
            }
            result.put("orders", orders);
            renderJSON(RtnUtil.returnSuccess("OK", result));
        } catch (Exception e) {
            Logger.error(e, e.getMessage());
        }
    }

    public static void orderDetail(String session, String orderId) {
        try {
            UsersDDL user = UserService.findBySession(session);
            if (user == null) {
                renderJSON(RtnUtil.returnLoginFail());
            }
            ShopOrderDDL order = ShopOrderService.findByOrderId(orderId);
            if (order == null) {
                renderJSON(RtnUtil.returnFail("订单不存在"));
            }

            Map<String, Object> map = new HashMap<String, Object>();

            //通用信息
            map.put("orderType", order.getOrderType());
            map.put("orderId", order.getOrderId());
            map.put("orderStatus", order.getStatus());
            map.put("buyNum", order.getBuyNum());

            ShopIndexDDL shop = ShopIndexService.getByShopId(order.getShopId());
            String defaultContactMobile = shop.getContactMobile();
            String defaultContactWx = shop.getContactWx();

            map.put("sellerTelNumber", StringUtils.isEmpty(order.getSellerTelNumber()) ? defaultContactMobile : order.getSellerTelNumber());
            map.put("sellerWxNumber", StringUtils.isEmpty(order.getSellerWxNumber()) ? defaultContactWx : order.getSellerWxNumber());

            map.put("prize", order.getPrizeLevel());

            if (order.getOrderType() == 0) {//单笔订单支付
                map.put("groupPrice", AmountUtil.f2y(order.getGroupPrice()));
                map.put("groupImg", API.getObjectAccessUrlSimple(order.getGroupImg()));
                map.put("isSeller", user.getId().intValue() == order.getSellerUserId());
                map.put("groupName", order.getGroupName());
                map.put("productName", order.getProductName());
                map.put("groupId", order.getGroupId());
                map.put("productType", order.getProductType());
                map.put("productId", order.getProductId());
            } else if (order.getOrderType() == 1) {

                List<ShopCarDDL> carList = ShopCarService.findByCarIds(order.getCarIds().split(","));

                List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                if (carList == null || carList.size() == 0) {
                    throw new Exception("购物车为空");
                }

                int sumAmount = 0;
                for (ShopCarDDL car : carList) {
                    Map<String, Object> one = new HashMap<String, Object>();
                    ShopProductDDL product = ShopProductService.getByProductId(car.getProductId());
                    ShopProductGroupDDL group = ShopProductGroupService.findByProductIdAndGroupId(car.getProductId(), car.getGroupId());
                    if (group == null || product == null) continue;
                    one.put("id", car.getId());
                    one.put("productId", product.getProductId());
                    one.put("groupId", group.getGroupId());
                    one.put("productName", product.getProductName());
                    one.put("groupImgage", API.getObjectAccessUrlSimple(group.getGroupImage()));
                    one.put("buyNum", car.getBuyNum());
                    one.put("status", car.getStatus());
                    one.put("groupName", group.getGroupName());
                    one.put("totalAmount", AmountUtil.f2y(group.getGroupPrice() * car.getBuyNum()));//实时
                    one.put("singPrice", AmountUtil.f2y(group.getGroupPrice()));//实时
                    sumAmount += group.getGroupPrice() * car.getBuyNum();
                    list.add(one);
                }

                map.put("sumAmount", AmountUtil.f2y(sumAmount));
                map.put("productList", list);
            }
            //订单是否可以退款
            map.put("canRefund", ShopOrderService.canRefund(order));
            map.put("shareImage", API.getObjectAccessUrlSimple("4d638917b143496a95bb83d3d935c7c1"));

            if (order.getGroupTogetherPrice() != null) {
                map.put("groupTogetherPrice", AmountUtil.f2y(order.getGroupTogetherPrice()));
            }
            if (order.getUseCash() != null) {
                map.put("cashPay", AmountUtil.f2y(order.getUseCash()));
            }
            if (order.getUseCouponAmount() != null) {
                map.put("couponPay", AmountUtil.f2y(order.getUseCouponAmount()));
            }
            if (order.getUseUserAmount() != null) {
                map.put("balancePay", AmountUtil.f2y(order.getUseUserAmount()));
            }
            map.put("orderTime", DateUtil.format(order.getOrderTime()));

            if (!StringUtils.isEmpty(order.getMemo())) {
                String[] memos = order.getMemo().split("</br>");
                map.put("memos", memos);
            }
            //解析地址信息
            JsonObject addressJson = new JsonParser().parse(order.getAddress()).getAsJsonObject();
            String userName = addressJson.get("userName").getAsString();
            String telNumber = addressJson.get("telNumber").getAsString();
            String address = addressJson.get("provinceName").getAsString() + addressJson.get("cityName").getAsString() + addressJson.get("detailInfo").getAsString();
            Map<String, String> addressMap = new HashMap<String, String>();
            addressMap.put("userName", userName);
            addressMap.put("provinceName", addressJson.get("provinceName").getAsString());
            addressMap.put("cityName", addressJson.get("cityName").getAsString());
            addressMap.put("countyName", addressJson.get("countyName").getAsString());
            addressMap.put("detailInfo", addressJson.get("detailInfo").getAsString());
            addressMap.put("address", address);
            addressMap.put("telNumber", telNumber);
            map.put("address", addressMap);
            //获取发货跟踪信息
            ShopExpressDDL express = ShopExpressService.getByOrderId(orderId);
            if (express != null) {
                Map<String, Object> expressMap = new HashMap<String, Object>();
                expressMap.put("state", express.getState());
                expressMap.put("id", express.getId());
                expressMap.put("station", express.getAcceptStation());
                expressMap.put("time", express.getAcceptTime());
                map.put("express", expressMap);
            }
            //获取团详情
            map.put("totalPay", AmountUtil.f2y(order.getGroupPrice() * order.getBuyNum()));
            if (!StringUtils.isEmpty(order.getTogetherId())) {
                map.put("totalPay", AmountUtil.f2y(order.getGroupTogetherPrice() * order.getBuyNum()));
                ShopTogetherDDL together = ShopTogetherService.listByTogetherId(order.getTogetherId());
                List<ShopTogetherJoinerDDL> togethers = ShopTogetherService.listJoinerByTogetherId(order.getTogetherId());
                List<Map<String, Object>> togetherList = new ArrayList<Map<String, Object>>();
                for (ShopTogetherJoinerDDL t : togethers) {
                    Map<String, Object> joiner = new HashMap<String, Object>();
                    joiner.put("id", t.getTogetherId());
                    joiner.put("master", t.getIsMaster() == 1);
                    joiner.put("avatar", t.getUserAvatar());
                    joiner.put("name", t.getUserName());
                    joiner.put("joinTime", DateUtil.format(t.getJoinTime()));
                    togetherList.add(joiner);
                }
                Map<String, Object> togetherMap = new HashMap<String, Object>();
                togetherMap.put("joiner", togetherList);
                togetherMap.put("status", together == null ? -1 : together.getStatus());
                togetherMap.put("totalNumber", together == null ? -1 : together.getTogetherNumber());
                togetherMap.put("residueNumber", together == null ? -1 : together.getTogetherNumberResidue());
                togetherMap.put("createTime", together == null ? "" : DateUtil.format(together.getCreateTime()));
                togetherMap.put("expireTime", together == null ? "" : DateUtil.format(together.getExpireTime()));
                map.put("together", togetherMap);
            }
            renderJSON(RtnUtil.returnSuccess("OK", map));
        } catch (Exception e) {
            Logger.error(e, e.getMessage());
            renderJSON(RtnUtil.returnFail());
        }
    }

    //获取物流详细情况
    public static void shipperTraces(String session, int expressId) {
        try {
            UsersDDL user = UserService.findBySession(session);
            if (user == null) {
                renderJSON(RtnUtil.returnLoginFail());
            }

            ShopExpressDDL express = ShopExpressService.getById(expressId);
            if (express == null) {
                renderJSON(RtnUtil.returnFail("物流不存在"));
            }
            express = ShopExpressService.updateExpressTrace(express);
            renderJSON(RtnUtil.returnSuccess("OK", express));
        } catch (Exception e) {
            Logger.error(e, e.getMessage());
            renderJSON(RtnUtil.returnFail());
        }
    }

    //status = 1 有效  2无效
    public static void listWallet(String session, int status, int page, int pageSize) {
        try {

            UsersDDL user = UserService.findBySession(session);
            if (user == null) {
                renderJSON(RtnUtil.returnLoginFail());
            }
            Map<String, Object> result = new HashMap<String, Object>();
            int validCount = UserAccountService.countValidateCoupons(user.getId().intValue());
            result.put("validCouponCount", validCount);
            UserAccountDDL basicAccount = UserAccountService.getBasicAccount(user.getId().intValue());
            if (basicAccount != null) {
                result.put("basicAccountId", basicAccount.getAccountId());
                result.put("basicAccountName", basicAccount.getAccountName());
                result.put("basicAccountAmount", AmountUtil.f2y(basicAccount.getAmount()));
            } else {
                result.put("basicAccountId", "");
                result.put("basicAccountName", "");
                result.put("basicAccountAmount", "0.00");
            }
            List<UserAccountDDL> couponList = null;
            if (status == 1) {
                couponList = UserAccountService.listValidateCoupons(user.getId().intValue(), page, pageSize);
            } else if (status == 2) {
                couponList = UserAccountService.listInvalidateCoupons(user.getId().intValue(), page, pageSize);
            }

            List<Map<String, Object>> coupons = new ArrayList<Map<String, Object>>();
            if (couponList == null || couponList.size() == 0) {
                renderJSON(RtnUtil.returnSuccess("OK", result));
            }

            for (UserAccountDDL account : couponList) {
                Map<String, Object> coupon = new HashMap<String, Object>();
                coupon.put("accountId", account.getAccountId());
                coupon.put("accountName", account.getAccountName());
                coupon.put("accountAmount", AmountUtil.f2y(account.getAmount()));
                coupon.put("expireTime", DateUtil.format(account.getExpireTime()));
                coupons.add(coupon);
            }

            result.put("coupons", coupons);

            renderJSON(RtnUtil.returnSuccess("OK", result));
        } catch (Exception e) {
            Logger.error(e, e.getMessage());
            renderJSON(RtnUtil.returnFail(e.getMessage()));
        }
    }

    public static void getCoupon(String session, int couponId, String productId) {
        try {
            UsersDDL user = UserService.findBySession(session);
            if (user == null) {
                renderJSON(RtnUtil.returnLoginFail());
            }
			/*ShopProductDDL p =ShopProductService.getByProductId(productId);
			if(p==null){
				throw new Exception("商品不存在");
			} */
            Map<String, Object> result = new HashMap<String, Object>();
            boolean get = ShopCouponMngService.getCoupon(couponId, user.getId().intValue());
            result.put("get", get);
            List<ShopCouponMngDDL> couponList = ShopCouponMngService.selectCouponActivities(null, productId, 0, 3);
            if (couponList != null && couponList.size() > 0) {
                List<Map<String, Object>> coupons = new ArrayList<Map<String, Object>>();
                for (ShopCouponMngDDL c : couponList) {
                    //是否有优惠券领取
                    Map<String, Object> map = new HashMap<String, Object>();

                    String key = "GET_COUPON_" + user.getId().intValue() + "_" + c.getId();
                    Object value = Cache.get(key);
                    int gets = value == null ? 0 : Integer.parseInt(String.valueOf(value));
                    map.put("valid", 1);
                    if (gets >= c.getLimitTimes()) {
                        map.put("valid", 0);
                    }

                    map.put("id", c.getId());
                    map.put("amount", AmountUtil.f2y(c.getAmount()));
                    map.put("name", c.getCouponName());
                    map.put("expireTime", DateUtil.format(c.getExpireTime()));
                    coupons.add(map);
                }
                result.put("coupons", coupons);
            }
            renderJSON(RtnUtil.returnSuccess("OK", result));
        } catch (Exception e) {
            Logger.error(e, e.getMessage());
            renderJSON(RtnUtil.returnFail(e.getMessage()));
        }
    }


    public static void isTogethering(String togetherid) {
        try {
            ShopTogetherDDL together = ShopTogetherService.getShopTogether(togetherid);
            if (together != null && together.getStatus() == ShopTogetherService.TOGETHER_ING) {
                renderJSON(RtnUtil.returnSuccess("OK", true));
            }
            renderJSON(RtnUtil.returnSuccess("OK", false));
        } catch (Exception e) {
            Logger.error(e, e.getMessage());
            renderJSON(RtnUtil.returnFail(e.getMessage()));
        }
    }


    /**
     * 查询店铺首页配置
     *
     * @param shopId
     */
    public static void getShopIndexConfig(String shopId) {
        try {
            if (StringUtils.isEmpty(shopId)) {
                shopId = String.valueOf(Jws.configuration.get("shop.index.default"));
            }
            ShopIndexDDL shopIndex = ShopIndexService.getByShopId(shopId);

            if (shopIndex == null) {
                renderJSON(RtnUtil.returnFail("店铺不存在"));
            }
            if (StringUtils.isEmpty(shopIndex.getConfig())) {
                renderJSON(RtnUtil.returnSuccess());
            }
            ShopIndexDto shopIndexConfig = gson.fromJson(shopIndex.getConfig(), ShopIndexDto.class);
            //处理图片URL
            shopIndexConfig.shopAvatar = API.getObjectAccessUrlSimple(shopIndexConfig.shopAvatarKey);
            shopIndexConfig.shopBanner = API.getObjectAccessUrlSimple(shopIndexConfig.shopBannerKey);
            shopIndexConfig.activityBg = API.getObjectAccessUrlSimple(shopIndexConfig.activityBgKey);


            for (ShopNavDto dto : shopIndexConfig.firstNavList) {
                if (dto.linkType == 2) {
                    dto.img = API.getObjectAccessUrlSimple(dto.imgkey);
                }
            }
            for (ShopNavDto dto : shopIndexConfig.secondNavList) {
                if (dto.linkType == 2) {
                    dto.img = API.getObjectAccessUrlSimple(dto.imgkey);
                }
            }
            for (ShopNavDto dto : shopIndexConfig.swiperList) {
                if (dto.linkType == 2) {
                    dto.img = API.getObjectAccessUrlSimple(dto.imgkey);
                }
            }
            for (ShopNavDto dto : shopIndexConfig.thirdNavList) {
                if (dto.linkType == 2) {
                    dto.img = API.getObjectAccessUrlSimple(dto.imgkey);
                }
            }
            for (ShopNavDto dto : shopIndexConfig.fourthNavList) {
                if (dto.linkType == 2) {
                    dto.img = API.getObjectAccessUrlSimple(dto.imgkey);
                }
            }
            for (ShopNavDto dto : shopIndexConfig.fiveNavList) {
                if (dto.linkType == 2) {
                    dto.img = API.getObjectAccessUrlSimple(dto.imgkey);
                }
            }

            for (ShopNavWrap wrap : shopIndexConfig.shopNavWrapList) {
                for (ShopNavDto dto : wrap.list) {
                    dto.img = API.getObjectAccessUrlSimple(dto.imgkey);
                }
            }

            Map<String, Object> result = new HashMap<String, Object>();
            result.put("config", shopIndexConfig);

            //兼容小程序吧
            result.put("shopName", shopIndex.getName());
            result.put("shopAvatar", API.getObjectAccessUrlSimple(shopIndex.getAvatar()));
            result.put("follow", shopIndex.getFollow());

            //获取可领取的代金券
            List<ShopCouponMngDDL> couponList = ShopCouponMngService.selectCouponActivities(shopId, null, 0, 10);
            if (couponList != null && couponList.size() > 0) {
                List<Map<String, Object>> coupons = new ArrayList<Map<String, Object>>();
                for (ShopCouponMngDDL c : couponList) {
                    //是否有优惠券领取
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("valid", 1);
                    map.put("id", c.getId());
                    map.put("amount", AmountUtil.f2y(c.getAmount()));
                    map.put("name", c.getCouponName());
                    map.put("desc", "活动时间范围：" + DateUtil.format(c.getStartTime()) + " 至 " + DateUtil.format(c.getEndTime()));
                    map.put("expireTime", DateUtil.format(c.getExpireTime()));
                    coupons.add(map);
                }
                result.put("coupons", coupons);
            }
            renderJSON(RtnUtil.returnSuccess("OK", result));
        } catch (Exception e) {
            Logger.error(e, e.getMessage());
            renderJSON(RtnUtil.returnFail(e.getMessage()));
        }
    }


    public static void listWeTao(int page, int pageSize, int id, String shopId) {
        try {

            List<Map<String, Object>> weTaos = new ArrayList<Map<String, Object>>();
            Map<String, Object> response = new HashMap<String, Object>();

            int total = ShopWeTaoService.countWeTao(shopId, "", 0);
            response.put("total", total);
            response.put("pageTotal", Math.ceil(total / (double) pageSize));

            if (total == 0) {
                response.put("list", weTaos);
                renderJSON(RtnUtil.returnSuccess("OK", response));
            }

            String ip = request.remoteAddress;

            if (id > 0) {
                String key = ip + "_" + id;
                if (Cache.safeAdd(key, "1", "1d")) {
                    ShopWeTaoService.zan(id);
                } else {
                    Cache.delete(key);
                    ShopWeTaoService.cancelZan(id);
                }
            }

            page = page == 0 ? 1 : page;
            pageSize = pageSize == 0 ? 10 : pageSize;


            List<ShopWetaoDDL> list = ShopWeTaoService.listWeTao(shopId, "", 0, page, pageSize);

            for (ShopWetaoDDL weTao : list) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("id", weTao.getId());
                map.put("content", weTao.getContent());
                map.put("createTime", DateUtil.timeDesc(weTao.getCreateTime()));
                map.put("comment", weTao.getComment());
                String key = ip + "_" + weTao.getId();
                if (Cache.get(key) != null) {
                    map.put("isZan", true);
                }
                //deal Image
                if (!StringUtils.isEmpty(weTao.getImages())) {
                    List<Map<String, Object>> imgs = new ArrayList<Map<String, Object>>();
                    for (String ossKey : weTao.getImages().split(",")) {
                        Map<String, Object> imgOne = new HashMap<String, Object>();
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
        } catch (Exception e) {
            Logger.error(e, e.getMessage());
            renderJSON(RtnUtil.returnFail("服务器异常"));
        }
    }

    public static void zanOnDetailPage(int id) {
        try {
            Map<String, Object> detail = new HashMap<String, Object>();
            ShopWetaoDDL weTao = ShopWeTaoService.get(id);
            if (weTao == null) {
                renderJSON(RtnUtil.returnFail("点赞失败"));
            }
            String ip = request.remoteAddress;
            String key = ip + "_" + id;
            if (Cache.safeAdd(key, "1", "1d")) {
                ShopWeTaoService.zan(id);
                detail.put("isZan", true);
            } else {
                Cache.delete(key);
                ShopWeTaoService.cancelZan(id);
                detail.put("isZan", false);
            }
            weTao = ShopWeTaoService.get(id);
            detail.put("zan", weTao.getZan());
            renderJSON(RtnUtil.returnSuccess("OK", detail));
        } catch (Exception e) {
            Logger.error(e, e.getMessage());
            renderJSON(RtnUtil.returnFail("服务器异常"));
        }
    }

    public static void weTaoDetail(int id) {
        try {
            Map<String, Object> detail = new HashMap<String, Object>();
            ShopWetaoDDL weTao = ShopWeTaoService.get(id);
            if (weTao == null) {
                renderText("找不到网页");
            }

            detail.put("id", weTao.getId());
            detail.put("seoTitle", weTao.getSeoTitle());
            detail.put("seoKey", weTao.getSeoKey());
            detail.put("seoDesc", weTao.getSeoDesc());
            detail.put("content", weTao.getContent());
            detail.put("zan", weTao.getZan());
            detail.put("shopId", weTao.getShopId());
            detail.put("comment", weTao.getComment());


            String ip = request.remoteAddress;
            String key = ip + "_" + id;
            if (Cache.get(key) != null) {
                detail.put("isZan", true);
            } else {
                detail.put("isZan", false);
            }


            List<String> images = new ArrayList<String>();
            if (!StringUtils.isEmpty(weTao.getImages())) {
                for (String ossKey : weTao.getImages().split(",")) {
                    images.add(API.getObjectAccessUrlSimple(ossKey));
                }
            }

            Map<String, Object> comments = wrapComments(1, 5, id, null);
            ShopWeTaoService.view(id);
            renderTemplate("weTaoDetailTmp.html", detail, images, comments);
        } catch (Exception e) {
            Logger.error(e, e.getMessage());
            renderText("服务器异常");
        }
    }


    public static void delWeTaoComment(int commentId, int weTaoId, String session, int page, int pageSize) {
        try {
            UsersDDL user = UserService.findBySession(session);
            if (user == null) {
                renderJSON(RtnUtil.returnLoginFail());
            }
            ShopWeTaoService.deleteComment(weTaoId, commentId);
            Map<String, Object> response = wrapComments(page, pageSize, weTaoId, user.getId().intValue());
            renderJSON(RtnUtil.returnSuccess("OK", response));

        } catch (Exception e) {
            Logger.error(e, e.getMessage());
            renderJSON(RtnUtil.returnFail("服务器异常"));
        }
    }


    public static void addWeTaoComment(int weTaoId, String session, String comment, int page, int pageSize) {
        try {
            UsersDDL user = UserService.findBySession(session);
            if (user == null) {
                renderJSON(RtnUtil.returnLoginFail());
            }
            ShopWeTaoService.comment(weTaoId, user.getId().intValue(), user.getAvatarUrl(), user.getNickName(), comment, request.remoteAddress);


            Map<String, Object> response = wrapComments(page, pageSize, weTaoId, user.getId().intValue());
            renderJSON(RtnUtil.returnSuccess("OK", response));

        } catch (Exception e) {
            Logger.error(e, e.getMessage());
            renderJSON(RtnUtil.returnFail("服务器异常"));
        }
    }

    public static void listWeTaoComment(String session, int weTaoId, int page, int pageSize) {
        try {
            Integer userId = null;
            UsersDDL user = UserService.findBySession(session);
            if (user != null) {
                userId = user.getId().intValue();
            }
            Map<String, Object> response = wrapComments(page, pageSize, weTaoId, userId);
            renderJSON(RtnUtil.returnSuccess("OK", response));
        } catch (Exception e) {
            Logger.error(e, e.getMessage());
            renderJSON(RtnUtil.returnFail("服务器异常"));
        }
    }

    private static Map<String, Object> wrapComments(int page, int pageSize, int weTaoId, Integer userId) {
        List<Map<String, Object>> comments = new ArrayList<Map<String, Object>>();
        Map<String, Object> response = new HashMap<String, Object>();

        int total = ShopWeTaoCommentService.countComment(weTaoId);
        response.put("total", total);
        response.put("pageTotal", Math.ceil(total / (double) pageSize));

        if (total == 0) {
            response.put("list", comments);
            return response;
        }

        page = page == 0 ? 1 : page;
        pageSize = pageSize == 0 ? 10 : pageSize;

        List<ShopWetaoCommentDDL> list = ShopWeTaoCommentService.list(weTaoId, page, pageSize);


        for (ShopWetaoCommentDDL comment : list) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", comment.getId());
            map.put("weTaoId", comment.getWetaoId());
            map.put("createTime", DateUtil.timeDesc(comment.getCreateTime()));
            map.put("comment", comment.getComment());
            map.put("nickName", comment.getNickName());
            map.put("avatar", comment.getAvatar());
            map.put("userId", comment.getUserId());
            if (userId != null && comment.getUserId() == userId.intValue()) {
                map.put("isAdmin", true);
            }
            comments.add(map);
        }
        response.put("list", comments);
        return response;
    }

    public static void categoryALL(String shopId) {
        try {
            SelectSourceDto selectSource = ShopCategoryService.reflushCategoryALL(shopId, false);
            renderJSON(RtnUtil.returnSuccess("OK", selectSource));
        } catch (Exception e) {
            Logger.error(e, e.getMessage());
            renderJSON(RtnUtil.returnFail("服务器异常"));
        }
    }

    public static void addTocar(String session, String productId, String groupId, int buyNum) {
        try {
            UsersDDL user = UserService.findBySession(session);
            if (user == null) {
                renderJSON(RtnUtil.returnLoginFail());
            }

            if (buyNum <= 0) {
                renderJSON(RtnUtil.returnFail("购买数量不能少于1"));
            }

            ShopProductDDL product = ShopProductService.getByProductId(productId);
            if (product == null) {
                renderJSON(RtnUtil.returnFail("商品已经下架"));
            }

            ShopProductGroupDDL productGroup = null;

            if (StringUtils.isEmpty(groupId)) {
                List<ShopProductGroupDDL> groups = ShopProductGroupService.findByProductId(productId);
                if (groups != null && groups.size() > 0) {
                    productGroup = groups.get(0);
                }
            } else {
                productGroup = ShopProductGroupService.findByProductIdAndGroupId(productId, groupId);
            }
            if (productGroup == null) {
                renderJSON(RtnUtil.returnFail("商品此规格已经下架"));
            }


            ShopCarDDL shopCar = new ShopCarDDL();
            shopCar.setBuyNum(buyNum);
            shopCar.setCreateTime(System.currentTimeMillis());
            shopCar.setUpdateTime(System.currentTimeMillis());
            shopCar.setGroupId(productGroup.getGroupId());
            shopCar.setProductId(productGroup.getProductId());
            shopCar.setShopId(product.getShopId());

            ShopIndexDDL shop = ShopIndexService.getByShopId(product.getShopId());
            shopCar.setShopName(shop == null ? "" : shop.getName());

            shopCar.setStatus(ShopCarService.UN_PAYED);
            shopCar.setUserId(user.getId().intValue());


            ShopCarService.addToShopCar(shopCar);

            renderJSON(RtnUtil.returnSuccess("OK"));
        } catch (Exception e) {
            Logger.error(e, e.getMessage());
            renderJSON(RtnUtil.returnFail("服务器异常"));
        }
    }

    private static long getSecKillingEndTimes(int time) {
        //设置秒杀结束时间
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, time);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.add(Calendar.HOUR_OF_DAY, 2);
        return c.getTimeInMillis();
    }

    public static void listMyCar(String session, int page, int pageSize) {
        try {
            UsersDDL user = UserService.findBySession(session);

            if (user == null) {
                renderJSON(RtnUtil.returnLoginFail());
            }


            List<ShopCarDDL> carList = ShopCarService.myCarList(user.getId().intValue(), page, pageSize);

            Map<String, Object> result = new HashMap<String, Object>();
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

            if (carList == null || carList.size() == 0) {
                result.put("list", list);
                renderJSON(RtnUtil.returnSuccess("OK", result));
            }
            int sumAmount = 0;
            for (ShopCarDDL car : carList) {
                Map<String, Object> one = new HashMap<String, Object>();
                ShopProductDDL product = ShopProductService.getByProductId(car.getProductId());
                ShopProductGroupDDL group = ShopProductGroupService.findByProductIdAndGroupId(car.getProductId(), car.getGroupId());
                if (group == null || product == null) continue;
                one.put("id", car.getId());
                one.put("productId", product.getProductId());
                one.put("groupId", group.getGroupId());
                one.put("productName", product.getProductName());
                one.put("groupImgage", API.getObjectAccessUrlSimple(group.getGroupImage()));
                one.put("buyNum", car.getBuyNum());
                one.put("status", car.getStatus());
                one.put("groupName", group.getGroupName());

                one.put("checked", true);

                //正在秒杀活动
                if (product.getJoinSeckilling() != null && product.getJoinSeckilling() == 1 &&
                        getSecKillingEndTimes(product.getSeckillingTime()) > System.currentTimeMillis()
                ) {
                    one.put("joinSeckilling", product.getJoinSeckilling());
                    one.put("seckillingPrice", AmountUtil.f2y(product.getSeckillingPrice()));

                    one.put("totalAmount", AmountUtil.f2y(product.getSeckillingPrice() * car.getBuyNum()));//实时
                    one.put("singPrice", AmountUtil.f2y(product.getSeckillingPrice()));//实时

                    String secKillingTime = DateUtil.format(getSecKillingEndTimes(product.getSeckillingTime()), "HH:mm");
                    one.put("secKillingTime", secKillingTime);

                } else {
                    one.put("totalAmount", AmountUtil.f2y(group.getGroupPrice() * car.getBuyNum()));//实时
                    one.put("singPrice", AmountUtil.f2y(group.getGroupPrice()));//实时
                }

                sumAmount += group.getGroupPrice() * car.getBuyNum();
                list.add(one);
            }
            result.put("sumAmount", AmountUtil.f2y(sumAmount));
            result.put("list", list);

            UserAccountDDL basicAccount = UserAccountService.getBasicAccount(user.getId().intValue());
            result.put("userAmount", AmountUtil.f2y(basicAccount.getAmount()));
            renderJSON(RtnUtil.returnSuccess("OK", result));

        } catch (Exception e) {
            Logger.error(e, e.getMessage());
            renderJSON(RtnUtil.returnFail("服务器异常"));
        }
    }

    public static void calCoupon(String session, String amount) {
        try {
            UsersDDL user = UserService.findBySession(session);
            if (user == null) {
                renderJSON(RtnUtil.returnLoginFail());
            }
            UserAccountDDL coupon = UserAccountService.fullReduce(user.getId().intValue(), AmountUtil.y2f(amount));
            Map<String, Object> result = new HashMap<String, Object>();
            if (coupon != null) {
                result.put("couponId", coupon.getAccountId());
                result.put("couponName", coupon.getAccountName());
                result.put("couponAmount", AmountUtil.f2y(coupon.getAmount()));
            } else {
                result.put("couponId", "");
                result.put("couponName", "");
                result.put("couponAmount", 0);
            }
            renderJSON(RtnUtil.returnSuccess("OK", result));
        } catch (Exception e) {
            Logger.error(e, e.getMessage());
            renderJSON(RtnUtil.returnFail("服务器异常"));
        }
    }

    public static void deleteProductFromMyCar(String session, int id) {
        try {
            UsersDDL user = UserService.findBySession(session);
            if (user == null) {
                renderJSON(RtnUtil.returnLoginFail());
            }
            ShopCarService.updateStatus(id, user.getId().intValue(), ShopCarService.DELETED);
            renderJSON(RtnUtil.returnSuccess("OK"));
        } catch (Exception e) {
            Logger.error(e, e.getMessage());
            renderJSON(RtnUtil.returnFail("服务器异常"));
        }
    }

    public static void updateBuyNumOfMyCar(String session, int id, int buyNum) {
        try {
            UsersDDL user = UserService.findBySession(session);
            if (user == null) {
                renderJSON(RtnUtil.returnLoginFail());
            }
            ShopCarService.updateBuyNum(id, user.getId().intValue(), buyNum);
            renderJSON(RtnUtil.returnSuccess("OK"));
        } catch (Exception e) {
            Logger.error(e, e.getMessage());
            renderJSON(RtnUtil.returnFail("服务器异常"));
        }
    }
}
