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
 * @createDate 2016-11-15 15:58:14
 **/
@Table(name="gae_draw")
public class GaeDrawDDL{
	
	@Id
	@Column(name="id", type=DbType.Varchar)
	private String id;
	
	@Column(name="room_id", type=DbType.Varchar)
	private String roomId;
	
	@Column(name="room_name", type=DbType.Varchar)
	private String roomName;
	
	@Column(name="room_total_beans", type=DbType.Int)
	private Integer roomTotalBeans;
	
	@Column(name="room_draw_ratio", type=DbType.Int)
	private Integer roomDrawRatio;
	
	@Column(name="room_draw_settings", type=DbType.Varchar)
	private String roomDrawSettings;
	
	@Column(name="create_time", type=DbType.DateTime)
	private Long createTime;
	
	@Column(name="status", type=DbType.Int)
	private Integer status=1;
	
	@Column(name="draw_time", type=DbType.DateTime)
	private Long drawTime;
	
	@Column(name="head_count", type=DbType.Int)
	private Integer headCount;
	
	@Column(name="this_head_count", type=DbType.Int)
	private Integer thisHeadCount;
	
	public String getId() {
		return id;
	}
	public void setId(String id){
		this.id=id;
	}
	public String getRoomId() {
		return roomId;
	}
	public void setRoomId(String roomId){
		this.roomId=roomId;
	}
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName){
		this.roomName=roomName;
	}
	public Integer getRoomTotalBeans() {
		return roomTotalBeans;
	}
	public void setRoomTotalBeans(Integer roomTotalBeans) {
		this.roomTotalBeans = roomTotalBeans;
	}
	public Integer getRoomDrawRatio() {
		return roomDrawRatio;
	}
	public void setRoomDrawRatio(Integer roomDrawRatio) {
		this.roomDrawRatio = roomDrawRatio;
	}
	public String getRoomDrawSettings() {
		return roomDrawSettings;
	}
	public void setRoomDrawSettings(String roomDrawSettings) {
		this.roomDrawSettings = roomDrawSettings;
	}
	public Long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Long createTime){
		this.createTime=createTime;
	}
	public Integer getStatus() {
		return status==null?Integer.parseInt("1"):status;
	}
	public void setStatus(Integer status){
		this.status=status==null?Integer.parseInt("1"):status;
	}
	public Long getDrawTime() {
		return drawTime;
	}
	public void setDrawTime(Long drawTime){
		this.drawTime=drawTime;
	}
	public Integer getHeadCount() {
		return headCount;
	}
	public void setHeadCount(Integer headCount){
		this.headCount=headCount;
	}

	public Integer getThisHeadCount() {
		return thisHeadCount;
	}
	public void setThisHeadCount(Integer thisHeadCount) {
		this.thisHeadCount = thisHeadCount;
	}
	public static GaeDrawDDL newExample(){
		GaeDrawDDL object=new GaeDrawDDL();
		object.setId(null);
		object.setRoomId(null);
		object.setRoomName(null);
		object.setCreateTime(null);
		object.setDrawTime(null);
		object.setHeadCount(null);
		return object;
	}
	@Override
	public String toString() {
		return "GaeDrawDDL [id=" + id + ", roomId=" + roomId + ", roomName="
				+ roomName + ", roomTotalBeans=" + roomTotalBeans
				+ ", roomDrawRatio=" + roomDrawRatio + ", roomDrawSettings="
				+ roomDrawSettings + ", createTime=" + createTime + ", status="
				+ status + ", drawTime=" + drawTime + ", headCount="
				+ headCount + "]";
	}
	
}
