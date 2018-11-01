package modules.shop.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;

/**
 * @author auto
 * @createDate 2017-12-15 16:21:21
 **/
@Table(name = "wx_access_token")
public class WxAccessTokenDDL {
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

    @Column(name = "access_token", type = DbType.Varchar)
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Column(name = "expires_in", type = DbType.DateTime)
    private Long expiresIn;

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    @Column(name = "app_id", type = DbType.Varchar)
    private String appId;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public static WxAccessTokenDDL newExample() {
        WxAccessTokenDDL object = new WxAccessTokenDDL();
        object.setId(null);
        object.setAccessToken(null);
        object.setExpiresIn(null);
        object.setAppId(null);
        return object;
    }
}
