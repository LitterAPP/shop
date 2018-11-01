package modules.shop.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;

/**
 * @author auto
 * @createDate 2017-12-15 17:04:53
 **/
@Table(name = "shop_together_joiner")
public class ShopTogetherJoinerDDL {
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

    @Column(name = "product_id", type = DbType.Varchar)
    private String productId;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    @Column(name = "together_id", type = DbType.Varchar)
    private String togetherId;

    public String getTogetherId() {
        return togetherId;
    }

    public void setTogetherId(String togetherId) {
        this.togetherId = togetherId;
    }

    @Column(name = "user_id", type = DbType.Int)
    private Integer userId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Column(name = "user_avatar", type = DbType.Varchar)
    private String userAvatar;

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    @Column(name = "user_name", type = DbType.Varchar)
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Column(name = "is_master", type = DbType.Int)
    private Integer isMaster;

    public Integer getIsMaster() {
        return isMaster;
    }

    public void setIsMaster(Integer isMaster) {
        this.isMaster = isMaster;
    }

    @Column(name = "join_time", type = DbType.DateTime)
    private Long joinTime;

    public Long getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(Long joinTime) {
        this.joinTime = joinTime;
    }

    @Column(name = "order_id", type = DbType.Varchar)
    private String orderId;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public static ShopTogetherJoinerDDL newExample() {
        ShopTogetherJoinerDDL object = new ShopTogetherJoinerDDL();
        object.setId(null);
        object.setProductId(null);
        object.setTogetherId(null);
        object.setUserId(null);
        object.setUserAvatar(null);
        object.setUserName(null);
        object.setIsMaster(null);
        object.setJoinTime(null);
        object.setOrderId(null);
        return object;
    }
}
