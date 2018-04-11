package moudles.award.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;

/**
 * 
 * @author auto
 * @createDate 2016-07-27 18:14:45
 **/
@Table(name = "award_record")
public class AwardRecordDDL {
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

	@Column(name = "award_id", type = DbType.Int)
	private Integer awardId;

	public Integer getAwardId() {
		return awardId;
	}

	public void setAwardId(Integer awardId) {
		this.awardId = awardId;
	}

	@Column(name = "zhifu_order_id", type = DbType.Varchar)
	private String zhifuOrderId;

	public String getZhifuOrderId() {
		return zhifuOrderId;
	}

	public void setZhifuOrderId(String zhifuOrderId) {
		this.zhifuOrderId = zhifuOrderId;
	}

	@Column(name = "source_desc", type = DbType.Varchar)
	private String sourceDesc = "";

	public String getSourceDesc() {
		return sourceDesc == null ? String.valueOf("") : sourceDesc;
	}

	public void setSourceDesc(String sourceDesc) {
		this.sourceDesc = sourceDesc == null ? String.valueOf("") : sourceDesc;
	}

	@Column(name = "source_type", type = DbType.Int)
	private Integer sourceType;

	public Integer getSourceType() {
		return sourceType;
	}

	public void setSourceType(Integer sourceType) {
		this.sourceType = sourceType;
	}

	@Column(name = "uid", type = DbType.Int)
	private Integer uid;

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	@Column(name = "user_name", type = DbType.Varchar)
	private String userName = "";

	public String getUserName() {
		return userName == null ? String.valueOf("") : userName;
	}

	public void setUserName(String userName) {
		this.userName = userName == null ? String.valueOf("") : userName;
	}

	@Column(name = "addr", type = DbType.Varchar)
	private String addr = "";

	public String getAddr() {
		return addr == null ? String.valueOf("") : addr;
	}

	public void setAddr(String addr) {
		this.addr = addr == null ? String.valueOf("") : addr;
	}

	@Column(name = "mobile", type = DbType.Varchar)
	private String mobile = "";

	public String getMobile() {
		return mobile == null ? String.valueOf("") : mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile == null ? String.valueOf("") : mobile;
	}

	@Column(name = "QQ", type = DbType.Varchar)
	private String QQ = "";

	public String getQQ() {
		return QQ == null ? String.valueOf("") : QQ;
	}

	public void setQQ(String QQ) {
		this.QQ = QQ == null ? String.valueOf("") : QQ;
	}

	@Column(name = "game_id", type = DbType.Varchar)
	private String gameId = "";

	public String getGameId() {
		return gameId == null ? String.valueOf("") : gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId == null ? String.valueOf("") : gameId;
	}

	@Column(name = "game_name", type = DbType.Varchar)
	private String gameName = "";

	public String getGameName() {
		return gameName == null ? String.valueOf("") : gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName == null ? String.valueOf("") : gameName;
	}

	@Column(name = "game_uid", type = DbType.Varchar)
	private String gameUid = "";

	public String getGameUid() {
		return gameUid == null ? String.valueOf("") : gameUid;
	}

	public void setGameUid(String gameUid) {
		this.gameUid = gameUid == null ? String.valueOf("") : gameUid;
	}

	@Column(name = "audit_status", type = DbType.Int)
	private Integer auditStatus = 10;

	public Integer getAuditStatus() {
		return auditStatus == null ? Integer.parseInt("10") : auditStatus;
	}

	public void setAuditStatus(Integer auditStatus) {
		this.auditStatus = auditStatus == null ? Integer.parseInt("10") : auditStatus;
	}

	@Column(name = "audit_remark", type = DbType.Varchar)
	private String auditRemark = "";

	public String getAuditRemark() {
		return auditRemark == null ? String.valueOf("") : auditRemark;
	}

	public void setAuditRemark(String auditRemark) {
		this.auditRemark = auditRemark == null ? String.valueOf("") : auditRemark;
	}

	@Column(name = "remark", type = DbType.Varchar)
	private String remark = "";

	public String getRemark() {
		return remark == null ? String.valueOf("") : remark;
	}

	public void setRemark(String remark) {
		this.remark = remark == null ? String.valueOf("") : remark;
	}

	@Column(name = "op_name", type = DbType.Varchar)
	private String opName;

	public String getOpName() {
		return opName;
	}

	public void setOpName(String opName) {
		this.opName = opName;
	}

	@Column(name = "create_time", type = DbType.DateTime)
	private Long createTime;

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	@Column(name = "update_time", type = DbType.DateTime)
	private Long updateTime;

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

	// 快递公司
	@Column(name = "deliver_company", type = DbType.Varchar)
	private String deliverCompany;

	public String getDeliverCompany() {
		return deliverCompany;
	}

	public void setDeliverCompany(String deliverCompany) {
		this.deliverCompany = deliverCompany;
	}

	// 快递单号
	@Column(name = "deliver_no", type = DbType.Varchar)
	private String deliverNo;

	public String getDeliverNo() {
		return deliverNo;
	}

	public void setDeliverNo(String deliverNo) {
		this.deliverNo = deliverNo;
	}
	
	@Column(name = "base_coupon_id", type = DbType.Varchar)
	private String baseCouponId;
	
	public String getBaseCouponId() {
		return baseCouponId;
	}
	
	public void setBaseCouponId(String baseCouponId) {
		this.baseCouponId = baseCouponId;
	}

	public static AwardRecordDDL newExample() {
		AwardRecordDDL object = new AwardRecordDDL();
		object.setId(null);
		object.setAwardId(null);
		object.setZhifuOrderId(null);
		object.setSourceDesc(null);
		object.setUid(null);
		object.setUserName(null);
		object.setAddr(null);
		object.setMobile(null);
		object.setQQ(null);
		object.setGameId(null);
		object.setGameName(null);
		object.setGameUid(null);
		object.setAuditStatus(null);
		object.setRemark(null);
		object.setOpName(null);
		object.setCreateTime(null);
		object.setUpdateTime(null);
		object.setDeliverCompany(null);
		object.setDeliverNo(null);
		object.setSourceType(null);
		return object;
	}
}
