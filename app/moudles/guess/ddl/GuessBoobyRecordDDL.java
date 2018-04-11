package moudles.guess.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;

/**
 * 安慰奖累计
 * 
 * @author Coming
 */
@Table(name = "guess_booby_record")
public class GuessBoobyRecordDDL {

	/**
	 * ID 主键
	 */
	@Id
	@GeneratedValue(generationType = GenerationType.Auto)
	@Column(name = "id", type = DbType.Int)
	private int id;

	/**
	 * 场次ID
	 */
	@Column(name = "guess_award_id", type = DbType.Int)
	private int guessAwardId;

	/**
	 * 累计开心豆数
	 */
	@Column(name = "happy_bean", type = DbType.Int)
	private int happyBean;

	/**
	 * 用户ID
	 */
	@Column(name = "uid", type = DbType.Int)
	private int uid;
	
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

	public int getGuessAwardId() {
		return guessAwardId;
	}

	public void setGuessAwardId(int guessAwardId) {
		this.guessAwardId = guessAwardId;
	}

	public int getHappyBean() {
		return happyBean;
	}

	public void setHappyBean(int happyBean) {
		this.happyBean = happyBean;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

}
