package moudles.gae.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;
/**
 * 
 * @author auto
 * @createDate 2016-11-15 15:58:15
 **/
@Table(name="gae_draw_record_temp")
public class GaeDrawRecordTempDDL{
	@Id
	@GeneratedValue(generationType= GenerationType.Auto)
	@Column(name="id", type=DbType.Int)
	private Integer id;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id){
		this.id=id;
	}

	@Column(name="draw_id", type=DbType.Varchar)
	private String drawId;
	public String getDrawId() {
		return drawId;
	}
	public void setDrawId(String drawId){
		this.drawId=drawId;
	}

	@Column(name="room_id", type=DbType.Varchar)
	private String roomId;
	public String getRoomId() {
		return roomId;
	}
	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	@Column(name="room_name", type=DbType.Varchar)
	private String roomName;
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName){
		this.roomName=roomName;
	}

	@Column(name="hit_time", type=DbType.DateTime)
	private Long hitTime;
	public Long getHitTime() {
		return hitTime;
	}
	public void setHitTime(Long hitTime){
		this.hitTime=hitTime;
	}

	@Column(name="user_id", type=DbType.Int)
	private Integer userId;
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId){
		this.userId=userId;
	}

	@Column(name="user_avatar", type=DbType.Varchar)
	private String userAvatar;
	public String getUserAvatar() {
		return userAvatar;
	}
	public void setUserAvatar(String userAvatar){
		this.userAvatar=userAvatar;
	}

	@Column(name="user_name", type=DbType.Varchar)
	private String userName;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName){
		this.userName=userName;
	}
	
	@Column(name="user_ip", type=DbType.Varchar)
	private String userIp;
	
	
	public String getUserIp() {
		return userIp;
	}
	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}

	@Column(name="user_zone", type=DbType.Varchar)
	private String userZone;
	public String getUserZone() {
		return userZone;
	}
	public void setUserZone(String userZone){
		this.userZone=userZone;
	}
	
	@Column(name="is_robot", type=DbType.Int)
	private Integer isRobot;
	public Integer getIsRobot() {
		return isRobot;
	}
	public void setIsRobot(Integer isRobot) {
		this.isRobot = isRobot;
	}

	@Column(name="deduct_beans", type=DbType.Int)
	private Integer deductBeans=0;
	public Integer getDeductBeans() {
		return deductBeans==null?Integer.parseInt("0"):deductBeans;
	}
	public void setDeductBeans(Integer deductBeans){
		this.deductBeans=deductBeans==null?Integer.parseInt("0"):deductBeans;
	}

	@Column(name="order_id", type=DbType.Varchar)
	private String orderId;
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
}
