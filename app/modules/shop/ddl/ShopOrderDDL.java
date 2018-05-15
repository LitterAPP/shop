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
 * @createDate 2018-04-25 17:51:31
 **/
@Table(name="shop_order")
public class ShopOrderDDL{
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

	@Column(name="order_id", type=DbType.Varchar)
	private String orderId;
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId){
		this.orderId=orderId;
	}

	@Column(name="group_id", type=DbType.Varchar)
	private String groupId;
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId){
		this.groupId=groupId;
	}

	@Column(name="group_name", type=DbType.Varchar)
	private String groupName;
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName){
		this.groupName=groupName;
	}

	@Column(name="group_img", type=DbType.Varchar)
	private String groupImg;
	public String getGroupImg() {
		return groupImg;
	}
	public void setGroupImg(String groupImg){
		this.groupImg=groupImg;
	}

	@Column(name="shop_id", type=DbType.Varchar)
	private String shopId;
	public String getShopId() {
		return shopId;
	}
	public void setShopId(String shopId){
		this.shopId=shopId;
	}

	@Column(name="product_id", type=DbType.Varchar)
	private String productId;
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId){
		this.productId=productId;
	}

	@Column(name="product_name", type=DbType.Varchar)
	private String productName;
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName){
		this.productName=productName;
	}

	@Column(name="buyer_user_id", type=DbType.Int)
	private Integer buyerUserId;
	public Integer getBuyerUserId() {
		return buyerUserId;
	}
	public void setBuyerUserId(Integer buyerUserId){
		this.buyerUserId=buyerUserId;
	}

	@Column(name="seller_user_id", type=DbType.Int)
	private Integer sellerUserId;
	public Integer getSellerUserId() {
		return sellerUserId;
	}
	public void setSellerUserId(Integer sellerUserId){
		this.sellerUserId=sellerUserId;
	}

	@Column(name="status", type=DbType.Int)
	private Integer status;
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status){
		this.status=status;
	}

	@Column(name="product_origin_amount", type=DbType.Int)
	private Integer productOriginAmount;
	public Integer getProductOriginAmount() {
		return productOriginAmount;
	}
	public void setProductOriginAmount(Integer productOriginAmount){
		this.productOriginAmount=productOriginAmount;
	}

	@Column(name="product_now_amount", type=DbType.Int)
	private Integer productNowAmount;
	public Integer getProductNowAmount() {
		return productNowAmount;
	}
	public void setProductNowAmount(Integer productNowAmount){
		this.productNowAmount=productNowAmount;
	}

	@Column(name="product_together_amount", type=DbType.Int)
	private Integer productTogetherAmount;
	public Integer getProductTogetherAmount() {
		return productTogetherAmount;
	}
	public void setProductTogetherAmount(Integer productTogetherAmount){
		this.productTogetherAmount=productTogetherAmount;
	}

	@Column(name="group_price", type=DbType.Int)
	private Integer groupPrice;
	public Integer getGroupPrice() {
		return groupPrice;
	}
	public void setGroupPrice(Integer groupPrice){
		this.groupPrice=groupPrice;
	}

	@Column(name="group_together_price", type=DbType.Int)
	private Integer groupTogetherPrice;
	public Integer getGroupTogetherPrice() {
		return groupTogetherPrice;
	}
	public void setGroupTogetherPrice(Integer groupTogetherPrice){
		this.groupTogetherPrice=groupTogetherPrice;
	}

	@Column(name="use_coupon_account_id", type=DbType.Varchar)
	private String useCouponAccountId;
	public String getUseCouponAccountId() {
		return useCouponAccountId;
	}
	public void setUseCouponAccountId(String useCouponAccountId){
		this.useCouponAccountId=useCouponAccountId;
	}

	@Column(name="use_coupon_amount", type=DbType.Int)
	private Integer useCouponAmount;
	public Integer getUseCouponAmount() {
		return useCouponAmount;
	}
	public void setUseCouponAmount(Integer useCouponAmount){
		this.useCouponAmount=useCouponAmount;
	}

	@Column(name="use_user_account_id", type=DbType.Varchar)
	private String useUserAccountId;
	public String getUseUserAccountId() {
		return useUserAccountId;
	}
	public void setUseUserAccountId(String useUserAccountId){
		this.useUserAccountId=useUserAccountId;
	}

	@Column(name="use_user_amount", type=DbType.Int)
	private Integer useUserAmount;
	public Integer getUseUserAmount() {
		return useUserAmount;
	}
	public void setUseUserAmount(Integer useUserAmount){
		this.useUserAmount=useUserAmount;
	}

	@Column(name="use_cash", type=DbType.Int)
	private Integer useCash;
	public Integer getUseCash() {
		return useCash;
	}
	public void setUseCash(Integer useCash){
		this.useCash=useCash;
	}

	@Column(name="buy_num", type=DbType.Int)
	private Integer buyNum;
	public Integer getBuyNum() {
		return buyNum;
	}
	public void setBuyNum(Integer buyNum){
		this.buyNum=buyNum;
	}

	@Column(name="platform_gets_rate", type=DbType.Int)
	private Integer platformGetsRate;
	public Integer getPlatformGetsRate() {
		return platformGetsRate;
	}
	public void setPlatformGetsRate(Integer platformGetsRate){
		this.platformGetsRate=platformGetsRate;
	}

	@Column(name="platform_gets", type=DbType.Int)
	private Integer platformGets;
	public Integer getPlatformGets() {
		return platformGets;
	}
	public void setPlatformGets(Integer platformGets){
		this.platformGets=platformGets;
	}

	@Column(name="seller_gets", type=DbType.Int)
	private Integer sellerGets;
	public Integer getSellerGets() {
		return sellerGets;
	}
	public void setSellerGets(Integer sellerGets){
		this.sellerGets=sellerGets;
	}

	@Column(name="order_time", type=DbType.DateTime)
	private Long orderTime;
	public Long getOrderTime() {
		return orderTime;
	}
	public void setOrderTime(Long orderTime){
		this.orderTime=orderTime;
	}

	@Column(name="pay_time", type=DbType.DateTime)
	private Long payTime;
	public Long getPayTime() {
		return payTime;
	}
	public void setPayTime(Long payTime){
		this.payTime=payTime;
	}

	@Column(name="expire_time", type=DbType.DateTime)
	private Long expireTime;
	public Long getExpireTime() {
		return expireTime;
	}
	public void setExpireTime(Long expireTime){
		this.expireTime=expireTime;
	}

	@Column(name="litter_app_params", type=DbType.Varchar)
	private String litterAppParams;
	public String getLitterAppParams() {
		return litterAppParams;
	}
	public void setLitterAppParams(String litterAppParams){
		this.litterAppParams=litterAppParams;
	}

	@Column(name="transaction_id", type=DbType.Varchar)
	private String transactionId;
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId){
		this.transactionId=transactionId;
	}

	@Column(name="notify_body", type=DbType.Varchar)
	private String notifyBody;
	public String getNotifyBody() {
		return notifyBody;
	}
	public void setNotifyBody(String notifyBody){
		this.notifyBody=notifyBody;
	}

	@Column(name="together_id", type=DbType.Varchar)
	private String togetherId;
	public String getTogetherId() {
		return togetherId;
	}
	public void setTogetherId(String togetherId){
		this.togetherId=togetherId;
	}

	@Column(name="address", type=DbType.Varchar)
	private String address;
	public String getAddress() {
		return address;
	}
	public void setAddress(String address){
		this.address=address;
	}

	@Column(name="seller_tel_number", type=DbType.Varchar)
	private String sellerTelNumber;
	public String getSellerTelNumber() {
		return sellerTelNumber;
	}
	public void setSellerTelNumber(String sellerTelNumber){
		this.sellerTelNumber=sellerTelNumber;
	}

	@Column(name="seller_wx_number", type=DbType.Varchar)
	private String sellerWxNumber;
	public String getSellerWxNumber() {
		return sellerWxNumber;
	}
	public void setSellerWxNumber(String sellerWxNumber){
		this.sellerWxNumber=sellerWxNumber;
	}

	@Column(name="prize_level", type=DbType.Varchar)
	private String prizeLevel;
	public String getPrizeLevel() {
		return prizeLevel;
	}
	public void setPrizeLevel(String prizeLevel){
		this.prizeLevel=prizeLevel;
	}

	@Column(name="product_type", type=DbType.Int)
	private Integer productType;
	public Integer getProductType() {
		return productType;
	}
	public void setProductType(Integer productType){
		this.productType=productType;
	}
	
	
	@Column(name="memo", type=DbType.Varchar)
	private String memo;
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public static ShopOrderDDL newExample(){
		ShopOrderDDL object=new ShopOrderDDL();
		object.setId(null);
		object.setOrderId(null);
		object.setGroupId(null);
		object.setGroupName(null);
		object.setGroupImg(null);
		object.setShopId(null);
		object.setProductId(null);
		object.setProductName(null);
		object.setBuyerUserId(null);
		object.setSellerUserId(null);
		object.setStatus(null);
		object.setProductOriginAmount(null);
		object.setProductNowAmount(null);
		object.setProductTogetherAmount(null);
		object.setGroupPrice(null);
		object.setGroupTogetherPrice(null);
		object.setUseCouponAccountId(null);
		object.setUseCouponAmount(null);
		object.setUseUserAccountId(null);
		object.setUseUserAmount(null);
		object.setUseCash(null);
		object.setBuyNum(null);
		object.setPlatformGetsRate(null);
		object.setPlatformGets(null);
		object.setSellerGets(null);
		object.setOrderTime(null);
		object.setPayTime(null);
		object.setExpireTime(null);
		object.setLitterAppParams(null);
		object.setTransactionId(null);
		object.setNotifyBody(null);
		object.setTogetherId(null);
		object.setAddress(null);
		object.setSellerTelNumber(null);
		object.setSellerWxNumber(null);
		object.setPrizeLevel(null);
		object.setProductType(null);
		return object;
	}
}
