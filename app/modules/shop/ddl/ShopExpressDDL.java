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
 * @createDate 2017-12-21 15:57:51
 **/
@Table(name="shop_express")
public class ShopExpressDDL{
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

	@Column(name="order_code", type=DbType.Varchar)
	private String orderCode;
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode){
		this.orderCode=orderCode;
	}

	@Column(name="shipper_code", type=DbType.Varchar)
	private String shipperCode;
	public String getShipperCode() {
		return shipperCode;
	}
	public void setShipperCode(String shipperCode){
		this.shipperCode=shipperCode;
	}

	@Column(name="shipper_name", type=DbType.Varchar)
	private String shipperName;
	public String getShipperName() {
		return shipperName;
	}
	public void setShipperName(String shipperName){
		this.shipperName=shipperName;
	}

	@Column(name="state", type=DbType.Int)
	private Integer state;
	public Integer getState() {
		return state;
	}
	public void setState(Integer state){
		this.state=state;
	}

	@Column(name="accept_station", type=DbType.Varchar)
	private String acceptStation;
	public String getAcceptStation() {
		return acceptStation;
	}
	public void setAcceptStation(String acceptStation){
		this.acceptStation=acceptStation;
	}

	@Column(name="accept_time", type=DbType.Varchar)
	private String acceptTime;
	public String getAcceptTime() {
		return acceptTime;
	}
	public void setAcceptTime(String acceptTime){
		this.acceptTime=acceptTime;
	}

	@Column(name="traces", type=DbType.Varchar)
	private String traces;
	public String getTraces() {
		return traces;
	}
	public void setTraces(String traces){
		this.traces=traces;
	}

	public static ShopExpressDDL newExample(){
		ShopExpressDDL object=new ShopExpressDDL();
		object.setId(null);
		object.setOrderId(null);
		object.setOrderCode(null);
		object.setShipperCode(null);
		object.setShipperName(null);
		object.setState(null);
		object.setAcceptStation(null);
		object.setAcceptTime(null);
		object.setTraces(null);
		return object;
	}
}
