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
 * @createDate 2018-05-14 10:53:55
 **/
@Table(name="shop_refund_order")
public class ShopRefundOrderDDL{
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
	@Column(name="shop_id", type=DbType.Varchar)
	private String shopId;
	
	public String getShopId() {
		return shopId;
	}
	public void setShopId(String shopId) {
		this.shopId = shopId;
	}
	@Column(name="transaction_id", type=DbType.Varchar)
	private String transactionId;
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId){
		this.transactionId=transactionId;
	}
	
	
	

	@Column(name="out_trade_no", type=DbType.Varchar)
	private String outTradeNo;
	public String getOutTradeNo() {
		return outTradeNo;
	}
	public void setOutTradeNo(String outTradeNo){
		this.outTradeNo=outTradeNo;
	}

	@Column(name="refund_id", type=DbType.Varchar)
	private String refundId;
	public String getRefundId() {
		return refundId;
	}
	public void setRefundId(String refundId){
		this.refundId=refundId;
	}

	@Column(name="out_refund_no", type=DbType.Varchar)
	private String outRefundNo;
	public String getOutRefundNo() {
		return outRefundNo;
	}
	public void setOutRefundNo(String outRefundNo){
		this.outRefundNo=outRefundNo;
	}

	@Column(name="total_fee", type=DbType.Int)
	private Integer totalFee;
	public Integer getTotalFee() {
		return totalFee;
	}
	public void setTotalFee(Integer totalFee){
		this.totalFee=totalFee;
	}

	@Column(name="settlement_total_fee", type=DbType.Int)
	private Integer settlementTotalFee;
	public Integer getSettlementTotalFee() {
		return settlementTotalFee;
	}
	public void setSettlementTotalFee(Integer settlementTotalFee){
		this.settlementTotalFee=settlementTotalFee;
	}

	@Column(name="refund_fee", type=DbType.Int)
	private Integer refundFee;
	public Integer getRefundFee() {
		return refundFee;
	}
	public void setRefundFee(Integer refundFee){
		this.refundFee=refundFee;
	}

	@Column(name="settlement_refund_fee", type=DbType.Int)
	private Integer settlementRefundFee;
	public Integer getSettlementRefundFee() {
		return settlementRefundFee;
	}
	public void setSettlementRefundFee(Integer settlementRefundFee){
		this.settlementRefundFee=settlementRefundFee;
	}

	@Column(name="refund_status", type=DbType.Varchar)
	private String refundStatus;
	public String getRefundStatus() {
		return refundStatus;
	}
	public void setRefundStatus(String refundStatus){
		this.refundStatus=refundStatus;
	}

	@Column(name="success_time", type=DbType.Varchar)
	private String successTime;
	public String getSuccessTime() {
		return successTime;
	}
	public void setSuccessTime(String successTime){
		this.successTime=successTime;
	}

	@Column(name="refund_recv_accout", type=DbType.Varchar)
	private String refundRecvAccout;
	public String getRefundRecvAccout() {
		return refundRecvAccout;
	}
	public void setRefundRecvAccout(String refundRecvAccout){
		this.refundRecvAccout=refundRecvAccout;
	}

	@Column(name="refund_account", type=DbType.Varchar)
	private String refundAccount;
	public String getRefundAccount() {
		return refundAccount;
	}
	public void setRefundAccount(String refundAccount){
		this.refundAccount=refundAccount;
	}

	@Column(name="refund_request_source", type=DbType.Varchar)
	private String refundRequestSource;
	public String getRefundRequestSource() {
		return refundRequestSource;
	}
	public void setRefundRequestSource(String refundRequestSource){
		this.refundRequestSource=refundRequestSource;
	}

	public static ShopRefundOrderDDL newExample(){
		ShopRefundOrderDDL object=new ShopRefundOrderDDL();
		object.setId(null);
		object.setTransactionId(null);
		object.setOutTradeNo(null);
		object.setRefundId(null);
		object.setOutRefundNo(null);
		object.setTotalFee(null);
		object.setSettlementTotalFee(null);
		object.setRefundFee(null);
		object.setSettlementRefundFee(null);
		object.setRefundStatus(null);
		object.setSuccessTime(null);
		object.setRefundRecvAccout(null);
		object.setRefundAccount(null);
		object.setRefundRequestSource(null);
		return object;
	}
}
