package moudles.order.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;

/**
 * 游戏-订单关系表
 * 
 * @author Coming
 */
@Table(name = "game_order")
public class GameOrderDDL {

	@Id
	@GeneratedValue(generationType = GenerationType.Auto)
	@Column(name = "id", type = DbType.Int)
	private int id;

	/**
	 * 游戏Id
	 */
	@Column(name = "game_id", type = DbType.Int)
	private int gameId;

	/**
	 * 支付订单号
	 */
	@Column(name = "zhifu_order_id", type = DbType.Varchar)
	private String zhifuOrderId;

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

	public String getZhifuOrderId() {
		return zhifuOrderId;
	}

	public void setZhifuOrderId(String zhifuOrderId) {
		this.zhifuOrderId = zhifuOrderId;
	}

}
