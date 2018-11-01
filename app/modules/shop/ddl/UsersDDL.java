package modules.shop.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;

/**
 * @author auto
 * @createDate 2018-01-08 14:09:19
 **/
@Table(name = "users")
public class UsersDDL {
    @Id
    @GeneratedValue(generationType = GenerationType.Auto)
    @Column(name = "id", type = DbType.BigInt)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "open_id", type = DbType.Varchar)
    private String openId;

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    @Column(name = "mobile", type = DbType.Varchar)
    private String mobile;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Column(name = "session", type = DbType.Varchar)
    private String session;

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    @Column(name = "nick_name", type = DbType.Varchar)
    private String nickName;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Column(name = "avatar_url", type = DbType.Varchar)
    private String avatarUrl;

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @Column(name = "gender", type = DbType.Int)
    private Integer gender;

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    @Column(name = "province", type = DbType.Varchar)
    private String province;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    @Column(name = "city", type = DbType.Varchar)
    private String city;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Column(name = "country", type = DbType.Varchar)
    private String country;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Column(name = "is_seller", type = DbType.Int)
    private Integer isSeller;

    public Integer getIsSeller() {
        return isSeller;
    }

    public void setIsSeller(Integer isSeller) {
        this.isSeller = isSeller;
    }

    @Column(name = "seller_mobile", type = DbType.Varchar)
    private String sellerMobile;

    public String getSellerMobile() {
        return sellerMobile;
    }

    public void setSellerMobile(String sellerMobile) {
        this.sellerMobile = sellerMobile;
    }

    @Column(name = "seller_wx", type = DbType.Varchar)
    private String sellerWx;

    public String getSellerWx() {
        return sellerWx;
    }

    public void setSellerWx(String sellerWx) {
        this.sellerWx = sellerWx;
    }

    public static UsersDDL newExample() {
        UsersDDL object = new UsersDDL();
        object.setId(null);
        object.setOpenId(null);
        object.setMobile(null);
        object.setSession(null);
        object.setNickName(null);
        object.setAvatarUrl(null);
        object.setGender(null);
        object.setProvince(null);
        object.setCity(null);
        object.setCountry(null);
        object.setIsSeller(null);
        object.setSellerMobile(null);
        object.setSellerWx(null);
        return object;
    }
}
