package moudles.gae.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;
/**
 * 
 * @author auto
 * @createDate 2016-11-15 15:58:15
 **/
@Table(name="gae_prize_top")
public class GaePrizeTopDDL{
	@Id
	@Column(name="id", type=DbType.Int)
	private Integer id;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id){
		this.id=id;
	}
	
	@Column(name="top_type", type=DbType.Int)
	private Integer topType;
	public Integer getTopType() {
		return topType;
	}
	public void setTopType(Integer topType){
		this.topType=topType;
	}

	@Column(name="user_id", type=DbType.Int)
	private Integer userId;
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId){
		this.userId=userId;
	}

	@Column(name="user_name", type=DbType.Varchar)
	private String userName;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName){
		this.userName=userName;
	}

	@Column(name="user_avatar", type=DbType.Varchar)
	private String userAvatar;
	public String getUserAvatar() {
		return userAvatar;
	}
	public void setUserAvatar(String userAvatar){
		this.userAvatar=userAvatar;
	}
	
	@Column(name="total_beans", type=DbType.Int)
	private Integer totalBeans;
	public Integer getTotalBeans() {
		return totalBeans;
	}
	public void setTotalBeans(Integer totalBeans){
		this.totalBeans=totalBeans;
	}

	@Column(name="deaw_count", type=DbType.Int)
	private Integer deawCount;
	public Integer getDeawCount() {
		return deawCount;
	}
	public void setDeawCount(Integer deawCount){
		this.deawCount=deawCount;
	}

	@Column(name="create_time", type=DbType.DateTime)
	private Long createTime;
	public Long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Long createTime){
		this.createTime=createTime;
	}

	public static GaePrizeTopDDL newExample(){
		GaePrizeTopDDL object=new GaePrizeTopDDL();
		object.setId(null);
		object.setTopType(null);
		object.setUserId(null);
		object.setUserName(null);
		object.setTotalBeans(null);
		object.setDeawCount(null);
		object.setCreateTime(null);
		return object;
	}
}
