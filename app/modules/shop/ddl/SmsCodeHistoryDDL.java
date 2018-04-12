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
 * @createDate 2017-11-10 16:07:02
 **/
@Table(name="sms_code_history")
public class SmsCodeHistoryDDL{
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
	
	@Column(name="status", type=DbType.Int)
	private Integer status;
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name="mobile", type=DbType.Varchar)
	private String mobile;
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile){
		this.mobile=mobile;
	}

	@Column(name="code", type=DbType.Int)
	private Integer code;
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code){
		this.code=code;
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

	public static SmsCodeHistoryDDL newExample(){
		SmsCodeHistoryDDL object=new SmsCodeHistoryDDL();
		object.setId(null);
		object.setMobile(null);
		object.setCode(null);
		object.setExpireTime(null);
		object.setCreateTime(null);
		return object;
	}
}
