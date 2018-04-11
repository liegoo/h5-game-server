package moudles.chance.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;

@Table(name = "doll_chance_record")
public class DollChanceRecordDDL {

	@Id
	@GeneratedValue(generationType = GenerationType.Auto)
	@Column(name = "id", type = DbType.Int)
	private int id;

	@Column(name = "uid", type = DbType.Int)
	private int uid;

	/**
	 * 赠送机会次数
	 */
	@Column(name = "chance", type = DbType.Int)
	private int chance;

	/**
	 * 赠送类型,1-新用户赠送,2-参与游戏赠送,3-充值赠送,4-其他赠送
	 */
	@Column(name = "type", type = DbType.Int)
	private int type;

	/**
	 * 赠送机会剩余次数
	 */
	@Column(name = "remain", type = DbType.Int)
	private int remain;

	@Column(name = "remark", type = DbType.Varchar)
	private String remark;

	@Column(name = "title", type = DbType.Varchar)
	private String title;

	@Column(name = "channel", type = DbType.Varchar)
	private String channel;

	@Column(name = "update_time", type = DbType.DateTime)
	private long updateTime;

	@Column(name = "create_time", type = DbType.DateTime)
	private long createTime;

	@Column(name = "expire", type = DbType.DateTime)
	private Long expire;

	@Column(name = "zhifu_order_id", type = DbType.Varchar)
	private String zhifuOrderId;

	/**
	 * 可用状态，1-可用，2-不可用
	 */
	@Column(name = "status", type = DbType.Int)
	private int status;

	/**
	 * 阅读状态 0-未读， 1-已读
	 */
	@Column(name = "is_read", type = DbType.Int)
	private int isRead;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public int getChance() {
		return chance;
	}

	public void setChance(int chance) {
		this.chance = chance;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	public String getZhifuOrderId() {
		return zhifuOrderId;
	}

	public void setZhifuOrderId(String zhifuOrderId) {
		this.zhifuOrderId = zhifuOrderId;
	}

	public int getRemain() {
		return remain;
	}

	public void setRemain(int remain) {
		this.remain = remain;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getIsRead() {
		return isRead;
	}

	public void setIsRead(int isRead) {
		this.isRead = isRead;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Long getExpire() {
		return expire;
	}

	public void setExpire(Long expire) {
		this.expire = expire;
	}

}
