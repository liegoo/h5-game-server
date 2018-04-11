package moudles.award.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;

@Table(name = "award_assign")
public class AwardAssignDDL {
	@Id
	@GeneratedValue(generationType = GenerationType.Auto)
	@Column(name = "id", type = DbType.Int)
	private Integer id;

	@Column(name = "award_id", type = DbType.Int)
	private Integer awardId;

	@Column(name = "hits", type = DbType.Int)
	private Integer hits;

	@Column(name = "remain", type = DbType.Int)
	private Integer remain;

	@Column(name = "sort", type = DbType.Int)
	private Integer sort;

	@Column(name = "game_level", type = DbType.Int)
	private Integer gameLevel;

	@Column(name = "create_time", type = DbType.DateTime)
	private Long createTime;

	@Column(name = "weight", type = DbType.Int)
	private Integer weight;

	@Column(name = "status", type = DbType.Int)
	private Integer status;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getAwardId() {
		return awardId;
	}

	public void setAwardId(Integer awardId) {
		this.awardId = awardId;
	}

	public Integer getHits() {
		return hits;
	}

	public void setHits(Integer hits) {
		this.hits = hits;
	}

	public Integer getRemain() {
		return remain;
	}

	public void setRemain(Integer remain) {
		this.remain = remain;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Integer getGameLevel() {
		return gameLevel;
	}

	public void setGameLevel(Integer gameLevel) {
		this.gameLevel = gameLevel;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public static AwardAssignDDL newExample() {
		AwardAssignDDL object = new AwardAssignDDL();
		object.setAwardId(null);
		object.setCreateTime(null);
		object.setGameLevel(null);
		object.setHits(null);
		object.setId(null);
		object.setWeight(null);
		object.setRemain(null);
		object.setSort(null);
		object.setStatus(null);
		return object;
	}

}
