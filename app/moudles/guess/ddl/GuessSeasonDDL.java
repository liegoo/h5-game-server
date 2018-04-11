package moudles.guess.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;

/**
 * 夺宝期
 * 
 * @author Coming
 */
@Table(name = "guess_season")
public class GuessSeasonDDL {

	/**
	 * ID 主键
	 */
	@Id
	@GeneratedValue(generationType = GenerationType.Auto)
	@Column(name = "id", type = DbType.Int)
	private int id;

	/**
	 * 奖品ID
	 */
	@Column(name = "guess_award_id", type = DbType.Int)
	private int guessAwardId;

	/**
	 * 奖池 (用户、机器人投豆数+官方奖励豆)
	 */
	@Column(name = "jackpot", type = DbType.Int)
	private int jackpot;

	/**
	 * 官方追加豆
	 */
	@Column(name = "raise_bean", type = DbType.Int)
	private int raiseBean;

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
	 * 中奖UID
	 */
	@Column(name = "winner_uid", type = DbType.Int)
	private int winnerUid;

	/**
	 * 开奖码
	 */
	@Column(name = "code", type = DbType.Int)
	private int code;

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
	 * 更新时间
	 */
	@Column(name = "update_time", type = DbType.DateTime)
	private long updateTime;

	/**
	 * 状态，1-正常 2-已删除
	 */
	@Column(name = "status", type = DbType.Int)
	private int status;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public int getWinnerUid() {
		return winnerUid;
	}

	public void setWinnerUid(int winnerUid) {
		this.winnerUid = winnerUid;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
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

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getGameLevel() {
		return gameLevel;
	}

	public void setGameLevel(int gameLevel) {
		this.gameLevel = gameLevel;
	}

	public int getJackpot() {
		return jackpot;
	}

	public void setJackpot(int jackpot) {
		this.jackpot = jackpot;
	}

	public int getRaiseBean() {
		return raiseBean;
	}

	public void setRaiseBean(int raiseBean) {
		this.raiseBean = raiseBean;
	}

	


}
