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
 * @createDate 2018-04-24 11:49:04
 **/
@Table(name="shop_index")
public class ShopIndexDDL{
	@Id
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
	public void setShopId(String shopId){
		this.shopId=shopId;
	}

	@Column(name="name", type=DbType.Varchar)
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name){
		this.name=name;
	}

	@Column(name="avatar", type=DbType.Varchar)
	private String avatar;
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar){
		this.avatar=avatar;
	}

	@Column(name="follow", type=DbType.Int)
	private Integer follow;
	public Integer getFollow() {
		return follow;
	}
	public void setFollow(Integer follow){
		this.follow=follow;
	}

	@Column(name="config", type=DbType.Varchar)
	private String config;
	public String getConfig() {
		return config;
	}
	public void setConfig(String config){
		this.config=config;
	}

	@Column(name="create_time", type=DbType.DateTime)
	private Long createTime;
	public Long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Long createTime){
		this.createTime=createTime;
	}

	public static ShopIndexDDL newExample(){
		ShopIndexDDL object=new ShopIndexDDL();
		object.setId(null);
		object.setShopId(null);
		object.setName(null);
		object.setAvatar(null);
		object.setFollow(null);
		object.setConfig(null);
		object.setCreateTime(null);
		return object;
	}
}
