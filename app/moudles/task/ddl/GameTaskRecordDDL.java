package moudles.task.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;

/**
 * 游戏任务记录
 * 
 * @author Coming
 */
@Table(name = "game_task_record")
public class GameTaskRecordDDL {

	@Id
	@GeneratedValue(generationType = GenerationType.Auto)
	@Column(name = "id", type = DbType.Int)
	private int id;

	/**
	 * 用户id
	 */
	@Column(name = "uid", type = DbType.Int)
	private int uid;

	/**
	 * 任务id
	 */
	@Column(name = "task_id", type = DbType.Int)
	private int taskId;

	/**
	 * 奖励数
	 */
	@Column(name = "award_num", type = DbType.Int)
	private int awardNum;

	/**
	 * 游戏Id
	 */
	@Column(name = "game_id", type = DbType.Int)
	private int gameId;

	/**
	 * 奖励发放状态
	 */
	@Column(name = "award_status", type = DbType.Int)
	private int awardStatus;

	/**
	 * 已完成目标数
	 */
	@Column(name = "complete_num", type = DbType.Int)
	private int completeNum;

	/**
	 * 目标数
	 */
	@Column(name = "target", type = DbType.Int)
	private int target;

	/**
	 * 备注
	 */
	@Column(name = "remark", type = DbType.Varchar)
	private String remark;

	/**
	 * 创建时间
	 */
	@Column(name = "create_time", type = DbType.DateTime)
	private long createTime;

	/**
	 * 更新时间
	 */
	@Column(name = "update_time", type = DbType.DateTime)
	private long updateTime;

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

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public int getAwardNum() {
		return awardNum;
	}

	public void setAwardNum(int awardNum) {
		this.awardNum = awardNum;
	}

	public int getAwardStatus() {
		return awardStatus;
	}

	public void setAwardStatus(int awardStatus) {
		this.awardStatus = awardStatus;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	public int getCompleteNum() {
		return completeNum;
	}

	public void setCompleteNum(int completeNum) {
		this.completeNum = completeNum;
	}

	public int getTarget() {
		return target;
	}

	public void setTarget(int target) {
		this.target = target;
	}

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

}
