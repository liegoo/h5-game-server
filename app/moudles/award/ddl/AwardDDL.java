package moudles.award.ddl;

import java.io.Serializable;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;

/**
 * 
 * @author auto
 * @createDate 2016-07-27 18:14:37
 **/
@Table(name = "award")
public class AwardDDL implements Serializable{
	@Id
	@GeneratedValue(generationType = GenerationType.Auto)
	@Column(name = "id", type = DbType.Int)
	private Integer id;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "name", type = DbType.Varchar)
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "happy_bean", type = DbType.Int)
	private Integer happyBean;

	public Integer getHappyBean() {
		return happyBean;
	}

	public void setHappyBean(Integer happyBean) {
		this.happyBean = happyBean;
	}

	@Column(name = "status", type = DbType.Int)
	private Integer status;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "type", type = DbType.Int)
	private Integer type;

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "total_num", type = DbType.Int)
	private Integer totalNum;

	public Integer getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(Integer totalNum) {
		this.totalNum = totalNum;
	}

	@Column(name = "exchage_num", type = DbType.Int)
	private Integer exchageNum;

	public Integer getExchageNum() {
		return exchageNum;
	}

	public void setExchageNum(Integer exchageNum) {
		this.exchageNum = exchageNum;
	}

	@Column(name = "store_num", type = DbType.Int)
	private Integer storeNum;

	public Integer getStoreNum() {
		return storeNum;
	}

	public void setStoreNum(Integer storeNum) {
		this.storeNum = storeNum;
	}

	@Column(name = "sort", type = DbType.Int)
	private Integer sort;

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	@Column(name = "op_name", type = DbType.Varchar)
	private String opName;

	public String getOpName() {
		return opName;
	}

	public void setOpName(String opName) {
		this.opName = opName;
	}

	@Column(name = "update_time", type = DbType.DateTime)
	private Long updateTime;

	public Long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

	@Column(name = "crate_time", type = DbType.DateTime)
	private Long crateTime;

	public Long getCrateTime() {
		return crateTime;
	}

	public void setCrateTime(Long crateTime) {
		this.crateTime = crateTime;
	}

	@Column(name = "img_url", type = DbType.Varchar)
	private String imgUrl = "";

	public String getImgUrl() {
		return imgUrl == null ? String.valueOf("") : imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl == null ? String.valueOf("") : imgUrl;
	}

	/**
	 * 场景 1领奖首页 2夹娃娃
	 */
	@Column(name = "scope", type = DbType.Int)
	public Integer scope;

	public Integer getScope() {
		return scope;
	}

	public void setScope(Integer scope) {
		this.scope = scope;
	}
	
	/**
	 * 是否在首页显示 1-在首页显示 2-不在首页显示 
	 */
	@Column(name="is_show_index", type=DbType.Int)
	private int isShowIndex;
	public int getIsShowIndex() {
		return isShowIndex;
	}
	public void setIsShowIndex(int isShowIndex) {
		this.isShowIndex = isShowIndex;
	}
	
	/**
	 * 该奖品适用的游戏
	 */
	@Column(name="game_id", type=DbType.Int)
	private Integer gameId;
	public Integer getGameId() {
		return gameId;
	}
	public void setGameId(Integer gameId) {
		this.gameId = gameId;
	}

	public static AwardDDL newExample() {
		AwardDDL object = new AwardDDL();
		object.setId(null);
		object.setName(null);
		object.setHappyBean(null);
		object.setStatus(null);
		object.setType(null);
		object.setTotalNum(null);
		object.setExchageNum(null);
		object.setStoreNum(null);
		object.setSort(null);
		object.setOpName(null);
		object.setUpdateTime(null);
		object.setCrateTime(null);
		object.setImgUrl(null);
		object.setScope(null);
		return object;
	}
}
