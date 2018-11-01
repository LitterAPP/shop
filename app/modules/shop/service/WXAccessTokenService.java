package modules.shop.service;

import java.util.List;

import com.google.gson.JsonObject;

import jws.Logger;
import jws.cache.Cache;
import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import modules.shop.ddl.WxAccessTokenDDL;
import util.API;

public class WXAccessTokenService {
    public static String fromCache(String appId) {
        try {
            String key = appId + "_accesstoken";
            Object fc = Cache.get(key);
            if (fc == null) {
                JsonObject tokenJson = API.requestAccessToken(appId);
                if (tokenJson == null) {
                    return null;
                }
                if (tokenJson.get("errcode") != null) {
                    Logger.error("请求微信获取accessToken错误,%s", tokenJson.toString());
                    return null;
                }
                String accessTokenStr = tokenJson.get("access_token").getAsString();
                long expireIn = tokenJson.get("expires_in").getAsLong();
                Cache.set(key, accessTokenStr, (expireIn - 10) + "s");
                return accessTokenStr;
            } else {
                return String.valueOf(fc);
            }
        } catch (Exception e) {
            Logger.error(e, e.getMessage());
            return null;
        }
    }

    public static WxAccessTokenDDL get(String appId) {
        List<WxAccessTokenDDL> results = Dal.select("WxAccessTokenDDL.*", new Condition("WxAccessTokenDDL.appId", "=", appId), null, 0, 1);
        if (results == null || results.size() == 0 || results.get(0).getExpiresIn() < System.currentTimeMillis() + 30 * 1000) {
            JsonObject tokenJson = API.requestAccessToken(appId);
            if (tokenJson == null) {
                return null;
            }
            if (tokenJson.get("errcode") != null) {
                Logger.error("请求微信获取accessToken错误,%s", tokenJson.toString());
                return null;
            }
            String accessTokenStr = tokenJson.get("access_token").getAsString();
            long expireIn = tokenJson.get("expires_in").getAsLong() * 1000;


            if (results == null || results.size() == 0) {
                WxAccessTokenDDL newWxAccessTokenDDL = new WxAccessTokenDDL();
                newWxAccessTokenDDL.setAccessToken(accessTokenStr);
                newWxAccessTokenDDL.setExpiresIn(System.currentTimeMillis() + expireIn);
                newWxAccessTokenDDL.setAppId(appId);
                Dal.insert(newWxAccessTokenDDL);
                return newWxAccessTokenDDL;
            } else {
                WxAccessTokenDDL old = results.get(0);
                old.setAccessToken(accessTokenStr);
                old.setExpiresIn(System.currentTimeMillis() + expireIn);
                return old;
            }
        }
        return results.get(0);
    }
}
