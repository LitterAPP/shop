package modules.shop.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;
/**
 * 
 * @author auto
 * @createDate 2018-04-10 11:37:52
 **/
@Table(name="shop_coupon_mng")
public class ShopCouponMngDDL{
	@Id
	@GeneratedValue(generationType= GenerationType.Auto)
	@Column(name="id", type=DbType.Int)
	private Integer id;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id){
		this.id=id;
	}

	@Column(name="coupon_id", type=DbType.Varchar)
	private String couponId;
	public String getCouponId() {
		return couponId;
	}
	public void setCouponId(String couponId){
		this.couponId=couponId;
	}

	@Column(name="coupon_name", type=DbType.Varchar)
	private String couponName;
	public String getCouponName() {
		return couponName;
	}
	public void setCouponName(String couponName){
		this.couponName=couponName;
	}

	@Column(name="amount", type=DbType.Int)
	private Integer amount;
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount){
		this.amount=amount;
	}

	@Column(name="limit_product_id", type=DbType.Varchar)
	private String limitProductId;
	public String getLimitProductId() {
		return limitProductId;
	}
	public void setLimitProductId(String limitProductId){
		this.limitProductId=limitProductId;
	}

	@Column(name="limit_seller_id", type=DbType.Int)
	private Integer limitSellerId;
	public Integer getLimitSellerId() {
		return limitSellerId;
	}
	public void setLimitSellerId(Integer limitSellerId){
		this.limitSellerId=limitSellerId;
	}

	@Column(name="limit_price", type=DbType.Int)
	private Integer limitPrice;
	public Integer getLimitPrice() {
		return limitPrice;
	}
	public void setLimitPrice(Integer limitPrice){
		this.limitPrice=limitPrice;
	}

	@Column(name="limit_times", type=DbType.Int)
	private Integer limitTimes;
	public Integer getLimitTimes() {
		return limitTimes;
	}
	public void setLimitTimes(Integer limitTimes){
		this.limitTimes=limitTimes;
	}

	@Column(name="coupon_type", type=DbType.Int)
	private Integer couponType;
	public Integer getCouponType() {
		return couponType;
	}
	public void setCouponType(Integer couponType){
		this.couponType=couponType;
	}

	@Column(name="expire_time", type=DbType.DateTime)
	private Long expireTime;
	public Long getExpireTime() {
		return expireTime;
	}
	public void setExpireTime(Long expireTime){
		this.expireTime=expireTime;
	}

	@Column(name="start_time", type=DbType.DateTime)
	private Long startTime;
	public Long getStartTime() {
		return startTime;
	}
	public void setStartTime(Long startTime){
		this.startTime=startTime;
	}

	@Column(name="end_time", type=DbType.DateTime)
	private Long endTime;
	public Long getEndTime() {
		return endTime;
	}
	public void setEndTime(Long endTime){
		this.endTime=endTime;
	}

	@Column(name="create_time", type=DbType.DateTime)
	private Long createTime;
	public Long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Long createTime){
		this.createTime=createTime;
	}

	public static ShopCouponMngDDL newExample(){
		ShopCouponMngDDL object=new ShopCouponMngDDL();
		object.setId(null);
		object.setCouponId(null);
		object.setCouponName(null);
		object.setAmount(null);
		object.setLimitProductId(null);
		object.setLimitSellerId(null);
		object.setLimitPrice(null);
		object.setLimitTimes(null);
		object.setCouponType(null);
		object.setExpireTime(null);
		object.setStartTime(null);
		object.setEndTime(null);
		object.setCreateTime(null);
		return object;
	}
}
