package moudles.award.model;

import java.util.Date;
import java.util.List;

public class Coupon{
	
	private int id;
	private String couponId; //代金券ID 1-》group_type_id（商品组ID）
	private String uid;      //代金券所属用户  1 seller
	private int sourceType;  //来源类型 1=夹娃娃 2=限量领奖 3=购买
	private String sourceId;  //来源标示
	private int type;    //类型（1=通用 2=非通用，只针对某款游戏）
	private int status;  //状态INIT(1,"初始状态"),TO_AUDIT(2,"待审核"),AUDIT_FAIL(6,"审核失败"),APPROVED(5,"审核通过"),ON_SALE(3,"在售"),ACTED(4,"已激活");
	private Date expTime;   //有效期 1
	private int gameId;   //游戏ID 1 有可能通用券为0
	private String gameName;   //游戏名称 1 有可能通用券，空字符
	private int denomination;   //代金券面额（单位：分）
	private int tranferLimit;   //转售次数限制
	private Date activateTime;   //激活时间
	private String activateAccount;   //激活账号
	private Date createTime;   //创建时间
	private Date updateTime;    //更新时间
	private Date sdkCouponExpTime; //激活后代金券有效期
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCouponId() {
		return couponId;
	}

	public void setCouponId(String couponId) {
		this.couponId = couponId;
	}

	public int getSourceType() {
		return sourceType;
	}

	public void setSourceType(int sourceType) {
		this.sourceType = sourceType;
	}

	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getExpTime() {
		return expTime;
	}

	public void setExpTime(Date expTime) {
		this.expTime = expTime;
	}

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public int getDenomination() {
		return denomination;
	}

	public void setDenomination(int denomination) {
		this.denomination = denomination;
	}

	public int getTranferLimit() {
		return tranferLimit;
	}

	public void setTranferLimit(int tranferLimit) {
		this.tranferLimit = tranferLimit;
	}

	public Date getActivateTime() {
		return activateTime;
	}

	public void setActivateTime(Date activateTime) {
		this.activateTime = activateTime;
	}

	public String getActivateAccount() {
		return activateAccount;
	}

	public void setActivateAccount(String activateAccount) {
		this.activateAccount = activateAccount;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Date getSdkCouponExpTime() {
		return sdkCouponExpTime;
	}

	public void setSdkCouponExpTime(Date sdkCouponExpTime) {
		this.sdkCouponExpTime = sdkCouponExpTime;
	}
}
