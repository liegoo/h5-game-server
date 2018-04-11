package moudles.activity.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;
/**
 * 
 * @author auto
 * @createDate 2016-07-27 18:14:59
 **/
@Table(name="recharge_presented_activity")
public class RechargePresentedActivityDDL{
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

	@Column(name="title", type=DbType.Varchar)
	private String title="";
	public String getTitle() {
		return title==null?String.valueOf(""):title;
	}
	public void setTitle(String title){
		this.title=title==null?String.valueOf(""):title;
	}

	@Column(name="intro", type=DbType.Varchar)
	private String intro="";
	public String getIntro() {
		return intro==null?String.valueOf(""):intro;
	}
	public void setIntro(String intro){
		this.intro=intro==null?String.valueOf(""):intro;
	}

	@Column(name="type", type=DbType.Int)
	private Integer type;
	public Integer getType() {
		return type;
	}
	public void setType(Integer type){
		this.type=type;
	}

	@Column(name="upper_limit", type=DbType.Int)
	private Integer upperLimit=0;
	public Integer getUpperLimit() {
		return upperLimit==null?Integer.parseInt("0"):upperLimit;
	}
	public void setUpperLimit(Integer upperLimit){
		this.upperLimit=upperLimit==null?Integer.parseInt("0"):upperLimit;
	}

	@Column(name="status", type=DbType.Int)
	private Integer status=1;
	public Integer getStatus() {
		return status==null?Integer.parseInt("1"):status;
	}
	public void setStatus(Integer status){
		this.status=status==null?Integer.parseInt("1"):status;
	}

	@Column(name="remark", type=DbType.Varchar)
	private String remark="";
	public String getRemark() {
		return remark==null?String.valueOf(""):remark;
	}
	public void setRemark(String remark){
		this.remark=remark==null?String.valueOf(""):remark;
	}

	@Column(name="effect_time", type=DbType.DateTime)
	private Long effectTime;
	public Long getEffectTime() {
		return effectTime;
	}
	public void setEffectTime(Long effectTime){
		this.effectTime=effectTime;
	}

	@Column(name="expire_time", type=DbType.DateTime)
	private Long expireTime;
	public Long getExpireTime() {
		return expireTime;
	}
	public void setExpireTime(Long expireTime){
		this.expireTime=expireTime;
	}

	@Column(name="op_name", type=DbType.Varchar)
	private String opName="";
	public String getOpName() {
		return opName==null?String.valueOf(""):opName;
	}
	public void setOpName(String opName){
		this.opName=opName==null?String.valueOf(""):opName;
	}

	@Column(name="update_time", type=DbType.DateTime)
	private Long updateTime;
	public Long getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Long updateTime){
		this.updateTime=updateTime;
	}

	@Column(name="create_time", type=DbType.DateTime)
	private Long createTime;
	public Long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Long createTime){
		this.createTime=createTime;
	}

	public static RechargePresentedActivityDDL newExample(){
		RechargePresentedActivityDDL object=new RechargePresentedActivityDDL();
		object.setId(null);
		object.setTitle(null);
		object.setIntro(null);
		object.setType(null);
		object.setUpperLimit(null);
		object.setStatus(null);
		object.setRemark(null);
		object.setEffectTime(null);
		object.setExpireTime(null);
		object.setOpName(null);
		object.setUpdateTime(null);
		object.setCreateTime(null);
		return object;
	}
}
