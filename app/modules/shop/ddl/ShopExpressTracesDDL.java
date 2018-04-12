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
 * @createDate 2017-12-21 15:55:28
 **/
@Table(name="shop_express_traces")
public class ShopExpressTracesDDL{
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

	@Column(name="shop_express_id", type=DbType.Int)
	private Integer shopExpressId;
	public Integer getShopExpressId() {
		return shopExpressId;
	}
	public void setShopExpressId(Integer shopExpressId){
		this.shopExpressId=shopExpressId;
	}

	@Column(name="traces", type=DbType.Varchar)
	private String traces;
	public String getTraces() {
		return traces;
	}
	public void setTraces(String traces){
		this.traces=traces;
	}

	public static ShopExpressTracesDDL newExample(){
		ShopExpressTracesDDL object=new ShopExpressTracesDDL();
		object.setId(null);
		object.setShopExpressId(null);
		object.setTraces(null);
		return object;
	}
}
