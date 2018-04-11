package moudles.capital.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;

@Table(name = "capital_pool")
public class CapitalPoolDDL {

	@Id
	@GeneratedValue(generationType = GenerationType.Auto)
	@Column(name = "id", type = DbType.Int)
	private int id;

	@Column(name = "game_id", type = DbType.Int)
	private int gameId;

	@Column(name = "game_level", type = DbType.Int)
	private int gameLevel;

	@Column(name = "uid", type = DbType.Int)
	private int uid;

	@Column(name = "happy_bean", type = DbType.BigInt)
	private long happyBean;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public int getGameLevel() {
		return gameLevel;
	}

	public void setGameLevel(int gameLevel) {
		this.gameLevel = gameLevel;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public long getHappyBean() {
		return happyBean;
	}

	public void setHappyBean(long happyBean) {
		this.happyBean = happyBean;
	}

}
