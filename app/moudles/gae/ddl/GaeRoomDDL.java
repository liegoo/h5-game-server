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
 * @createDate 2016-11-15 14:24:04
 **/
@Table(name="gae_room")
public class GaeRoomDDL{
	@Id
	@Column(name="id", type=DbType.Varchar)
	private String id;
	public String getId() {
		return id;
	}
	public void setId(String id){
		this.id=id;
	}

	@Column(name="name", type=DbType.Varchar)
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name){
		this.name=name;
	}

	@Column(name="total_beans", type=DbType.Int)
	private Integer totalBeans;
	public Integer getTotalBeans() {
		return totalBeans;
	}
	public void setTotalBeans(Integer totalBeans){
		this.totalBeans=totalBeans;
	}

	@Column(name="head_count", type=DbType.Int)
	private Integer headCount;
	public Integer getHeadCount() {
		return headCount;
	}
	public void setHeadCount(Integer headCount){
		this.headCount=headCount;
	}

	@Column(name="draw_ratio", type=DbType.Int)
	private Integer drawRatio;
	public Integer getDrawRatio() {
		return drawRatio;
	}
	public void setDrawRatio(Integer drawRatio){
		this.drawRatio=drawRatio;
	}

	@Column(name="draw_settings", type=DbType.Varchar)
	private String drawSettings;
	public String getDrawSettings() {
		return drawSettings;
	}
	public void setDrawSettings(String drawSettings){
		this.drawSettings=drawSettings;
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

	@Column(name="status", type=DbType.Int)
	private Integer status=1;
	public Integer getStatus() {
		return status==null?Integer.parseInt("0"):status;
	}
	public void setStatus(Integer status){
		this.status=status==null?Integer.parseInt("0"):status;
	}

	public static GaeRoomDDL newExample(){
		GaeRoomDDL object=new GaeRoomDDL();
		object.setId(null);
		object.setName(null);
		object.setTotalBeans(null);
		object.setHeadCount(null);
		object.setDrawRatio(null);
		object.setDrawSettings(null);
		object.setCreateTime(null);
		object.setUpdateTime(null);
		object.setStatus(null);
		return object;
	}
}
