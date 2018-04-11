package D:.workspace.h5-game-server.app.moudles.award.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;
/**
 * 
 * @author auto
 * @createDate 2018-03-29 16:14:13
 **/
@Table(name="daily")
public class DailyDDL{
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

	@Column(name="time", type=DbType.DateTime)
	private Long time;
	public Long getTime() {
		return time;
	}
	public void setTime(Long time){
		this.time=time;
	}

	@Column(name="chongzhi_total", type=DbType.Double)
	private Double chongzhiTotal;
	public Double getChongzhiTotal() {
		return chongzhiTotal;
	}
	public void setChongzhiTotal(Double chongzhiTotal){
		this.chongzhiTotal=chongzhiTotal;
	}

	@Column(name="bean_pay_amount", type=DbType.Double)
	private Double beanPayAmount;
	public Double getBeanPayAmount() {
		return beanPayAmount;
	}
	public void setBeanPayAmount(Double beanPayAmount){
		this.beanPayAmount=beanPayAmount;
	}

	@Column(name="doll_cost", type=DbType.Double)
	private Double dollCost;
	public Double getDollCost() {
		return dollCost;
	}
	public void setDollCost(Double dollCost){
		this.dollCost=dollCost;
	}

	@Column(name="exchange_cost", type=DbType.Double)
	private Double exchangeCost;
	public Double getExchangeCost() {
		return exchangeCost;
	}
	public void setExchangeCost(Double exchangeCost){
		this.exchangeCost=exchangeCost;
	}

	@Column(name="gross_margin", type=DbType.Double)
	private Double grossMargin;
	public Double getGrossMargin() {
		return grossMargin;
	}
	public void setGrossMargin(Double grossMargin){
		this.grossMargin=grossMargin;
	}

	@Column(name="people_number", type=DbType.Int)
	private Integer peopleNumber;
	public Integer getPeopleNumber() {
		return peopleNumber;
	}
	public void setPeopleNumber(Integer peopleNumber){
		this.peopleNumber=peopleNumber;
	}

	public static DailyDDL newExample(){
		DailyDDL object=new DailyDDL();
		object.setId(null);
		object.setTime(null);
		object.setChongzhiTotal(null);
		object.setBeanPayAmount(null);
		object.setDollCost(null);
		object.setExchangeCost(null);
		object.setGrossMargin(null);
		object.setPeopleNumber(null);
		return object;
	}
}
