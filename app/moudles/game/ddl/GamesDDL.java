package moudles.game.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;

/**
 * 
 * @author auto
 * @createDate 2016-07-27 18:08:53
 **/
@Table(name = "games")
public class GamesDDL {
	@Id
	@GeneratedValue(generationType = GenerationType.Auto)
	@Column(name = "game_id", type = DbType.Int)
	private Integer gameId;

	public Integer getGameId() {
		return gameId;
	}

	public void setGameId(Integer gameId) {
		this.gameId = gameId;
	}

	@Column(name = "game_url", type = DbType.Varchar)
	private String gameUrl;

	public String getGameUrl() {
		return gameUrl;
	}

	public void setGameUrl(String gameUrl) {
		this.gameUrl = gameUrl;
	}

	@Column(name = "name", type = DbType.Varchar)
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "cp_id", type = DbType.Int)
	private Integer cpId = 0;

	public Integer getCpId() {
		return cpId == null ? Integer.parseInt("0") : cpId;
	}

	public void setCpId(Integer cpId) {
		this.cpId = cpId == null ? Integer.parseInt("0") : cpId;
	}

	@Column(name = "logo", type = DbType.Varchar)
	private String logo = "";

	public String getLogo() {
		return logo == null ? String.valueOf("") : logo;
	}

	public void setLogo(String logo) {
		this.logo = logo == null ? String.valueOf("") : logo;
	}

	@Column(name = "game_desc", type = DbType.Varchar)
	private String gameDesc = "";

	public String getGameDesc() {
		return gameDesc == null ? String.valueOf("") : gameDesc;
	}

	public void setGameDesc(String gameDesc) {
		this.gameDesc = gameDesc == null ? String.valueOf("") : gameDesc;
	}

	@Column(name = "pay_callback_url", type = DbType.Varchar)
	private String payCallbackUrl;

	public String getPayCallbackUrl() {
		return payCallbackUrl;
	}

	public void setPayCallbackUrl(String payCallbackUrl) {
		this.payCallbackUrl = payCallbackUrl;
	}

	@Column(name = "create_time", type = DbType.DateTime)
	private Long createTime;

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	@Column(name = "remark", type = DbType.Varchar)
	private String remark;

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	// 是否加hot
	// 0不加hot 1加hot
	@Column(name = "hot", type = DbType.Int)
	private Integer hot;

	public Integer getHot() {
		return hot;
	}

	public void setHot(Integer hot) {
		this.hot = hot;
	}

	/**
	 * 状态 1-上线  2-下线  3-删除
	 */
	@Column(name = "status", type = DbType.Int)
	private Integer status;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	/**
	 * 权重
	 */
	@Column(name = "sort", type = DbType.Int)
	private Integer sort;
	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public static GamesDDL newExample() {
		GamesDDL object = new GamesDDL();
		object.setGameId(null);
		object.setGameUrl(null);
		object.setName(null);
		object.setCpId(null);
		object.setLogo(null);
		object.setGameDesc(null);
		object.setPayCallbackUrl(null);
		object.setCreateTime(null);
		object.setRemark(null);
		object.setHot(null);
		object.setStatus(null);
		object.setSort(null);
		return object;
	}
}
