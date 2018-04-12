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
 * @createDate 2017-12-11 12:42:11
 **/
@Table(name="user_account")
public class UserAccountDDL{
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

	@Column(name="account_id", type=DbType.Varchar)
	private String accountId;
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId){
		this.accountId=accountId;
	}

	@Column(name="account_name", type=DbType.Varchar)
	private String accountName;
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName){
		this.accountName=accountName;
	}

	@Column(name="user_id", type=DbType.Int)
	private Integer userId;
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId){
		this.userId=userId;
	}

	@Column(name="account_type", type=DbType.Int)
	private Integer accountType;
	public Integer getAccountType() {
		return accountType;
	}
	public void setAccountType(Integer accountType){
		this.accountType=accountType;
	}

	@Column(name="amount", type=DbType.Int)
	private Integer amount;
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount){
		this.amount=amount;
	}

	@Column(name="limit_rule", type=DbType.Varchar)
	private String limitRule;
	public String getLimitRule() {
		return limitRule;
	}
	public void setLimitRule(String limitRule){
		this.limitRule=limitRule;
	}

	@Column(name="expire_time", type=DbType.DateTime)
	private Long expireTime;
	public Long getExpireTime() {
		return expireTime;
	}
	public void setExpireTime(Long expireTime){
		this.expireTime=expireTime;
	}

	@Column(name="create_time", type=DbType.DateTime)
	private Long createTime;
	public Long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Long createTime){
		this.createTime=createTime;
	}

	public static UserAccountDDL newExample(){
		UserAccountDDL object=new UserAccountDDL();
		object.setId(null);
		object.setAccountId(null);
		object.setAccountName(null);
		object.setUserId(null);
		object.setAccountType(null);
		object.setAmount(null);
		object.setLimitRule(null);
		object.setExpireTime(null);
		object.setCreateTime(null);
		return object;
	}
}
