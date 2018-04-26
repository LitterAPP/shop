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
 * @createDate 2018-04-26 14:51:58
 **/
@Table(name="shop_mng_user")
public class ShopMngUserDDL{
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

	@Column(name="mobile", type=DbType.Varchar)
	private String mobile;
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile){
		this.mobile=mobile;
	}

	@Column(name="user_name", type=DbType.Varchar)
	private String userName;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName){
		this.userName=userName;
	}

	@Column(name="password", type=DbType.Varchar)
	private String password;
	public String getPassword() {
		return password;
	}
	public void setPassword(String password){
		this.password=password;
	}

	@Column(name="shop_id", type=DbType.Varchar)
	private String shopId;
	public String getShopId() {
		return shopId;
	}
	public void setShopId(String shopId){
		this.shopId=shopId;
	} 

	@Column(name="create_time", type=DbType.DateTime)
	private Long createTime;
	public Long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Long createTime){
		this.createTime=createTime;
	}

	public static ShopMngUserDDL newExample(){
		ShopMngUserDDL object=new ShopMngUserDDL();
		object.setId(null);
		object.setMobile(null);
		object.setUserName(null);
		object.setPassword(null);
		object.setShopId(null); 
		object.setCreateTime(null);
		return object;
	}
}
