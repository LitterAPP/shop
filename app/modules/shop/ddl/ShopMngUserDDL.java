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
 * @createDate 2018-04-03 10:22:12
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
		object.setUserName(null);
		object.setPassword(null);
		object.setCreateTime(null);
		return object;
	}
}
