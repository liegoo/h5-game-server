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
 * @createDate 2016-11-15 15:50:19
 **/
@Table(name="gae_draw_temp")
public class GaeDrawTempDDL{
	
	
	@Id
	@Column(name="id", type=DbType.Int)
	private Integer id;
	
	@Column(name="draw_id", type=DbType.Varchar)
	private String drawId;
	
	@Column(name="room_id", type=DbType.Varchar)
	private String roomId;
	
	@Column(name="room_name", type=DbType.Varchar)
	private String roomName;
	
	@Column(name="ceate_time", type=DbType.DateTime)
	private Long ceateTime;
	
	@Column(name="last_draw_id", type=DbType.Varchar)
	private String lastDrawId="";
	
	@Column(name="head_count", type=DbType.Int)
	private Integer headCount;
	


	public String getDrawId() {
		return drawId;
	}
	public void setDrawId(String drawId) {
		this.drawId = drawId;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	public Long getCeateTime() {
		return ceateTime;
	}
	public void setCeateTime(Long ceateTime){
		this.ceateTime=ceateTime;
	}
	
	public String getLastDrawId() {
		return lastDrawId==null?String.valueOf(""):lastDrawId;
	}
	public void setLastDrawId(String lastDrawId){
		this.lastDrawId=lastDrawId==null?String.valueOf(""):lastDrawId;
	}
	
	public Integer getHeadCount() {
		return headCount;
	}
	public void setHeadCount(Integer headCount){
		this.headCount=headCount;
	}

	public static GaeDrawTempDDL newExample(){
		GaeDrawTempDDL object=new GaeDrawTempDDL();
		object.setId(null);
		object.setRoomId(null);
		object.setCeateTime(null);
		object.setLastDrawId(null);
		object.setHeadCount(null);
		return object;
	}
}
