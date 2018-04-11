package moudles.guess.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;

/**
 * 
 * @author Coming
 */
@Table(name = "guess_record_temp")
public class GuessRecordTempDDL {

	/**
	 * ID 主键
	 */
	@Id
	@GeneratedValue(generationType = GenerationType.Auto)
	@Column(name = "id", type = DbType.Int)
	private int id;

	/**
	 * 场次
	 */
	@Column(name = "season_id", type = DbType.Int)
	private int seasonId;
	
	/**
	 * 夺宝码个数
	 */
	@Column(name = "code_amount", type = DbType.Int)
	private int codeAmount;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSeasonId() {
		return seasonId;
	}

	public void setSeasonId(int seasonId) {
		this.seasonId = seasonId;
	}

	public int getCodeAmount() {
		return codeAmount;
	}

	public void setCodeAmount(int codeAmount) {
		this.codeAmount = codeAmount;
	}
	
	

}
