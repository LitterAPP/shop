package modules.shop.service;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import jws.Jws;
import jws.Logger;
import jws.cache.Cache;
import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import modules.shop.ddl.ShopExpressCodeDDL;
import modules.shop.ddl.ShopExpressDDL;
import modules.shop.ddl.ShopOrderDDL;
import modules.shop.service.dto.ShipperApiResp;
import modules.shop.service.dto.ShipperApiResp.Trace;
import util.API;
import util.DateUtil;
import util.QQSMSUtil;

public class ShopExpressService {

    //物流状态：-3：出库中，-2:已出库交给快递 -1：单号或快递公司代码错误, 0：暂无轨迹，2：在途中,3：签收,4：问题件
    public static final int STATE_F3 = -3;
    public static final int STATE_F2 = -2;
    public static final int STATE_F1 = -1;
    public static final int STATE_0 = 0;
    public static final int STATE_4 = 4;
    public static final int STATE_3 = 3;


    public static ShopExpressDDL getById(int id) {
        return Dal.select("ShopExpressDDL.*", id);
    }

    public static ShopExpressDDL getByOrderId(String orderId) {
        Condition condition = new Condition("ShopExpressDDL.orderId", "=", orderId);
        List<ShopExpressDDL> list = Dal.select("ShopExpressDDL.*", condition, null, 0, 1);
        if (list == null || list.size() == 0) return null;
        return list.get(0);
    }


    private static final GsonBuilder gb = new GsonBuilder();

    //物流状态：-3：出库中，-2:已出库交给快递 -1：单号或快递公司代码错误, 0：暂无轨迹，2：在途中,3：签收,4：问题件
    public static ShopExpressDDL updateExpressTrace(ShopExpressDDL express) {
        if (express == null || StringUtils.isEmpty(express.getOrderCode()) || StringUtils.isEmpty(express.getShipperCode())) {
            return express;
        }
        String key = "EXPRESS_" + express.getOrderId();
        Object value = Cache.get(key);
        //不需要查物流接口
        if (express.getState() == ShopExpressService.STATE_F3 ||
                express.getState() == ShopExpressService.STATE_F1 ||
                express.getState() == ShopExpressService.STATE_3 ||
                express.getState() == ShopExpressService.STATE_4 ||
                value != null
        ) {
            return express;
        }
        Type type = new TypeToken<ShipperApiResp>() {
        }.getType();
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("n", express.getOrderCode());
        querys.put("t", express.getShipperCode());
        ShipperApiResp resp = API.aliAPI("http://wdexpress.market.alicloudapi.com", "/gxali", "GET", querys, type, "6914bab576494f64b376ecffb8ee81eb");
        if (resp != null && resp.isSuccess() && !resp.getState().equals("0")) {
            //先更新Express状态
            express.setState(Integer.parseInt(resp.getState()));
            List<Trace> traces = resp.getTraces();
            if (traces != null && traces.size() > 0) {
                Collections.sort(traces);
                express.setAcceptStation(traces.get(0).getAcceptStation());
                express.setAcceptTime(traces.get(0).getAcceptTime());
                gb.disableHtmlEscaping();
                express.setTraces(gb.create().toJson(traces).replace("<a>", "").replace("</a>", ""));
            }
            boolean expressUpdate = Dal.update(express, "ShopExpressDDL.state,ShopExpressDDL.traces,ShopExpressDDL.acceptStation,ShopExpressDDL.acceptTime", new Condition("ShopExpressDDL.id", "=", express.getId())) > 0;
            if (expressUpdate) {
                Cache.set(key, "1", "60mn");
            }
        }
        return express;
    }

    /**
     * 初始化快递数据
     *
     * @param orderId
     * @return
     */
    public static boolean initExpress(String orderId) {
        ShopExpressDDL express = getByOrderId(orderId);
        if (express != null) {
            return false;
        }
        ShopExpressDDL sed = new ShopExpressDDL();
        sed.setOrderId(orderId);
        sed.setAcceptStation("商家已接单，准备发货中...");
        sed.setAcceptTime(DateUtil.format(System.currentTimeMillis()));
        sed.setOrderCode(null);
        sed.setShipperCode(null);
        sed.setShipperName(null);
        sed.setState(STATE_F3);
        sed.setTraces("[]");

        //发送短信给商家，提醒发货了
        try {
            ShopOrderDDL order = ShopOrderService.findByOrderId(orderId);
            QQSMSUtil.sendWithParam("86", order.getSellerTelNumber(),
                    Integer.parseInt(String.valueOf(Jws.configuration.get("tencent.sms.tmp_notify.id"))),
                    null,
                    String.valueOf(Jws.configuration.get("tencent.sms.tmp_signname")),
                    null, null);
        } catch (Exception e) {
            Logger.info(e, e.getMessage());
        }
        return Dal.insert(sed) > 0;
    }

    /**
     * 提交给物流公司
     *
     * @param express
     * @param orderCode
     * @param shipperCode
     * @param shipperName
     * @return
     */
    public static boolean commitShipper(ShopExpressDDL express, String orderCode, String shipperCode, String shipperName) {
        if (express == null) return false;
        express.setOrderCode(orderCode);
        express.setShipperCode(shipperCode);
        express.setShipperName(shipperName);
        express.setState(STATE_F2);
        express.setAcceptStation("快递公司【" + shipperName + "】已揽件，单号：" + orderCode);
        express.setAcceptTime(DateUtil.format(System.currentTimeMillis()));
        boolean result = Dal.insertUpdate(express, "ShopExpressDDL.orderCode,ShopExpressDDL.shipperCode,ShopExpressDDL.state,ShopExpressDDL.shipperName,ShopExpressDDL.acceptStation,ShopExpressDDL.acceptTime") > 0;

        //通知买家已经发货了

        return result;
    }


    public static List<ShopExpressCodeDDL> listExpress() {
        return Dal.select("ShopExpressCodeDDL.*", null, null, 0, -1);
    }

    public static ShopExpressCodeDDL getByCode(String code) {
        Condition cond = new Condition("ShopExpressCodeDDL.shipperCode", "=", code);
        List<ShopExpressCodeDDL> list = Dal.select("ShopExpressCodeDDL.*", cond, null, 0, 1);
        if (list == null || list.size() == 0) return null;
        return list.get(0);
    }
}
