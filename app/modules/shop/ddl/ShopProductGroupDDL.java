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
 * @createDate 2017-12-14 11:07:24
 **/
@Table(name="shop_product_group")
public class ShopProductGroupDDL{
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

	@Column(name="group_id", type=DbType.Varchar)
	private String groupId;
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId){
		this.groupId=groupId;
	}

	@Column(name="product_id", type=DbType.Varchar)
	private String productId;
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId){
		this.productId=productId;
	}

	@Column(name="group_name", type=DbType.Varchar)
	private String groupName;
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName){
		this.groupName=groupName;
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

	@Column(name="group_image", type=DbType.Varchar)
	private String groupImage;
	public String getGroupImage() {
		return groupImage;
	}
	public void setGroupImage(String groupImage){
		this.groupImage=groupImage;
	}

	@Column(name="order_by", type=DbType.Int)
	private Integer orderBy;
	public Integer getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(Integer orderBy){
		this.orderBy=orderBy;
	}

	public static ShopProductGroupDDL newExample(){
		ShopProductGroupDDL object=new ShopProductGroupDDL();
		object.setId(null);
		object.setGroupId(null);
		object.setProductId(null);
		object.setGroupName(null);
		object.setGroupPrice(null);
		object.setGroupTogetherPrice(null);
		object.setGroupImage(null);
		object.setOrderBy(null);
		return object;
	}
}
