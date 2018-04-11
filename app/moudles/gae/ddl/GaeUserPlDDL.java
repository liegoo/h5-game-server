package moudles.gae.ddl;

import java.util.Date;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;
/**
 * 
 * @author auto
 * @createDate 2016-11-15 15:58:14
 **/
@Table(name="gae_user_pl")
public class GaeUserPlDDL{
	
	@Id
	@Column(name="id", type=DbType.Int)
	private Integer id;
	
	@Column(name="user_id", type=DbType.Int)
	private Integer userId;
	
	@Column(name="room_id", type=DbType.Varchar)
	private String roomId;
	
	@Column(name="sys_profit", type=DbType.Int)
	private Integer sysProfit;  
	
	@Column(name="profit", type=DbType.Int)
	private Integer profit;  
	
	@Column(name="loss", type=DbType.Int)
	private Integer loss;
	
	@Column(name="total", type=DbType.Int)
	private Integer total;     
	
	@Column(name="create_time", type=DbType.DateTime)
	private Long createTime;

	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public Integer getSysProfit() {
		return sysProfit;
	}

	public void setSysProfit(Integer sysProfit) {
		this.sysProfit = sysProfit;
	}

	public Integer getProfit() {
		return profit;
	}

	public void setProfit(Integer profit) {
		this.profit = profit;
	}

	public Integer getLoss() {
		return loss;
	}

	public void setLoss(Integer loss) {
		this.loss = loss;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}
	
}
