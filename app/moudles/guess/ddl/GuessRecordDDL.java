package moudles.guess.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;

/**
 * 夺宝记录
 * 
 * @author Coming
 */
@Table(name = "guess_record")
public class GuessRecordDDL {

	/**
	 * ID 主键
	 */
	@Id
	@GeneratedValue(generationType = GenerationType.Auto)
	@Column(name = "id", type = DbType.Int)
	private int id;

	/**
	 * 订单号
	 */
	@Column(name = "order_id", type = DbType.Varchar)
	private String orderId;

	/**
	 * 支付订单号
	 */
	@Column(name = "zhifu_order_id", type = DbType.Varchar)
	private String zhifuOrderId;

	/**
	 * 夺宝期Id
	 */
	@Column(name = "guess_season_id", type = DbType.Int)
	private int guessSeasonId;

	/**
	 * 夺宝奖品Id
	 */
	@Column(name = "guess_award_id", type = DbType.Int)
	private int guessAwardId;

	/**
	 * 夺宝期
	 */
	@Column(name = "season_num", type = DbType.Int)
	private int seasonNum;

	/**
	 * 游戏场次
	 */
	@Column(name = "game_level", type = DbType.Int)
	private int gameLevel;

	/**
	 * 用户ID
	 */
	@Column(name = "uid", type = DbType.Int)
	private int uid;

	/**
	 * 用户昵称
	 */
	@Column(name = "nickname", type = DbType.Varchar)
	private String nickname;

	/**
	 * 是否为机器人，0-不是 1-是
	 */
	@Column(name = "is_robot", type = DbType.Int)
	private int isRobot;

	/**
	 * 是否AllIn， 1-是 ，2-不是
	 */
	@Column(name = "is_allin", type = DbType.Int)
	private int isAllIn;

	/**
	 * 花费开心豆
	 */
	@Column(name = "happy_bean", type = DbType.Int)
	private int happyBean;

	/**
	 * 获得豆
	 */
	@Column(name = "reward_bean", type = DbType.Int)
	private int rewardBean;

	/**
	 * 官方加豆
	 */
	@Column(name = "raise_bean", type = DbType.Int)
	private int raiseBean;

	/**
	 * 系统抽成
	 */
	@Column(name = "deduct_bean", type = DbType.Int)
	private int deductBean;

	/**
	 * 用户本期投入开心豆
	 */
	@Column(name = "input_bean", type = DbType.Int)
	private int inputBean;

	/**
	 * 夺宝码
	 */
	@Column(name = "code", type = DbType.Varchar)
	private String code;

	/**
	 * 夺宝码数量
	 */
	@Column(name = "code_amount", type = DbType.Int)
	private int codeAmount;

	/**
	 * 中奖码
	 */
	@Column(name = "winning_code", type = DbType.Int)
	private int winningCode;

	/**
	 * 是否中奖 0-否 1-是
	 */
	@Column(name = "hit", type = DbType.Int)
	private int hit;

	/**
	 * 是否安慰奖 0-否 1-是
	 */
	@Column(name = "is_booby", type = DbType.Int)
	private int isBooby;

	/**
	 * 创建时间
	 */
	@Column(name = "create_time", type = DbType.DateTime)
	private long createTime;

	/**
	 * 开奖时间
	 */
	@Column(name = "publish_time", type = DbType.DateTime)
	private long publishTime;

	/**
	 * 创建时间(毫秒)
	 */
	@Column(name = "millis", type = DbType.Int)
	private int millis;

	/**
	 * 更新时间
	 */
	@Column(name = "update_time", type = DbType.DateTime)
	private long updateTime;

	/**
	 * 是否已读，0-否 1-是
	 */
	@Column(name = "is_read", type = DbType.Int)
	private int isRead;

	/**
	 * 状态，0-无效 1-有效
	 */
	@Column(name = "status", type = DbType.Int)
	private int status;

	/**
	 * 外部订单标题
	 */
	@Column(name = "title", type = DbType.Varchar)
	private String title;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public int getGuessSeasonId() {
		return guessSeasonId;
	}

	public void setGuessSeasonId(int guessSeasonId) {
		this.guessSeasonId = guessSeasonId;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public int getIsRobot() {
		return isRobot;
	}

	public void setIsRobot(int isRobot) {
		this.isRobot = isRobot;
	}

	public int getHappyBean() {
		return happyBean;
	}

	public void setHappyBean(int happyBean) {
		this.happyBean = happyBean;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getHit() {
		return hit;
	}

	public void setHit(int hit) {
		this.hit = hit;
	}

	public int getIsBooby() {
		return isBooby;
	}

	public void setIsBooby(int isBooby) {
		this.isBooby = isBooby;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public int getIsRead() {
		return isRead;
	}

	public void setIsRead(int isRead) {
		this.isRead = isRead;
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

	public int getSeasonNum() {
		return seasonNum;
	}

	public void setSeasonNum(int seasonNum) {
		this.seasonNum = seasonNum;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public int getGuessAwardId() {
		return guessAwardId;
	}

	public void setGuessAwardId(int guessAwardId) {
		this.guessAwardId = guessAwardId;
	}

	public int getMillis() {
		return millis;
	}

	public void setMillis(int millis) {
		this.millis = millis;
	}

	public int getRewardBean() {
		return rewardBean;
	}

	public void setRewardBean(int rewardBean) {
		this.rewardBean = rewardBean;
	}

	public int getRaiseBean() {
		return raiseBean;
	}

	public void setRaiseBean(int raiseBean) {
		this.raiseBean = raiseBean;
	}

	public String getZhifuOrderId() {
		return zhifuOrderId;
	}

	public void setZhifuOrderId(String zhifuOrderId) {
		this.zhifuOrderId = zhifuOrderId;
	}

	public long getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(long publishTime) {
		this.publishTime = publishTime;
	}

	public int getWinningCode() {
		return winningCode;
	}

	public void setWinningCode(int winningCode) {
		this.winningCode = winningCode;
	}

	public int getCodeAmount() {
		return codeAmount;
	}

	public void setCodeAmount(int codeAmount) {
		this.codeAmount = codeAmount;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getIsAllIn() {
		return isAllIn;
	}

	public void setIsAllIn(int isAllIn) {
		this.isAllIn = isAllIn;
	}

	public int getDeductBean() {
		return deductBean;
	}

	public void setDeductBean(int deductBean) {
		this.deductBean = deductBean;
	}

	public int getInputBean() {
		return inputBean;
	}

	public void setInputBean(int inputBean) {
		this.inputBean = inputBean;
	}

}
