package moudles.gae.ddl;
import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;

@Table(name="gae_draw_order")
public class GaeDrawOrderDDL{
	
	@Column(name="order_id", type=DbType.Varchar)
	private String orderId;
	
	@Column(name="order_price", type=DbType.Double)
	private Double orderPrice;
	
	@Column(name="order_fr", type=DbType.Varchar)
	private String orderFr;
	
	@Column(name="uid", type=DbType.Int)
	private Integer uid;
	
	@Column(name="ip", type=DbType.Varchar)
	private String ip;
	
	@Column(name="room_id", type=DbType.Varchar)
	private String roomId;
	
	@Column(name="draw_id", type=DbType.Varchar)
	private String drawId;
	
	@Column(name="is_lottery", type=DbType.Int)
	private Integer isLottery;
	
	@Column(name="is_worst", type=DbType.Int)
	private Integer isWorst;
	
	@Column(name="hit_time", type=DbType.DateTime)
	private Long hitTime;
	
	@Column(name="hit_beans", type=DbType.Int)
	private Integer hitBeans;
	
	@Column(name="draw_cost", type=DbType.Int)
	private Integer drawCost;
	
	@Column(name="create_time", type=DbType.DateTime)
	private Long createTime;

	@Column(name="remark", type=DbType.Varchar)
	private String remark;
	
	@Column(name="title", type=DbType.Varchar)
	private String title;
	
	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public Double getOrderPrice() {
		return orderPrice;
	}

	public void setOrderPrice(Double orderPrice) {
		this.orderPrice = orderPrice;
	}

	public String getOrderFr() {
		return orderFr;
	}

	public void setOrderFr(String orderFr) {
		this.orderFr = orderFr;
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public String getDrawId() {
		return drawId;
	}

	public void setDrawId(String drawId) {
		this.drawId = drawId;
	}

	public Integer getIsLottery() {
		return isLottery == null?Integer.parseInt("0"):isLottery;
	}

	public void setIsLottery(Integer isLottery) {
		this.isLottery = isLottery == null?Integer.parseInt("0"):isLottery;
	}

	public Integer getIsWorst() {
		return isWorst == null?Integer.parseInt("0"):isWorst;
	}

	public void setIsWorst(Integer isWorst) {
		this.isWorst = isWorst == null?Integer.parseInt("0"):isWorst;
	}

	public Long getHitTime() {
		return hitTime;
	}

	public void setHitTime(Long hitTime) {
		this.hitTime = hitTime;
	}

	public Integer getHitBeans() {
		return hitBeans == null?Integer.parseInt("0"):hitBeans;
	}

	public void setHitBeans(Integer hitBeans) {
		this.hitBeans = hitBeans == null?Integer.parseInt("0"):hitBeans;
	}

	public Integer getDrawCost() {
		return drawCost == null?Integer.parseInt("0"):drawCost;
	}

	public void setDrawCost(Integer drawCost) {
		this.drawCost = drawCost == null?Integer.parseInt("0"):drawCost;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
}
