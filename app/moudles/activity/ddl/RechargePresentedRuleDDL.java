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
 * @createDate 2016-07-27 18:15:08
 **/
@Table(name="recharge_presented_rule")
public class RechargePresentedRuleDDL{
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

	@Column(name="rpa_id", type=DbType.Int)
	private Integer rpaId;
	public Integer getRpaId() {
		return rpaId;
	}
	public void setRpaId(Integer rpaId){
		this.rpaId=rpaId;
	}

	@Column(name="recharge_bean", type=DbType.Int)
	private Integer rechargeBean;
	public Integer getRechargeBean() {
		return rechargeBean;
	}
	public void setRechargeBean(Integer rechargeBean){
		this.rechargeBean=rechargeBean;
	}

	@Column(name="presented_bean", type=DbType.Int)
	private Integer presentedBean;
	public Integer getPresentedBean() {
		return presentedBean;
	}
	public void setPresentedBean(Integer presentedBean){
		this.presentedBean=presentedBean;
	}

	public static RechargePresentedRuleDDL newExample(){
		RechargePresentedRuleDDL object=new RechargePresentedRuleDDL();
		object.setId(null);
		object.setRpaId(null);
		object.setRechargeBean(null);
		object.setPresentedBean(null);
		return object;
	}
}
