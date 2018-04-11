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
@Table(name="gae_robot_setting")
public class GaeRobotSettDDL{
	
	@Id
	@Column(name="room_id", type=DbType.Varchar)
	private String roomId; //房间id
	
	@Column(name="sleep_time", type=DbType.Varchar)
	private String sleepTime; //休眠时间              
	
	
	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public String getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(String sleepTime) {
		this.sleepTime = sleepTime;
	}
	
}
