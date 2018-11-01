package modules.shop.service;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import modules.shop.ddl.UsersDDL;

public class UserService {

    public static UsersDDL get(int id) {
        return Dal.select("UsersDDL.*", id);
    }

    public static UsersDDL login(String session) {
        return findBySession(session);
    }

    public static UsersDDL login(String mobile, int code) {
        boolean authSms = SmsService.validateSmsCode(mobile, code);
        if (!authSms) return null;
        UsersDDL exist = findByMoible(mobile);
        if (exist != null) {
            exist.setSession(UUID.randomUUID().toString());
            Dal.update(exist, "UsersDDL.session", new Condition("UsersDDL.id", "=", exist.getId()));
            return exist;
        }
        UsersDDL user = new UsersDDL();
        user.setMobile(mobile);
        user.setSession(UUID.randomUUID().toString());

        if (Dal.insert(user) > 0) {
            return user;
        }
        return null;
    }

    public static UsersDDL findByMoible(String mobile) {
        Condition condition = new Condition("UsersDDL.mobile", "=", mobile);
        List<UsersDDL> list = Dal.select("UsersDDL.*", condition, null, 0, 1);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    public static UsersDDL findBySession(String session) {
        if (StringUtils.isEmpty(session)) {
            return null;
        }
        Condition condition = new Condition("UsersDDL.session", "=", session);
        List<UsersDDL> list = Dal.select("UsersDDL.*", condition, null, 0, 1);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    public static List<UsersDDL> findMJH() {

        Condition condition = new Condition("UsersDDL.session", "like", "MJH%");
        List<UsersDDL> list = Dal.select("UsersDDL.*", condition, null, 0, -1);

        return list;
    }

    public static UsersDDL findByOpenId(String openId) {
        if (StringUtils.isEmpty(openId)) {
            return null;
        }
        Condition condition = new Condition("UsersDDL.openId", "=", openId);
        List<UsersDDL> list = Dal.select("UsersDDL.*", condition, null, 0, 1);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 更新用户资料
     *
     * @param session
     * @param openId
     * @param avatarUrl
     * @param city
     * @param country
     * @param gender
     * @param nickname
     * @param province
     * @param nickName
     * @return
     */
    public static UsersDDL updateUser(String session,
                                      String avatarUrl, String city,
                                      String country, Integer gender,
                                      String province, String nickName,
                                      String openId
    ) {

        UsersDDL existBySession = findBySession(session);
        UsersDDL existByOpenId = findByOpenId(openId);

        if (existBySession == null && existByOpenId == null) {
            UsersDDL newUser = new UsersDDL();
            newUser.setSession(UUID.randomUUID().toString());
            newUser.setAvatarUrl(avatarUrl);
            newUser.setCity(city);
            newUser.setCountry(country);
            newUser.setGender(gender);
            newUser.setNickName(nickName);
            newUser.setOpenId(openId);
            newUser.setMobile("");
            newUser.setProvince(province);
            long id = Dal.insertSelectLastId(newUser);
            newUser.setId(id);
            return newUser;
        }

        UsersDDL exist = existByOpenId == null ? existBySession : existByOpenId;

        exist.setAvatarUrl(avatarUrl);
        exist.setCity(city);
        exist.setCountry(country);
        exist.setGender(gender);
        exist.setNickName(nickName);
        exist.setOpenId(openId);
        exist.setSession(exist.getSession());
        exist.setProvince(province);
        Dal.update(exist, "UsersDDL.avatarUrl,UsersDDL.session,"
                        + "UsersDDL.city,UsersDDL.country,"
                        + "UsersDDL.gender,UsersDDL.nickName,"
                        + "UsersDDL.openId,UsersDDL.province",
                new Condition("UsersDDL.id", "=", exist.getId())
        );
        return exist;
    }

    public static void becomeSeller(int userId, String mobile, String sellerWx) throws Exception {
        UsersDDL user = get(userId);
        if (user == null) {
            throw new Exception("用户不存在");
        }
        user.setIsSeller(1);
        user.setSellerMobile(mobile);
        user.setSellerWx(sellerWx);
        Dal.update(user,
                "UsersDDL.isSeller,UsersDDL.sellerMobile,UsersDDL.sellerWx",
                new Condition("UsersDDL.id", "=", user.getId()));

    }
}
