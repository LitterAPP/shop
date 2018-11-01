package modules.shop.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;

/**
 * @author auto
 * @createDate 2018-04-26 14:51:46
 **/
@Table(name = "shop_mng_session")
public class ShopMngSessionDDL {
    @Id
    @GeneratedValue(generationType = GenerationType.Auto)
    @Column(name = "id", type = DbType.Int)
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "session", type = DbType.Varchar)
    private String session;

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    @Column(name = "user_id", type = DbType.Int)
    private Integer userId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Column(name = "user_name", type = DbType.Varchar)
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Column(name = "shop_id", type = DbType.Varchar)
    private String shopId;

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    @Column(name = "shop_qrcode_key", type = DbType.Varchar)
    private String shopQrcodeKey;

    public String getShopQrcodeKey() {
        return shopQrcodeKey;
    }

    public void setShopQrcodeKey(String shopQrcodeKey) {
        this.shopQrcodeKey = shopQrcodeKey;
    }

    @Column(name = "expire_time", type = DbType.DateTime)
    private Long expireTime;

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    @Column(name = "create_time", type = DbType.DateTime)
    private Long createTime;

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public static ShopMngSessionDDL newExample() {
        ShopMngSessionDDL object = new ShopMngSessionDDL();
        object.setId(null);
        object.setSession(null);
        object.setUserId(null);
        object.setUserName(null);
        object.setShopId(null);
        object.setShopQrcodeKey(null);
        object.setExpireTime(null);
        object.setCreateTime(null);
        return object;
    }
}
