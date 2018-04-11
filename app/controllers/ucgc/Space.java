package controllers.ucgc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jws.module.constants.member.MemberLogOpResult;
import jws.module.response.award.AwardRecordDto;
import jws.module.response.award.ListAwardRecordRspDto;
import jws.module.response.member.GetMemberLogRespDto;
import jws.module.response.member.MemberLogDto;
import moudles.award.service.AwardRecordService;
import moudles.member.ddl.MemberLogDDL;
import moudles.member.service.MemberLogService;
import moudles.wechat.WeChatService;
import utils.DateUtil;
import common.core.UcgcController;

/**
 * 个人中心
 * 
 * @author Coming
 */
public class Space extends UcgcController {

	/**
	 * 开心豆明细(三个月内)
	 */
	public static void listHappyBeanIORecords() {
		
		GetMemberLogRespDto rsp = new GetMemberLogRespDto();
		Map params = getDTO(Map.class);
		int uid = Integer.parseInt(params.get("uid").toString());
		int opType = 0;
		int opResult = MemberLogOpResult.SUCCESS.getType(); //消费成功
		long startTime = DateUtil.getThreeMonthStartEndTime()[0];
		long endTime = DateUtil.getThreeMonthStartEndTime()[1];
		int page = Integer.parseInt(params.get("page").toString());
		int pageSize = Integer.parseInt(params.get("pageSize").toString());
		List<MemberLogDDL> list = MemberLogService.getMemberLogList(uid, opType,opResult, startTime, endTime, page, pageSize);
		List<MemberLogDto> logList = new ArrayList<MemberLogDto>();
		for (MemberLogDDL ddl : list) {
			MemberLogDto log = new MemberLogDto();
			log.setDate(ddl.getOpTime());
			log.setOpType(ddl.getOpType());
			log.setRemark(ddl.getRemark());
			log.setHappyBean(ddl.getHappyBean());
			logList.add(log);
		}
		rsp.setList(logList);
		getHelper().returnSucc(rsp);
	}

	/**
	 * 领奖记录
	 */
	public static void listAwardRecords() {
		ListAwardRecordRspDto resp = new ListAwardRecordRspDto();
		Map params = getDTO(Map.class);
		int uid = Integer.parseInt(params.get("uid").toString());
		int page = Integer.parseInt(params.get("page").toString());
		int pageSize = Integer.parseInt(params.get("pageSize").toString());
		List<AwardRecordDto> list = AwardRecordService.listAwardRecords(uid, page, pageSize);
		resp.setAwardRecordList(list);
		getHelper().returnSucc(resp);
	}

}
