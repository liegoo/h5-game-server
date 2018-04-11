package moudles.odds.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;

@Table(name = "odds")
public class OddsDDL {
	@Id
	@GeneratedValue(generationType = GenerationType.Auto)
	@Column(name = "id", type = DbType.Int)
	private int id;

	@Column(name = "begin", type = DbType.Int)
	private int begin;

	@Column(name = "end", type = DbType.Int)
	private int end;

	@Column(name = "ratio", type = DbType.Float)
	private float ratio;

	@Column(name = "game_level", type = DbType.Int)
	private int gameLevel;

	@Column(name = "happy_bean", type = DbType.BigInt)
	private long happyBean;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getGameLevel() {
		return gameLevel;
	}

	public void setGameLevel(int gameLevel) {
		this.gameLevel = gameLevel;
	}

	public long getHappyBean() {
		return happyBean;
	}

	public void setHappyBean(long happyBean) {
		this.happyBean = happyBean;
	}

	public int getBegin() {
		return begin;
	}

	public void setBegin(int begin) {
		this.begin = begin;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public float getRatio() {
		return ratio;
	}

	public void setRatio(float ratio) {
		this.ratio = ratio;
	}

}
