package moudles.member.ddl;

import java.io.Serializable;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;

/**
 * 
 * @author auto
 * @createDate 2016-07-27 18:14:14
 **/
@Table(name = "member")
public class MemberDDL implements Serializable {
	@Id
	@GeneratedValue(generationType = GenerationType.Auto)
	@Column(name = "id", type = DbType.BigInt)
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "uid", type = DbType.Int)
	private Integer uid;

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	@Column(name = "avatar", type = DbType.Varchar)
	private String avatar;
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	@Column(name = "account", type = DbType.Varchar)
	private String account = "";

	public String getAccount() {
		return account == null ? String.valueOf("") : account;
	}

	public void setAccount(String account) {
		this.account = account == null ? String.valueOf("") : account;
	}

	@Column(name = "happy_bean", type = DbType.Int)
	private Integer happyBean = 0;

	public Integer getHappyBean() {
		return happyBean == null ? Integer.parseInt("0") : happyBean;
	}

	public void setHappyBean(Integer happyBean) {
		this.happyBean = happyBean == null ? Integer.parseInt("0") : happyBean;
	}
	
	@Column(name = "happy_bean_from_op", type = DbType.Int)
	private Integer happyBeanFromOp = 0;

	public Integer getHappyBeanFromOp() {
		return happyBeanFromOp;
	}

	public void setHappyBeanFromOp(Integer happyBeanFromOp) {
		this.happyBeanFromOp = happyBeanFromOp == null ? Integer.parseInt("0") : happyBeanFromOp;
	}

	@Column(name = "password", type = DbType.Varchar)
	private String password = "";

	public String getPassword() {
		return password == null ? String.valueOf("") : password;
	}

	public void setPassword(String password) {
		this.password = password == null ? String.valueOf("") : password;
	}

	@Column(name = "nick_name", type = DbType.Varchar)
	private String nickName = "";

	public String getNickName() {
		return nickName == null ? String.valueOf("") : nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName == null ? String.valueOf("") : nickName;
	}

	@Column(name = "mobile", type = DbType.Varchar)
	private String mobile = "";

	public String getMobile() {
		return mobile == null ? String.valueOf("") : mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile == null ? String.valueOf("") : mobile;
	}

	@Column(name = "status", type = DbType.Int)
	private Integer status = 1;

	public Integer getStatus() {
		return status == null ? Integer.parseInt("1") : status;
	}

	public void setStatus(Integer status) {
		this.status = status == null ? Integer.parseInt("1") : status;
	}

	@Column(name = "ch", type = DbType.Varchar)
	private String ch = "";

	public String getCh() {
		return ch == null ? String.valueOf("") : ch;
	}

	public void setCh(String ch) {
		this.ch = ch == null ? String.valueOf("") : ch;
	}

	@Column(name = "continue_check_in", type = DbType.Int)
	private Integer continueCheckIn = 0;

	public Integer getContinueCheckIn() {
		return continueCheckIn == null ? Integer.parseInt("0") : continueCheckIn;
	}

	public void setContinueCheckIn(Integer continueCheckIn) {
		this.continueCheckIn = continueCheckIn == null ? Integer.parseInt("0") : continueCheckIn;
	}

	@Column(name = "create_time", type = DbType.DateTime)
	private Long createTime;

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	@Column(name = "last_login_time", type = DbType.DateTime)
	private Long lastLoginTime;

	public Long getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Long lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	@Column(name = "last_check_in_time", type = DbType.DateTime)
	private Long lastCheckInTime;

	public Long getLastCheckInTime() {
		return lastCheckInTime;
	}

	public void setLastCheckInTime(Long lastCheckInTime) {
		this.lastCheckInTime = lastCheckInTime;
	}

	public static MemberDDL newExample() {
		MemberDDL object = new MemberDDL();
		object.setId(null);
		object.setUid(null);
		object.setAccount(null);
		object.setHappyBean(null);
		object.setPassword(null);
		object.setNickName(null);
		object.setMobile(null);
		object.setStatus(null);
		object.setCh(null);
		object.setContinueCheckIn(null);
		object.setCreateTime(null);
		object.setLastLoginTime(null);
		object.setLastCheckInTime(null);
		return object;
	}
}
