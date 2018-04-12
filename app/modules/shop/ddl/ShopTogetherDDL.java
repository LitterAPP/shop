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
 * @createDate 2017-12-26 17:13:58
 **/
@Table(name="shop_together")
public class ShopTogetherDDL{
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

	@Column(name="together_id", type=DbType.Varchar)
	private String togetherId;
	public String getTogetherId() {
		return togetherId;
	}
	public void setTogetherId(String togetherId){
		this.togetherId=togetherId;
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

	@Column(name="together_number", type=DbType.Int)
	private Integer togetherNumber;
	public Integer getTogetherNumber() {
		return togetherNumber;
	}
	public void setTogetherNumber(Integer togetherNumber){
		this.togetherNumber=togetherNumber;
	}

	@Column(name="together_number_residue", type=DbType.Int)
	private Integer togetherNumberResidue;
	public Integer getTogetherNumberResidue() {
		return togetherNumberResidue;
	}
	public void setTogetherNumberResidue(Integer togetherNumberResidue){
		this.togetherNumberResidue=togetherNumberResidue;
	}

	@Column(name="expire_time", type=DbType.DateTime)
	private Long expireTime;
	public Long getExpireTime() {
		return expireTime;
	}
	public void setExpireTime(Long expireTime){
		this.expireTime=expireTime;
	}

	@Column(name="status", type=DbType.Int)
	private Integer status;
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status){
		this.status=status;
	}

	@Column(name="master_avatar", type=DbType.Varchar)
	private String masterAvatar;
	public String getMasterAvatar() {
		return masterAvatar;
	}
	public void setMasterAvatar(String masterAvatar){
		this.masterAvatar=masterAvatar;
	}

	@Column(name="master_name", type=DbType.Varchar)
	private String masterName;
	public String getMasterName() {
		return masterName;
	}
	public void setMasterName(String masterName){
		this.masterName=masterName;
	}

	@Column(name="product_type", type=DbType.Int)
	private Integer productType;
	public Integer getProductType() {
		return productType;
	}
	public void setProductType(Integer productType){
		this.productType=productType;
	}

	@Column(name="create_time", type=DbType.DateTime)
	private Long createTime;
	public Long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Long createTime){
		this.createTime=createTime;
	}

	public static ShopTogetherDDL newExample(){
		ShopTogetherDDL object=new ShopTogetherDDL();
		object.setId(null);
		object.setTogetherId(null);
		object.setProductId(null);
		object.setProductName(null);
		object.setTogetherNumber(null);
		object.setTogetherNumberResidue(null);
		object.setExpireTime(null);
		object.setStatus(null);
		object.setMasterAvatar(null);
		object.setMasterName(null);
		object.setProductType(null);
		object.setCreateTime(null);
		return object;
	}
}
