package moudles.rank.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;

@Table(name = "rank")
public class RankDDL {

	@Id
	@GeneratedValue(generationType = GenerationType.Auto)
	@Column(name = "id", type = DbType.Int)
	private int id;

	@Column(name = "uid", type = DbType.Int)
	private int uid;

	@Column(name = "suid", type = DbType.Int)
	private int suid;

	@Column(name = "game_id", type = DbType.Int)
	private int gameId;

	@Column(name = "happy_bean", type = DbType.BigInt)
	private long happyBean;

	@Column(name = "create_time", type = DbType.DateTime)
	private long createTime;

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

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
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

	public long getHappyBean() {
		return happyBean;
	}

	public void setHappyBean(long happyBean) {
		this.happyBean = happyBean;
	}

	public int getSuid() {
		return suid;
	}

	public void setSuid(int suid) {
		this.suid = suid;
	}

}
