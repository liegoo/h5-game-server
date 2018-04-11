package moudles.order.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;
/**
 * 
 * @author auto
 * @createDate 2016-07-27 18:09:29
 **/
@Table(name="zhifu_order")
public class ZhifuOrderDDL{
	@Id
	@GeneratedValue(generationType= GenerationType.Auto)
	@Column(name="id", type=DbType.BigInt)
	private Long id;
	public Long getId() {
		return id;
	}
	public void setId(Long id){
		this.id=id;
	}

	@Column(name="order_id", type=DbType.Varchar)
	private String orderId="";
	public String getOrderId() {
		return orderId==null?String.valueOf(""):orderId;
	}
	public void setOrderId(String orderId){
		this.orderId=orderId==null?String.valueOf(""):orderId;
	}

	@Column(name="game_name", type=DbType.Varchar)
	private String gameName="";
	public String getGameName() {
		return gameName==null?String.valueOf(""):gameName;
	}
	public void setGameName(String gameName){
		this.gameName=gameName==null?String.valueOf(""):gameName;
	}

	@Column(name="product_name", type=DbType.Varchar)
	private String productName="";
	public String getProductName() {
		return productName==null?String.valueOf(""):productName;
	}
	public void setProductName(String productName){
		this.productName=productName==null?String.valueOf(""):productName;
	}

	@Column(name="exchange_rate", type=DbType.Int)
	private Integer exchangeRate=1000;
	public Integer getExchangeRate() {
		return exchangeRate==null?Integer.parseInt("1000"):exchangeRate;
	}
	public void setExchangeRate(Integer exchangeRate){
		this.exchangeRate=exchangeRate==null?Integer.parseInt("1000"):exchangeRate;
	}

	@Column(name="happy_bean", type=DbType.Int)
	private Integer happyBean;
	public Integer getHappyBean() {
		return happyBean;
	}
	public void setHappyBean(Integer happyBean){
		this.happyBean=happyBean;
	}

	@Column(name="presented_happy_bean", type=DbType.Int)
	private Integer presentedHappyBean=0;
	public Integer getPresentedHappyBean() {
		return presentedHappyBean==null?Integer.parseInt("0"):presentedHappyBean;
	}
	public void setPresentedHappyBean(Integer presentedHappyBean){
		this.presentedHappyBean=presentedHappyBean==null?Integer.parseInt("0"):presentedHappyBean;
	}

	@Column(name="amount", type=DbType.Int)
	private Integer amount;
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount){
		this.amount=amount;
	}

	@Column(name="order_type", type=DbType.Int)
	private Integer orderType;
	public Integer getOrderType() {
		return orderType;
	}
	public void setOrderType(Integer orderType){
		this.orderType=orderType;
	}

	@Column(name="pay_way", type=DbType.Int)
	private Integer payWay=0;
	public Integer getPayWay() {
		return payWay==null?Integer.parseInt("0"):payWay;
	}
	public void setPayWay(Integer payWay){
		this.payWay=payWay==null?Integer.parseInt("0"):payWay;
	}

	@Column(name="pay_time", type=DbType.DateTime)
	private Long payTime;
	public Long getPayTime() {
		return payTime;
	}
	public void setPayTime(Long payTime){
		this.payTime=payTime;
	}

	@Column(name="callback_time", type=DbType.DateTime)
	private Long callbackTime;
	public Long getCallbackTime() {
		return callbackTime;
	}
	public void setCallbackTime(Long callbackTime){
		this.callbackTime=callbackTime;
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

	@Column(name="uid", type=DbType.Int)
	private Integer uid;
	public Integer getUid() {
		return uid;
	}
	public void setUid(Integer uid){
		this.uid=uid;
	}

	@Column(name="suid", type=DbType.Int)
	private Integer suid=0;
	public Integer getSuid() {
		return suid==null?Integer.parseInt("0"):suid;
	}
	public void setSuid(Integer suid){
		this.suid=suid==null?Integer.parseInt("0"):suid;
	}

	@Column(name="server_id", type=DbType.Int)
	private Integer serverId=0;
	public Integer getServerId() {
		return serverId==null?Integer.parseInt("0"):serverId;
	}
	public void setServerId(Integer serverId){
		this.serverId=serverId==null?Integer.parseInt("0"):serverId;
	}

	@Column(name="role_id", type=DbType.Varchar)
	private String roleId="";
	public String getRoleId() {
		return roleId==null?String.valueOf(""):roleId;
	}
	public void setRoleId(String roleId){
		this.roleId=roleId==null?String.valueOf(""):roleId;
	}

	@Column(name="ch", type=DbType.Varchar)
	private String ch="";
	public String getCh() {
		return ch==null?String.valueOf(""):ch;
	}
	public void setCh(String ch){
		this.ch=ch==null?String.valueOf(""):ch;
	}

	@Column(name="source_desc", type=DbType.Varchar)
	private String sourceDesc="";
	public String getSourceDesc() {
		return sourceDesc==null?String.valueOf(""):sourceDesc;
	}
	public void setSourceDesc(String sourceDesc){
		this.sourceDesc=sourceDesc==null?String.valueOf(""):sourceDesc;
	}

	@Column(name="pay_status", type=DbType.Int)
	private Integer payStatus=0;
	public Integer getPayStatus() {
		return payStatus==null?Integer.parseInt("0"):payStatus;
	}
	public void setPayStatus(Integer payStatus){
		this.payStatus=payStatus==null?Integer.parseInt("0"):payStatus;
	}

	@Column(name="callback_info", type=DbType.Varchar)
	private String callbackInfo="";
	public String getCallbackInfo() {
		return callbackInfo==null?String.valueOf(""):callbackInfo;
	}
	public void setCallbackInfo(String callbackInfo){
		this.callbackInfo=callbackInfo==null?String.valueOf(""):callbackInfo;
	}

	@Column(name="err_msg", type=DbType.Varchar)
	private String errMsg="";
	public String getErrMsg() {
		return errMsg==null?String.valueOf(""):errMsg;
	}
	public void setErrMsg(String errMsg){
		this.errMsg=errMsg==null?String.valueOf(""):errMsg;
	}

	@Column(name="remark", type=DbType.Varchar)
	private String remark="";
	public String getRemark() {
		return remark==null?String.valueOf(""):remark;
	}
	public void setRemark(String remark){
		this.remark=remark==null?String.valueOf(""):remark;
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

	public static ZhifuOrderDDL newExample(){
		ZhifuOrderDDL object=new ZhifuOrderDDL();
		object.setId(null);
		object.setOrderId(null);
		object.setGameName(null);
		object.setProductName(null);
		object.setExchangeRate(null);
		object.setHappyBean(null);
		object.setPresentedHappyBean(null);
		object.setAmount(null);
		object.setOrderType(null);
		object.setPayWay(null);
		object.setPayTime(null);
		object.setCallbackTime(null);
		object.setCpId(null);
		object.setGameId(null);
		object.setUid(null);
		object.setSuid(null);
		object.setServerId(null);
		object.setRoleId(null);
		object.setCh(null);
		object.setSourceDesc(null);
		object.setPayStatus(null);
		object.setCallbackInfo(null);
		object.setErrMsg(null);
		object.setRemark(null);
		object.setCreateTime(null);
		object.setUpdateTime(null);
		return object;
	}
}
