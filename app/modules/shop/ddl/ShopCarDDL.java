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
 * @createDate 2018-05-18 14:52:24
 **/
@Table(name="shop_car")
public class ShopCarDDL{
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

	@Column(name="user_id", type=DbType.Int)
	private Integer userId;
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId){
		this.userId=userId;
	}

	@Column(name="product_id", type=DbType.Varchar)
	private String productId;
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId){
		this.productId=productId;
	}

	@Column(name="group_id", type=DbType.Varchar)
	private String groupId;
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId){
		this.groupId=groupId;
	}

	@Column(name="shop_id", type=DbType.Varchar)
	private String shopId;
	public String getShopId() {
		return shopId;
	}
	public void setShopId(String shopId){
		this.shopId=shopId;
	}

	@Column(name="shop_name", type=DbType.Varchar)
	private String shopName;
	public String getShopName() {
		return shopName;
	}
	public void setShopName(String shopName){
		this.shopName=shopName;
	}

	@Column(name="buy_num", type=DbType.Int)
	private Integer buyNum;
	public Integer getBuyNum() {
		return buyNum;
	}
	public void setBuyNum(Integer buyNum){
		this.buyNum=buyNum;
	}

	@Column(name="status", type=DbType.Int)
	private Integer status;
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status){
		this.status=status;
	}

	@Column(name="create_time", type=DbType.DateTime)
	private Long createTime;
	public Long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Long createTime){
		this.createTime=createTime;
	}

	@Column(name="update_time", type=DbType.DateTime)
	private Long updateTime;
	public Long getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Long updateTime){
		this.updateTime=updateTime;
	}

	public static ShopCarDDL newExample(){
		ShopCarDDL object=new ShopCarDDL();
		object.setId(null);
		object.setUserId(null);
		object.setProductId(null);
		object.setGroupId(null);
		object.setShopId(null);
		object.setShopName(null);
		object.setBuyNum(null);
		object.setStatus(null);
		object.setCreateTime(null);
		object.setUpdateTime(null);
		return object;
	}
}
