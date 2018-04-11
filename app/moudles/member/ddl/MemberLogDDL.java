package moudles.member.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;

/**
 * 
 * @author auto
 * @createDate 2016-07-27 18:14:20
 **/
@Table(name = "member_log")
public class MemberLogDDL {
	@Id
	@GeneratedValue(generationType = GenerationType.Auto)
	@Column(name = "id", type = DbType.Int)
	private Integer id;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
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

	@Column(name = "suid", type = DbType.Int)
	private Integer suid = 0;

	public Integer getSuid() {
		return suid == null ? Integer.parseInt("0") : suid;
	}

	public void setSuid(Integer suid) {
		this.suid = suid == null ? Integer.parseInt("0") : suid;
	}

	@Column(name = "cp_id", type = DbType.Int)
	private Integer cpId = 0;

	public Integer getCpId() {
		return cpId == null ? Integer.parseInt("0") : cpId;
	}

	public void setCpId(Integer cpId) {
		this.cpId = cpId == null ? Integer.parseInt("0") : cpId;
	}

	@Column(name = "game_id", type = DbType.Int)
	private Integer gameId = 0;

	public Integer getGameId() {
		return gameId == null ? Integer.parseInt("0") : gameId;
	}

	public void setGameId(Integer gameId) {
		this.gameId = gameId == null ? Integer.parseInt("0") : gameId;
	}

	@Column(name = "user_ip", type = DbType.Varchar)
	private String userIp = "";

	public String getUserIp() {
		return userIp == null ? String.valueOf("") : userIp;
	}

	public void setUserIp(String userIp) {
		this.userIp = userIp == null ? String.valueOf("") : userIp;
	}

	@Column(name = "user_agent", type = DbType.Varchar)
	private String userAgent = "";

	public String getUserAgent() {
		return userAgent == null ? String.valueOf("") : userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent == null ? String.valueOf("") : userAgent;
	}

	@Column(name = "ch", type = DbType.Varchar)
	private String ch = "";

	public String getCh() {
		return ch == null ? String.valueOf("") : ch;
	}

	public void setCh(String ch) {
		this.ch = ch == null ? String.valueOf("") : ch;
	}

	@Column(name = "happy_bean", type = DbType.Int)
	private Integer happyBean = 0;

	public Integer getHappyBean() {
		return happyBean == null ? Integer.parseInt("0") : happyBean;
	}

	public void setHappyBean(Integer happyBean) {
		this.happyBean = happyBean == null ? Integer.parseInt("0") : happyBean;
	}

	@Column(name = "rpa_id", type = DbType.Int)
	private Integer rpaId = 0;

	public Integer getRpaId() {
		return rpaId == null ? Integer.parseInt("0") : rpaId;
	}

	public void setRpaId(Integer rpaId) {
		this.rpaId = rpaId == null ? Integer.parseInt("0") : rpaId;
	}

	@Column(name = "op_type", type = DbType.Int)
	private Integer opType = 0;

	public Integer getOpType() {
		return opType == null ? Integer.parseInt("0") : opType;
	}

	public void setOpType(Integer opType) {
		this.opType = opType == null ? Integer.parseInt("0") : opType;
	}

	@Column(name = "op_result", type = DbType.Int)
	private Integer opResult = 99;

	public Integer getOpResult() {
		return opResult == null ? Integer.parseInt("99") : opResult;
	}

	public void setOpResult(Integer opResult) {
		this.opResult = opResult == null ? Integer.parseInt("99") : opResult;
	}

	@Column(name = "remark", type = DbType.Varchar)
	private String remark = "";

	public String getRemark() {
		return remark == null ? String.valueOf("") : remark;
	}

	public void setRemark(String remark) {
		this.remark = remark == null ? String.valueOf("") : remark;
	}

	@Column(name = "op_time", type = DbType.DateTime)
	private Long opTime;

	public Long getOpTime() {
		return opTime;
	}

	public void setOpTime(Long opTime) {
		this.opTime = opTime;
	}

	/**
	 * 每次操作后的余额
	 */
	@Column(name = "balance", type = DbType.Int)
	private int balance;

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	@Column(name = "bill_id", type = DbType.Varchar)
	private String billId;

	public String getBillId() {
		return billId;
	}

	public void setBillId(String billId) {
		this.billId = billId;
	}

	public static MemberLogDDL newExample() {
		MemberLogDDL object = new MemberLogDDL();
		object.setId(null);
		object.setUid(null);
		object.setSuid(null);
		object.setCpId(null);
		object.setGameId(null);
		object.setUserIp(null);
		object.setUserAgent(null);
		object.setCh(null);
		object.setHappyBean(null);
		object.setRpaId(null);
		object.setOpType(null);
		object.setOpResult(null);
		object.setRemark(null);
		object.setOpTime(null);
		return object;
	}
}
