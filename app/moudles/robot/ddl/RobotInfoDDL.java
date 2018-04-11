package moudles.robot.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;

/**
 * 机器人信息
 * 
 * @author Coming
 */
/**
 * 
 * @author Coming
 */
@Table(name = "robot_info")
public class RobotInfoDDL {

	/**
	 * ID 主键
	 */
	@Id
	@GeneratedValue(generationType = GenerationType.Auto)
	@Column(name = "id", type = DbType.Int)
	private int id;

	/**
	 * 机器人UID
	 */
	@Column(name = "uid", type = DbType.Int)
	private int uid;

	/**
	 * 昵称
	 */
	@Column(name = "nickname", type = DbType.Varchar)
	private String nickname;

	/**
	 * 手机
	 */
	@Column(name = "mobile", type = DbType.Varchar)
	private String mobile;

	/**
	 * 创建时间
	 */
	@Column(name = "create_time", type = DbType.DateTime)
	private long createTime;

	/**
	 * 状态，0-停用 1-启用
	 */
	@Column(name = "status", type = DbType.Int)
	private int status;

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

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
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

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

}
