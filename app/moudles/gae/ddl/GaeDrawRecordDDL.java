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
@Table(name="gae_draw_record")
public class GaeDrawRecordDDL{
	@Id
	@GeneratedValue(generationType= GenerationType.Auto)
	@Column(name="id", type=DbType.Int)
	private Integer id;
	
	@Column(name="draw_id", type=DbType.Varchar)
	private String drawId;
	
	@Column(name="room_id", type=DbType.Varchar)
	private String roomId;
	
	@Column(name="room_name", type=DbType.Varchar)
	private String roomName;
	
	@Column(name="hit_time", type=DbType.DateTime)
	private Long hitTime;
	
	@Column(name="hit_beans", type=DbType.Int)
	private Integer hitBeans;
	
	@Column(name="user_id", type=DbType.Int)
	private Integer userId;
	
	@Column(name="user_avatar", type=DbType.Varchar)
	private String userAvatar;
	
	@Column(name="user_name", type=DbType.Varchar)
	private String userName;
	
	@Column(name="user_ip", type=DbType.Varchar)
	private String userIp;
	
	@Column(name="user_zone", type=DbType.Varchar)
	private String userZone;
	
	@Column(name="is_worst", type=DbType.Int)
	private Integer isWorst;
	
	@Column(name="deduct_beans", type=DbType.Int)
	private Integer deductBeans=0;
	@Column(name="is_robot", type=DbType.Int)
	private Integer isRobot;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id){
		this.id=id;
	}

	
	public String getDrawId() {
		return drawId;
	}
	public void setDrawId(String drawId){
		this.drawId=drawId;
	}
	
	public String getRoomId() {
		return roomId;
	}
	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}
	
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName){
		this.roomName=roomName;
	}

	public Long getHitTime() {
		return hitTime;
	}
	public void setHitTime(Long hitTime){
		this.hitTime=hitTime;
	}

	public Integer getHitBeans() {
		return hitBeans;
	}
	public void setHitBeans(Integer hitBeans){
		this.hitBeans=hitBeans;
	}

	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId){
		this.userId=userId;
	}

	public String getUserAvatar() {
		return userAvatar;
	}
	public void setUserAvatar(String userAvatar){
		this.userAvatar=userAvatar;
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName){
		this.userName=userName;
	}
	
	public String getUserIp() {
		return userIp;
	}
	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}
	public String getUserZone() {
		return userZone;
	}
	public void setUserZone(String userZone){
		this.userZone=userZone;
	}

	public Integer getIsWorst() {
		return isWorst;
	}
	public void setIsWorst(Integer isWorst){
		this.isWorst=isWorst;
	}
	
	public Integer getDeductBeans() {
		return deductBeans==null?Integer.parseInt("0"):deductBeans;
	}
	public void setDeductBeans(Integer deductBeans){
		this.deductBeans=deductBeans==null?Integer.parseInt("0"):deductBeans;
	}
	public Integer getIsRobot() {
		return isRobot;
	}
	public void setIsRobot(Integer isRobot) {
		this.isRobot = isRobot;
	}
	@Override
	public String toString() {
		return "GaeDrawRecordDDL [id=" + id + ", drawId=" + drawId
				+ ", roomId=" + roomId + ", roomName=" + roomName
				+ ", hitTime=" + hitTime + ", hitBeans=" + hitBeans
				+ ", userId=" + userId + ", userAvatar=" + userAvatar
				+ ", userName=" + userName + ", userIp=" + userIp
				+ ", userZone=" + userZone + ", isWorst=" + isWorst
				+ ", deductBeans=" + deductBeans + "]";
	}
	
}
