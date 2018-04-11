package moudles.gae.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;

/**
 * 真麻烦
 * 
 * @author caixb
 *
 */
@Table(name="gae_robot_active")
public class GaeRobotActiveDDL{
	
	@Id
	@Column(name="room_id", type=DbType.Varchar)
	private String roomId; //房间id
	
	@Column(name="thread_id", type=DbType.BigInt)
	private Long threadId; //线程时间 
	
	@Column(name="create_time", type=DbType.DateTime)
	private Long createTime; //创建时间

	
	
	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public Long getThreadId() {
		return threadId;
	}

	public void setThreadId(Long threadId) {
		this.threadId = threadId;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}
	
	
}
