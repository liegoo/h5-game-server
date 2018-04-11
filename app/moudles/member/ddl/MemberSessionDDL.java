package moudles.member.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;
/**
 * 
 * @author auto
 * @createDate 2016-07-27 18:14:26
 **/
@Table(name="member_session")
public class MemberSessionDDL{
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

	@Column(name="tmp_token", type=DbType.Varchar)
	private String tmpToken;
	public String getTmpToken() {
		return tmpToken;
	}
	public void setTmpToken(String tmpToken){
		this.tmpToken=tmpToken;
	}

	@Column(name="uid", type=DbType.Int)
	private Integer uid;
	public Integer getUid() {
		return uid;
	}
	public void setUid(Integer uid){
		this.uid=uid;
	}

	@Column(name="suid", type=DbType.Int)
	private Integer suid;
	public Integer getSuid() {
		return suid;
	}
	public void setSuid(Integer suid){
		this.suid=suid;
	}

	@Column(name="cp_id", type=DbType.Int)
	private Integer cpId;
	public Integer getCpId() {
		return cpId;
	}
	public void setCpId(Integer cpId){
		this.cpId=cpId;
	}

	@Column(name="game_id", type=DbType.Int)
	private Integer gameId;
	public Integer getGameId() {
		return gameId;
	}
	public void setGameId(Integer gameId){
		this.gameId=gameId;
	}

	@Column(name="token", type=DbType.Varchar)
	private String token="";
	public String getToken() {
		return token==null?String.valueOf(""):token;
	}
	public void setTocken(String token){
		this.token=token==null?String.valueOf(""):token;
	}

	@Column(name="exp_time", type=DbType.DateTime)
	private Long expTime;
	public Long getExpTime() {
		return expTime;
	}
	public void setExpTime(Long expTime){
		this.expTime=expTime;
	}

	@Column(name="create_time", type=DbType.DateTime)
	private Long createTime;
	public Long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Long createTime){
		this.createTime=createTime;
	}

	public static MemberSessionDDL newExample(){
		MemberSessionDDL object=new MemberSessionDDL();
		object.setId(null);
		object.setTmpToken(null);
		object.setUid(null);
		object.setSuid(null);
		object.setCpId(null);
		object.setGameId(null);
		object.setTocken(null);
		object.setExpTime(null);
		object.setCreateTime(null);
		return object;
	}
}
