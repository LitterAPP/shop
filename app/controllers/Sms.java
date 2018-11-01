package controllers;

import jws.Logger;
import jws.cache.Cache;
import jws.mvc.Controller;
import modules.shop.service.SmsService;
import util.RtnUtil;

public class Sms extends Controller {

    public static void sendCode(String mobile) {
        try {
            if (Cache.get("sendAutCode_" + mobile) != null) {
                renderJSON(RtnUtil.returnFail("发送短信验证码太频繁"));
            }
            int code = SmsService.sendAuthCode(mobile);
            if (code == 0) {
                renderJSON(RtnUtil.returnFail("发送短信验证码失败,请稍后再试"));
            }
            Cache.set("sendAutCode_" + mobile, "1", "60s");
            renderJSON(RtnUtil.returnSuccess("OK", code));
        } catch (Exception e) {
            Logger.error(e, e.getMessage());
            renderJSON(RtnUtil.returnFail("发送短信验证码失败,请稍后再试"));
        }
    }

    public static void validateCode(String mobile, int code) {
        try {
            boolean authSms = SmsService.validateSmsCode(mobile, code);
            if (!authSms) {
                renderJSON(RtnUtil.returnFail("短信验证失败"));
            }
            renderJSON(RtnUtil.returnSuccess("OK"));
        } catch (Exception e) {
            Logger.error(e, e.getMessage());
            renderJSON(RtnUtil.returnFail("发送短信验证码失败,请稍后再试"));
        }
    }
}
