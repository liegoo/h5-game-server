package moudles.award.ddl;

import net.sf.oval.guard.Guarded;
import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;

@Table(name = "award_detail")
public class AwardDetailDDL {
	@Id
	@GeneratedValue(generationType = GenerationType.Auto)
	@Column(name = "id", type = DbType.Int)
	private Integer id;

	/**
	 * 奖品id
	 */
	@Column(name = "award_assign_id", type = DbType.Int)
	private Integer awardAssignId;

	/**
	 * 中奖时间
	 */
	@Column(name = "hit_time", type = DbType.DateTime)
	private Long hitTime;

	/**
	 * 订单ID
	 */
	@Column(name = "order_id", type = DbType.Varchar)
	private String orderId;

	@Column(name = "uid", type = DbType.Int)
	private Integer uid;

	/**
	 * 状态 1-未中奖 2-已中奖 3-已废弃
	 */
	@Column(name = "status", type = DbType.Int)
	private Integer status;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getAwardAssignId() {
		return awardAssignId;
	}

	public void setAwardAssignId(Integer awardAssignId) {
		this.awardAssignId = awardAssignId;
	}

	public Long getHitTime() {
		return hitTime;
	}

	public void setHitTime(Long hitTime) {
		this.hitTime = hitTime;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}
