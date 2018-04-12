package modules.common.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;
/**
 * 
 * @author auto
 * @createDate 2018-02-06 10:23:51
 **/
@Table(name="form_ids")
public class FormIdsDDL{
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

	@Column(name="app_id", type=DbType.Varchar)
	private String appId;
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId){
		this.appId=appId;
	}

	@Column(name="open_id", type=DbType.Varchar)
	private String openId;
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId){
		this.openId=openId;
	}

	@Column(name="form_id", type=DbType.Varchar)
	private String formId;
	public String getFormId() {
		return formId;
	}
	public void setFormId(String formId){
		this.formId=formId;
	}

	@Column(name="use_status", type=DbType.Int)
	private Integer useStatus;
	public Integer getUseStatus() {
		return useStatus;
	}
	public void setUseStatus(Integer useStatus){
		this.useStatus=useStatus;
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

	public static FormIdsDDL newExample(){
		FormIdsDDL object=new FormIdsDDL();
		object.setId(null);
		object.setUserId(null);
		object.setAppId(null);
		object.setOpenId(null);
		object.setFormId(null);
		object.setUseStatus(null);
		object.setExpireTime(null);
		object.setCreateTime(null);
		return object;
	}
}
