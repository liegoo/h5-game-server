package controllers.ucgc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;

import common.core.UcgcController;
import constants.CouponStatus;
import exception.BusinessException;
import externals.CommonService;
import externals.DicService;
import externals.coupon.CouponService;
import jws.Logger;
import jws.module.response.award.AwardDto;
import jws.module.response.award.GetAwardRecordDollDto;
import jws.module.response.award.ListAwardsRspDto;
import moudles.award.ddl.AwardDDL;
import moudles.award.ddl.AwardDetailDDL;
import moudles.award.ddl.AwardRecordDollDDL;
import moudles.award.service.AwardDetailService;
import moudles.award.service.AwardRecordDollService;
import moudles.award.service.AwardService;
import moudles.game.service.GameService;
import utils.CopyDDLUtil;

public class Award extends UcgcController {

	/**
	 * 获取奖品列表
	 */
	public static void listAwards() throws BusinessException {
		ListAwardsRspDto rsp = new ListAwardsRspDto();
		Map params = getDTO(Map.class);
		int status = Integer.parseInt(params.get("status").toString());
		int scope = Integer.parseInt(params.get("scope").toString());
		int type = Integer.parseInt(params.get("type").toString());
		int isShowIndex = Integer.parseInt(params.get("isShowIndex").toString());
		int[] types = null;
		if (type > 0) {
			types = new int[] { type };
		}
		boolean isHasStored = Boolean.valueOf(params.get("isHasStored").toString());
		List<AwardDto> awardList = AwardService.listAward(status, types, isHasStored, scope, isShowIndex);
		rsp.setList(awardList);
		getHelper().returnSucc(rsp);
	}

	/**
	 * 通过id获取奖品信息
	 */
	public static void getAwardById() throws BusinessException {
		Map params = getDTO(Map.class);
		int awardId = Integer.parseInt(params.get("awardId").toString());
		AwardDDL ddl = AwardService.getAwardById(awardId);
		if(ddl == null){
			getHelper().returnSucc(null);
		}
		AwardDto dto = new AwardDto();
		CopyDDLUtil copy = new CopyDDLUtil(ddl, dto);
		copy.copy();
		if(dto.getGameId() != null){
			Record game = GameService.getCouponGame(dto.getGameId()+"");
			if(game != null){
				dto.setGameName(game.getStr("game_name"));
			}
		}
		Integer price = CommonService.getCouponPrice(dto.getName());
		if(price != null){
			dto.setPrice(price);
			String gameId = "";
			if(dto.getGameId() != null){
				gameId = dto.getGameId() + "";
			}
			Double discount = DicService.service.getReferenceDiscount(gameId);
			if(discount != null){
				BigDecimal bg = new BigDecimal(price * discount / 10.0).setScale(2, RoundingMode.HALF_UP);
				dto.setReferencePrice(bg.doubleValue());
			}
		}
		getHelper().returnSucc(dto);
	}

	/**
	 * 创建领奖记录
	 */
	public static void createAwardRecord() throws BusinessException {
		Map params = getDTO(Map.class);
		Map<String, String> result = new HashMap<String, String>();
		result = AwardService.createAwardRecord(params);
		getHelper().returnSucc(result);
	}

	/**
	 * 通过id获取夹娃娃记录
	 */
	public static void getAwardRecordDollById() throws BusinessException {
		GetAwardRecordDollDto dto = new GetAwardRecordDollDto();
		Map params = getDTO(Map.class);
		int awardRecordDollId = Integer.parseInt(params.get("awardRecordDollId").toString());
		AwardRecordDollDDL ddl = AwardRecordDollService.getById(awardRecordDollId);
		if (ddl != null) {
			dto.setId(ddl.getId());
			dto.setHit(ddl.getHit());
			dto.setQq(ddl.getQq());
			dto.setAddr(ddl.getAddr());
			dto.setMobile(ddl.getMobile());
			dto.setUserName(ddl.getUserName());
			dto.setGameUid(ddl.getGameUid());
			dto.setGameId(ddl.getGameId());
			dto.setGameName(ddl.getGameName());
			dto.setStatus(ddl.getStatus());
			dto.setTime(ddl.getCreateTime());
			dto.setHappyBean(ddl.getHappyBean());
			dto.setOrderId(ddl.getZhifuOrderId());
			dto.setUid(Integer.parseInt(ddl.getUid()));
			dto.setAuditStatus(ddl.getAuditStatus());
			dto.setAuditRemark(ddl.getAuditRemark());
			dto.setCardNo(ddl.getCardNo());
			dto.setCardPwd(ddl.getCardPwd());
			dto.setDeliverCompany(ddl.getDeliverCompany());
			dto.setDeliverNo(ddl.getDeliverNo());
			dto.setExchange(ddl.getExchange());
			AwardDDL award = AwardService.getAwardById(ddl.getAwardId());
			if (award != null) {
				dto.setAwardId(award.getId());
				dto.setAwardImgUrl(award.getImgUrl());
				dto.setAwardName(award.getName());
			}
		}
		getHelper().returnSucc(dto);
	}

	/**
	 * 更新领奖信息
	 */
	public static void updateAwardRecordDoll() {
		Map params = getDTO(Map.class);
		boolean flag = false;
		/**
		 * 更新夹娃娃中奖记录的之前先同步基础状态
		 * */	
		String couponId = "";//代金券编号
		String auditMemo = "";//备注
		int awardId = 0 ;//奖品编号
		if(params.get("awardRecordDollId") == null){
			Logger.error("娃娃记录编号为空");
			getHelper().returnSucc(flag);
		}
		Integer awardRecordDollId = Integer.parseInt(params.get("awardRecordDollId").toString());
		AwardRecordDollDDL awardRecordDollDDL = AwardRecordDollService.getById(awardRecordDollId);
		if(null == awardRecordDollDDL){
			Logger.error("获取夹娃娃记录信息失败,awardRecordDollId =%s",awardRecordDollId);
			getHelper().returnSucc(flag);
		}
		couponId = awardRecordDollDDL.getBaseCouponId();
		awardId = awardRecordDollDDL.getAwardId();
		if (params.containsKey("auditRemark")) {
			auditMemo = (String) params.get("auditRemark");
		}		
		try {
			AwardDDL award = AwardService.getAwardById(awardId);	
			// 同步状态
			if(award.getType() == 4){//判断是代金券
				Logger.info("同步代金券信息：couponId=%s,auditMemo=%s", couponId,auditMemo);
				String result = CouponService.audit(couponId, CouponStatus.TO_AUDIT.getStatus(), auditMemo);
				if(!"".equals(result)){// 返回值如果不是为空字符串 则同步代金券失败
					Logger.error("同步代金券失败:couponId = %s,auditMemo=%s",couponId,auditMemo);
					getHelper().returnSucc(flag);
				}
			}
		} catch (BusinessException e) {
			e.printStackTrace();
			Logger.error("获取奖品记录失败,奖品编号是%s",awardId);
			getHelper().returnSucc(flag);	
		}
		flag = AwardRecordDollService.update(params);
		getHelper().returnSucc(flag);
	}

	/**
	 * 更新奖品明细
	 */
	public static void updateAwardDetail() {
		Map params = getDTO(Map.class);
		int id = Integer.parseInt(params.get("awardDetailId").toString());
		int status = Integer.parseInt(params.get("status").toString());
		AwardDetailDDL awardDetail = AwardDetailService.getById(id);
		boolean flag = false;
		if (awardDetail != null) {
			awardDetail.setStatus(status);
			flag = AwardDetailService.updateAwardDetail(awardDetail);
		}
		getHelper().returnSucc(flag);
	}
	/**
	 * 激活代金卷
	 * */
	public static void activite(){
		Map params = getDTO(Map.class);
		if(params.get("couponId") == null || params.get("gameId") == null || 
				params.get("gameName") == null || params.get("activiteAccount") == null){
			getHelper().returnError(-10, "参数有误！");
		}
		
		String couponId = params.get("couponId").toString();
		Integer gameId = Integer.parseInt(params.get("gameId").toString());
		String gameName = params.get("gameName").toString();
		String activiteAccount = params.get("activiteAccount").toString();
		Logger.info("代金券信息.gameId=%s,优惠卷编号 =%s,游戏名字 = %s,activiteAccount =%s",gameId,couponId,gameName,activiteAccount);
		String result = CouponService.activite(couponId, gameId, gameName, activiteAccount);
		if(!"".equals(result)){
			getHelper().returnError(-100, result);
		}
		getHelper().returnSucc(result);
	}
	/**
	 * 同步代金卷
	 * */
	public static void audit(){
		Map params = getDTO(Map.class);
		if(params.get("couponId") == null || params.get("status") == null || params.get("auditMemo") == null){
			getHelper().returnError(-10, "参数有误！");
		}		
		String couponId = params.get("couponId").toString();
		Integer status = Integer.parseInt(params.get("status").toString());
		String auditMemo = params.get("auditMemo").toString();
		String result = CouponService.audit(couponId, status, auditMemo);
		if(!"".equals(result)){
			Logger.error("代金券状态同步失败.优惠卷编号 =%s,状态 = %s,审核备注 =%s",couponId,status,auditMemo);
			getHelper().returnError(-100, result);
		}
		getHelper().returnSucc(result);
	}
}
