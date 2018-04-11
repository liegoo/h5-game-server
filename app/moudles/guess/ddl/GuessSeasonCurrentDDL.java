package moudles.guess.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;

/**
 * 当前夺宝期
 * 
 * @author Coming
 */
@Table(name = "guess_season_current")
public class GuessSeasonCurrentDDL {

	/**
	 * 夺宝期ID
	 */
	@Column(name = "guess_season_id", type = DbType.Int)
	private int guessSeasonId;

	/**
	 * 奖品ID
	 */
	@Column(name = "guess_award_id", type = DbType.Int)
	private int guessAwardId;

	/**
	 * 游戏场次
	 */
	@Column(name = "game_level", type = DbType.Int)
	private int gameLevel;

	/**
	 * 期
	 */
	@Column(name = "season_num", type = DbType.Int)
	private int seasonNum;

	/**
	 * 投注单价
	 */
	@Column(name = "base_bean", type = DbType.Int)
	private int baseBean;

	/**
	 * 当前总投开心豆数
	 */
	@Column(name = "current_bean", type = DbType.Int)
	private int currentBean;

	/**
	 * 开奖时间
	 */
	@Column(name = "publish_time", type = DbType.DateTime)
	private Long publishTime;

	/**
	 * 创建时间
	 */
	@Column(name = "create_time", type = DbType.DateTime)
	private long createTime;

	/**
	 * 加奖规则
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

	public int getBaseBean() {
		return baseBean;
	}

	public void setBaseBean(int baseBean) {
		this.baseBean = baseBean;
	}

	public int getCurrentBean() {
		return currentBean;
	}

	public void setCurrentBean(int currentBean) {
		this.currentBean = currentBean;
	}

	public int getGuessAwardId() {
		return guessAwardId;
	}

	public void setGuessAwardId(int guessAwardId) {
		this.guessAwardId = guessAwardId;
	}

	public int getSeasonNum() {
		return seasonNum;
	}

	public void setSeasonNum(int seasonNum) {
		this.seasonNum = seasonNum;
	}

	public Long getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Long publishTime) {
		this.publishTime = publishTime;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public int getGameLevel() {
		return gameLevel;
	}

	public void setGameLevel(int gameLevel) {
		this.gameLevel = gameLevel;
	}

	public int getGuessSeasonId() {
		return guessSeasonId;
	}

	public void setGuessSeasonId(int guessSeasonId) {
		this.guessSeasonId = guessSeasonId;
	}

	public String getRaiseRule() {
		return raiseRule;
	}

	public void setRaiseRule(String raiseRule) {
		this.raiseRule = raiseRule;
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

}
