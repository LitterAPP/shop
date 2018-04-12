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
 * @createDate 2017-12-13 14:29:48
 **/
@Table(name="shop_product_category_rel")
public class ShopProductCategoryRelDDL{
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

	@Column(name="product_id", type=DbType.Varchar)
	private String productId;
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId){
		this.productId=productId;
	}

	@Column(name="p_category_id", type=DbType.Varchar)
	private String pCategoryId;
	public String getPCategoryId() {
		return pCategoryId;
	}
	public void setPCategoryId(String pCategoryId){
		this.pCategoryId=pCategoryId;
	}

	@Column(name="sub_category_id", type=DbType.Varchar)
	private String subCategoryId;
	public String getSubCategoryId() {
		return subCategoryId;
	}
	public void setSubCategoryId(String subCategoryId){
		this.subCategoryId=subCategoryId;
	}

	@Column(name="p_category_name", type=DbType.Varchar)
	private String pCategoryName;
	public String getPCategoryName() {
		return pCategoryName;
	}
	public void setPCategoryName(String pCategoryName){
		this.pCategoryName=pCategoryName;
	}

	@Column(name="sub_category_name", type=DbType.Varchar)
	private String subCategoryName;
	public String getSubCategoryName() {
		return subCategoryName;
	}
	public void setSubCategoryName(String subCategoryName){
		this.subCategoryName=subCategoryName;
	}

	@Column(name="order_by", type=DbType.Int)
	private Integer orderBy;
	public Integer getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(Integer orderBy){
		this.orderBy=orderBy;
	}

	public static ShopProductCategoryRelDDL newExample(){
		ShopProductCategoryRelDDL object=new ShopProductCategoryRelDDL();
		object.setId(null);
		object.setProductId(null);
		object.setPCategoryId(null);
		object.setSubCategoryId(null);
		object.setPCategoryName(null);
		object.setSubCategoryName(null);
		object.setOrderBy(null);
		return object;
	}
}
