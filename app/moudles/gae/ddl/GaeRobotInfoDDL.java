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
@Table(name="gae_robot_info")
public class GaeRobotInfoDDL{
	
	@Id
	@Column(name="uid", type=DbType.Int)
	private Integer uid;
	
	@Column(name="nick_name", type=DbType.Varchar)
	private String nickName; 
	
	@Column(name="user_ip", type=DbType.Varchar)
	private String userIp;
	
	@Column(name="user_avatar", type=DbType.Varchar)
	private String userAvatar; 
	
	@Column(name="user_zone", type=DbType.Varchar)
	private String userZone;

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getUserIp() {
		return userIp;
	}

	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}

	public String getUserAvatar() {
		return userAvatar;
	}

	public void setUserAvatar(String userAvatar) {
		this.userAvatar = userAvatar;
	}

	public String getUserZone() {
		return userZone;
	}

	public void setUserZone(String userZone) {
		this.userZone = userZone;
	} 
	

	
}
