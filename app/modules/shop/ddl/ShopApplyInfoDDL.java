package modules.shop.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;

/**
 * @author auto
 * @createDate 2018-01-16 10:54:13
 **/
@Table(name = "shop_apply_info")
public class ShopApplyInfoDDL {
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

    @Column(name = "user_id", type = DbType.Int)
    private Integer userId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Column(name = "front_card_key", type = DbType.Varchar)
    private String frontCardKey;

    public String getFrontCardKey() {
        return frontCardKey;
    }

    public void setFrontCardKey(String frontCardKey) {
        this.frontCardKey = frontCardKey;
    }

    @Column(name = "back_card_key", type = DbType.Varchar)
    private String backCardKey;

    public String getBackCardKey() {
        return backCardKey;
    }

    public void setBackCardKey(String backCardKey) {
        this.backCardKey = backCardKey;
    }

    @Column(name = "mobile", type = DbType.Varchar)
    private String mobile;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Column(name = "wx", type = DbType.Varchar)
    private String wx;

    public String getWx() {
        return wx;
    }

    public void setWx(String wx) {
        this.wx = wx;
    }

    @Column(name = "fee_rate", type = DbType.Int)
    private Integer feeRate;

    public Integer getFeeRate() {
        return feeRate;
    }

    public void setFeeRate(Integer feeRate) {
        this.feeRate = feeRate;
    }

    @Column(name = "create_time", type = DbType.DateTime)
    private Long createTime;

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public static ShopApplyInfoDDL newExample() {
        ShopApplyInfoDDL object = new ShopApplyInfoDDL();
        object.setId(null);
        object.setUserId(null);
        object.setFrontCardKey(null);
        object.setBackCardKey(null);
        object.setMobile(null);
        object.setWx(null);
        object.setFeeRate(null);
        object.setCreateTime(null);
        return object;
    }
}
