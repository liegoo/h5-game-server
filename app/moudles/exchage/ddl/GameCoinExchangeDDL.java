package moudles.exchage.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;
/**
 * 
 * @author auto
 * @createDate 2016-08-10 17:51:16
 **/
@Table(name="game_coin_exchange")
public class GameCoinExchangeDDL{
	@Id
	@GeneratedValue(generationType= GenerationType.Auto)
	@Column(name="id", type=DbType.Int)
	private Integer id;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id){
		this.id=id;
	}

	@Column(name="game_id", type=DbType.Int)
	private Integer gameId;
	public Integer getGameId() {
		return gameId;
	}
	public void setGameId(Integer gameId){
		this.gameId=gameId;
	}
	
	@Column(name="cp_id", type=DbType.Int)
	private Integer cpId;
	public Integer getCpId() {
		return cpId;
	}
	public void setCpId(Integer cpId) {
		this.cpId = cpId;
	}

	@Column(name="game_name", type=DbType.Varchar)
	private String gameName;
	public String getGameName() {
		return gameName;
	}
	public void setGameName(String gameName){
		this.gameName=gameName;
	}
	
	@Column(name="backurl", type=DbType.Varchar)
	private String backurl;
	public String getBackurl() {
		return backurl;
	}
	public void setBackurl(String backurl) {
		this.backurl = backurl;
	}

	@Column(name="uid", type=DbType.Int)
	private int uid;
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}

	@Column(name="happy_bean", type=DbType.Int)
	private Integer happyBean;
	public Integer getHappyBean() {
		return happyBean;
	}
	public void setHappyBean(Integer happyBean){
		this.happyBean=happyBean;
	}

	@Column(name="game_coin", type=DbType.Int)
	private Integer gameCoin;
	public Integer getGameCoin() {
		return gameCoin;
	}
	public void setGameCoin(Integer gameCoin){
		this.gameCoin=gameCoin;
	}

	@Column(name="status", type=DbType.Int)
	private Integer status=0;
	public Integer getStatus() {
		return status==null?Integer.parseInt("0"):status;
	}
	public void setStatus(Integer status){
		this.status=status==null?Integer.parseInt("0"):status;
	}

	@Column(name="create_time", type=DbType.DateTime)
	private Long createTime;
	public Long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Long createTime){
		this.createTime=createTime;
	}

	@Column(name="update_time", type=DbType.DateTime)
	private Long updateTime;
	public Long getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Long updateTime){
		this.updateTime=updateTime;
	}
	
	@Column(name="bill_id", type=DbType.Varchar)
	private String billId;
	public String getBillId() {
		return billId;
	}
	public void setBillId(String billId) {
		this.billId = billId;
	}

	public static GameCoinExchangeDDL newExample(){
		GameCoinExchangeDDL object=new GameCoinExchangeDDL();
		object.setId(null);
		object.setGameId(null);
		object.setGameName(null);
 		object.setHappyBean(null);
		object.setGameCoin(null);
		object.setStatus(null);
		object.setCreateTime(null);
		object.setUpdateTime(null);
		return object;
	}
}
