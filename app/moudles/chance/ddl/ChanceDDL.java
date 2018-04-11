package moudles.chance.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;

@Table(name = "chance")
public class ChanceDDL {

	@Id
	@GeneratedValue(generationType = GenerationType.Auto)
	@Column(name = "id", type = DbType.Int)
	private Integer id;

	@Column(name = "uid", type = DbType.Int)
	private Integer uid;

	@Column(name = "chance", type = DbType.Int)
	private Integer chance;

	@Column(name = "status", type = DbType.Int)
	private Integer status;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public Integer getChance() {
		return chance;
	}

	public void setChance(Integer chance) {
		this.chance = chance;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public static ChanceDDL newExample() {
		ChanceDDL object = new ChanceDDL();
		object.setChance(null);
		object.setId(null);
		object.setUid(null);
		object.setStatus(null);
		return object;
	}
}
