package moudles.award.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;

/**
 * 夹娃娃记录
 * 
 * @author Coming
 */
@Table(name = "award_record_doll")
public class AwardRecordDollDDL {

	@Id
	@GeneratedValue(generationType = GenerationType.Auto)
	@Column(name = "id", type = DbType.Int)
	private Integer id;

	@Column(name = "award_id", type = DbType.Int)
	private Integer awardId;

	@Column(name = "zhifu_order_id", type = DbType.Varchar)
	private String zhifuOrderId;

	@Column(name = "user_name", type = DbType.Varchar)
	private String userName;

	@Column(name = "uid", type = DbType.Varchar)
	private String uid;

	@Column(name = "game_level", type = DbType.Int)
	private int gameLevel;

	@Column(name = "gain", type = DbType.Float)
	private float gain;

	@Column(name = "happy_bean", type = DbType.Int)
	private Integer happyBean;

	@Column(name = "addr", type = DbType.Varchar)
	private String addr;

	@Column(name = "qq", type = DbType.Varchar)
	private String qq;

	@Column(name = "mobile", type = DbType.Varchar)
	private String mobile;

	@Column(name = "game_id", type = DbType.Varchar)
	private String gameId;

	@Column(name = "game_uid", type = DbType.Varchar)
	private String gameUid;

	@Column(name = "game_name", type = DbType.Varchar)
	private String gameName;

	// 审核状态 10待审核 20待发货 30已发货 40审核不通过 50领奖信息不完善
	@Column(name = "audit_status", type = DbType.Int)
	private Integer auditStatus;

	// 京东卡号
	@Column(name = "card_no", type = DbType.Varchar)
	private String cardNo;

	// 京东卡密码
	@Column(name = "card_pwd", type = DbType.Varchar)
	private String cardPwd;

	// 快递公司
	@Column(name = "deliver_company", type = DbType.Varchar)
	private String deliverCompany;

	// 快递单号
	@Column(name = "deliver_no", type = DbType.Varchar)
	private String deliverNo;

	@Column(name = "audit_remark", type = DbType.Varchar)
	private String auditRemark;

	@Column(name = "op_name", type = DbType.Varchar)
	private String opName;

	@Column(name = "update_time", type = DbType.DateTime)
	private Long updateTime;

	// 是否中奖 1-已中奖 2-未中奖
	@Column(name = "hit", type = DbType.Int)
	private Integer hit;

	@Column(name = "create_time", type = DbType.DateTime)
	private Long createTime;

	@Column(name = "visible", type = DbType.Int)
	private Integer visible;

	// 领奖状态 1-未领取 2-已领取

	@Column(name = "status", type = DbType.Int)
	private Integer status;

	// 是否可兑换 ，1-可兑换 ， 2-不可兑换
	@Column(name = "exchange", type = DbType.Int)
	private int exchange;

	@Column(name = "base_coupon_id", type = DbType.Varchar)
	private String baseCouponId;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getZhifuOrderId() {
		return zhifuOrderId;
	}

	public void setZhifuOrderId(String zhifuOrderId) {
		this.zhifuOrderId = zhifuOrderId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Integer getHappyBean() {
		return happyBean;
	}

	public void setHappyBean(Integer happyBean) {
		this.happyBean = happyBean;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public String getGameUid() {
		return gameUid;
	}

	public void setGameUid(String gameUid) {
		this.gameUid = gameUid;
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public Integer getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(Integer auditStatus) {
		this.auditStatus = auditStatus;
	}

	public String getAuditRemark() {
		return auditRemark;
	}

	public void setAuditRemark(String auditRemark) {
		this.auditRemark = auditRemark;
	}

	public String getOpName() {
		return opName;
	}

	public void setOpName(String opName) {
		this.opName = opName;
	}

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getHit() {
		return hit;
	}

	public void setHit(Integer hit) {
		this.hit = hit;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Integer getVisible() {
		return visible;
	}

	public void setVisible(Integer visible) {
		this.visible = visible;
	}

	public Integer getAwardId() {
		return awardId;
	}

	public void setAwardId(Integer awardId) {
		this.awardId = awardId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getDeliverCompany() {
		return deliverCompany;
	}

	public void setDeliverCompany(String deliverCompany) {
		this.deliverCompany = deliverCompany;
	}

	public String getDeliverNo() {
		return deliverNo;
	}

	public void setDeliverNo(String deliverNo) {
		this.deliverNo = deliverNo;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getCardPwd() {
		return cardPwd;
	}

	public void setCardPwd(String cardPwd) {
		this.cardPwd = cardPwd;
	}

	public float getGain() {
		return gain;
	}

	public void setGain(float gain) {
		this.gain = gain;
	}

	public int getExchange() {
		return exchange;
	}

	public void setExchange(int exchange) {
		this.exchange = exchange;
	}

	public int getGameLevel() {
		return gameLevel;
	}

	public void setGameLevel(int gameLevel) {
		this.gameLevel = gameLevel;
	}

	public String getBaseCouponId() {
		return baseCouponId;
	}
	
	public void setBaseCouponId(String baseCouponId) {
		this.baseCouponId = baseCouponId;
	}
	
}
