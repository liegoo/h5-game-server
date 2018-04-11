package moudles.guess.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;

/**
 * 夺宝奖品
 * 
 * @author Coming
 */
@Table(name = "guess_award")
public class GuessAwardDDL {

	/**
	 * ID 主键
	 */
	@Id
	@GeneratedValue(generationType = GenerationType.Auto)
	@Column(name = "id", type = DbType.Int)
	private int id;

	/**
	 * 奖品名称
	 */
	@Column(name = "name", type = DbType.Varchar)
	private String name;

	/**
	 * 游戏场次 1-热身场 2-中级场 3-高级场
	 */
	@Column(name = "game_level", type = DbType.Int)
	private int gameLevel;

	/**
	 * 游戏类型， 1-定时夺宝 2-定额夺宝
	 */
	@Column(name = "type", type = DbType.Int)
	private int type;

	/**
	 * 投注单价
	 */
	@Column(name = "happy_bean", type = DbType.Int)
	private int happyBean;

	/**
	 * 奖品价值
	 */
	@Column(name = "worth", type = DbType.Int)
	private int worth;

	/**
	 * 倒计时
	 */
	@Column(name = "countdown", type = DbType.Int)
	private int countdown;

	/**
	 * 是否开启辅助，0-未开启 1-开启
	 */
	@Column(name = "is_auto", type = DbType.Int)
	private int isAuto;

	/**
	 * 机器人购买随机时间段
	 */
	@Column(name = "work_interval", type = DbType.Int)
	private int workInterval;

	/**
	 * 最大购买数量
	 */
	@Column(name = "max_quantity", type = DbType.Int)
	private int maxQuantity;

	/**
	 * 机器人购买临界值
	 */
	@Column(name = "critical_value", type = DbType.Int)
	private int criticalValue;

	/**
	 * 官方加码规则
	 */
	@Column(name = "raise_rule", type = DbType.Varchar)
	private String raiseRule;

	/**
	 * 安慰奖累计
	 */
	@Column(name = "booby_prize", type = DbType.Int)
	private int boobyPrize;

	/**
	 * 安慰奖返还比例
	 */
	@Column(name = "booby_rate", type = DbType.Double)
	private double boobyRate;

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

	/**
	 * 排序
	 */
	@Column(name = "sort", type = DbType.Int)
	private int sort;

	/**
	 * 状态，1-正常 2-下架 3-已删除
	 */
	@Column(name = "status", type = DbType.Int)
	private int status;

	/**
	 * 后台操作人员
	 */
	@Column(name = "op_name", type = DbType.Varchar)
	private String opName;

	/**
	 * 机器人配置
	 */
	@Column(name = "robot_config", type = DbType.Varchar)
	private String robotConfig;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getGameLevel() {
		return gameLevel;
	}

	public void setGameLevel(int gameLevel) {
		this.gameLevel = gameLevel;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getHappyBean() {
		return happyBean;
	}

	public void setHappyBean(int happyBean) {
		this.happyBean = happyBean;
	}

	public int getCountdown() {
		return countdown;
	}

	public void setCountdown(int countdown) {
		this.countdown = countdown;
	}

	public int getIsAuto() {
		return isAuto;
	}

	public void setIsAuto(int isAuto) {
		this.isAuto = isAuto;
	}

	public int getWorkInterval() {
		return workInterval;
	}

	public void setWorkInterval(int workInterval) {
		this.workInterval = workInterval;
	}

	public int getMaxQuantity() {
		return maxQuantity;
	}

	public void setMaxQuantity(int maxQuantity) {
		this.maxQuantity = maxQuantity;
	}

	public int getCriticalValue() {
		return criticalValue;
	}

	public void setCriticalValue(int criticalValue) {
		this.criticalValue = criticalValue;
	}

	public int getBoobyPrize() {
		return boobyPrize;
	}

	public void setBoobyPrize(int boobyPrize) {
		this.boobyPrize = boobyPrize;
	}

	public double getBoobyRate() {
		return boobyRate;
	}

	public void setBoobyRate(double boobyRate) {
		this.boobyRate = boobyRate;
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

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getRaiseRule() {
		return raiseRule;
	}

	public void setRaiseRule(String raiseRule) {
		this.raiseRule = raiseRule;
	}

	public int getWorth() {
		return worth;
	}

	public void setWorth(int worth) {
		this.worth = worth;
	}

	public String getOpName() {
		return opName;
	}

	public void setOpName(String opName) {
		this.opName = opName;
	}

	public String getRobotConfig() {
		return robotConfig;
	}

	public void setRobotConfig(String robotConfig) {
		this.robotConfig = robotConfig;
	}

}
