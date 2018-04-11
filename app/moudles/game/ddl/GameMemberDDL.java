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
 * @createDate 2016-07-27 18:09:12
 **/
@Table(name="game_member")
public class GameMemberDDL{
	@Id
	@GeneratedValue(generationType= GenerationType.Auto)
	@Column(name="suid", type=DbType.Int)
	private Integer suid;
	public Integer getSuid() {
		return suid;
	}
	public void setSuid(Integer suid){
		this.suid=suid;
	}

	@Column(name="uid", type=DbType.Int)
	private Integer uid=0;
	public Integer getUid() {
		return uid==null?Integer.parseInt("0"):uid;
	}
	public void setUid(Integer uid){
		this.uid=uid==null?Integer.parseInt("0"):uid;
	}

	@Column(name="cp_id", type=DbType.Int)
	private Integer cpId=0;
	public Integer getCpId() {
		return cpId==null?Integer.parseInt("0"):cpId;
	}
	public void setCpId(Integer cpId){
		this.cpId=cpId==null?Integer.parseInt("0"):cpId;
	}

	@Column(name="game_id", type=DbType.Int)
	private Integer gameId=0;
	public Integer getGameId() {
		return gameId==null?Integer.parseInt("0"):gameId;
	}
	public void setGameId(Integer gameId){
		this.gameId=gameId==null?Integer.parseInt("0"):gameId;
	}

	@Column(name="status", type=DbType.Int)
	private Integer status=0;
	public Integer getStatus() {
		return status==null?Integer.parseInt("0"):status;
	}
	public void setStatus(Integer status){
		this.status=status==null?Integer.parseInt("0"):status;
	}

	@Column(name="createTime", type=DbType.DateTime)
	private Long createTime;
	public Long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Long createTime){
		this.createTime=createTime;
	}

	@Column(name="remark", type=DbType.Varchar)
	private String remark;
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark){
		this.remark=remark;
	}

	public static GameMemberDDL newExample(){
		GameMemberDDL object=new GameMemberDDL();
		object.setSuid(null);
		object.setUid(null);
		object.setCpId(null);
		object.setGameId(null);
		object.setStatus(null);
		object.setCreateTime(null);
		object.setRemark(null);
		return object;
	}
}
