package moudles.task.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;

/**
 * 游戏任务
 * 
 * @author Coming
 */

@Table(name = "game_task")
public class GameTaskDDL {

	@Id
	@GeneratedValue(generationType = GenerationType.Auto)
	@Column(name = "id", type = DbType.Int)
	private int id;

	/**
	 * 任务类型
	 */
	@Column(name = "task_type", type = DbType.Int)
	private int taskType;

	/**
	 * 奖品类型
	 */
	@Column(name = "award_type", type = DbType.Int)
	private int awardType;

	/**
	 * 奖励数
	 */
	@Column(name = "award_num", type = DbType.Int)
	private int awardNum;

	/**
	 * 任务链接
	 */
	@Column(name = "task_url", type = DbType.Varchar)
	private String taskUrl;

	/**
	 * 任务图标
	 */
	@Column(name = "icon_url", type = DbType.Varchar)
	private String iconUrl;

	/**
	 * 是否在首页显示
	 */
	@Column(name = "index_show", type = DbType.Int)
	private int indexShow;

	/**
	 * 排序
	 */
	@Column(name = "sort", type = DbType.Int)
	private int sort;

	/**
	 * 游戏场次
	 */
	@Column(name = "game_level", type = DbType.BigInt)
	private long gameLevel;

	/**
	 * 游戏Id
	 */
	@Column(name = "game_id", type = DbType.Int)
	private int gameId;

	/**
	 * 完成指标
	 */
	@Column(name = "target", type = DbType.Int)
	private int target;

	/**
	 * 任务描述
	 */
	@Column(name = "task_desc", type = DbType.Varchar)
	private String taskDesc;

	/**
	 * 奖品描述
	 */
	@Column(name = "award_desc", type = DbType.Varchar)
	private String awardDesc;

	/**
	 * 创建时间
	 */
	@Column(name = "create_time", type = DbType.DateTime)
	private long createTime;

	/**
	 * 任务状态
	 */
	@Column(name = "status", type = DbType.Int)
	private int status;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTaskType() {
		return taskType;
	}

	public void setTaskType(int taskType) {
		this.taskType = taskType;
	}

	public int getAwardType() {
		return awardType;
	}

	public void setAwardType(int awardType) {
		this.awardType = awardType;
	}

	public int getAwardNum() {
		return awardNum;
	}

	public void setAwardNum(int awardNum) {
		this.awardNum = awardNum;
	}

	public String getTaskUrl() {
		return taskUrl;
	}

	public void setTaskUrl(String taskUrl) {
		this.taskUrl = taskUrl;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public int getIndexShow() {
		return indexShow;
	}

	public void setIndexShow(int indexShow) {
		this.indexShow = indexShow;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public long getGameLevel() {
		return gameLevel;
	}

	public void setGameLevel(long gameLevel) {
		this.gameLevel = gameLevel;
	}

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public int getTarget() {
		return target;
	}

	public void setTarget(int target) {
		this.target = target;
	}

	public String getTaskDesc() {
		return taskDesc;
	}

	public void setTaskDesc(String taskDesc) {
		this.taskDesc = taskDesc;
	}

	public String getAwardDesc() {
		return awardDesc;
	}

	public void setAwardDesc(String awardDesc) {
		this.awardDesc = awardDesc;
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

}
