package controllers.ucgc;

import java.util.ArrayList;
import java.util.List;

import jws.module.constants.award.AwardType;
import jws.module.constants.mng.DataStatus;
import jws.module.response.award.AwardDto;
import jws.module.response.award.AwardRecordDto;
import jws.module.response.data4page.GetData4AwardCenterRspDto;
import jws.module.response.data4page.GetData4IndexRspDto;
import jws.module.response.data4page.GetData4IndexRspDto.Banner;
import jws.module.response.news.NewsDto;
import moudles.award.service.AwardService;
import moudles.news.service.NewsService;

import common.core.UcgcController;

import exception.BusinessException;

public class ShowDatas extends UcgcController {

	/**
	 * 获取首页数据
	 */
	public static void getData4Index() {
		
		GetData4IndexRspDto rsp = new GetData4IndexRspDto();
		List<Banner> bannerList = null;// 需调用外部系统获取
		List<AwardRecordDto> awardRecordList = AwardService.listAwardRecords4Tmp();
		List<NewsDto> newsList = NewsService.listNews4Index(0,1,0,10);
		
		rsp.setBannerList(bannerList);
		rsp.setAwardRecordList(awardRecordList);
		rsp.setNewsList(newsList);
		getHelper().returnSucc(rsp);
	}

	/**
	 * 获取领奖中心首页数据
	 */
	public static void getData4AwardCenter() throws BusinessException {
		GetData4AwardCenterRspDto rsp = new GetData4AwardCenterRspDto();
		
		// 前期先直接读临时表的数据
		List<AwardRecordDto> awardRecordList = AwardService.listAwardRecords4Tmp();
		int[] types = new int[] {
				AwardType.GOODS.getType(),
				AwardType.COUPON.getType(),
				AwardType.QCOIN.getType(),
				AwardType.JDCARD.getType(),
				AwardType.VIRTUAL.getType()
				};
		int scope = 1;
		List<AwardDto> awardList = AwardService.getList4AwardCenter(DataStatus.ONLINE.getStatus(), types, false, scope);
		rsp.setAwardRecordList(awardRecordList);
		rsp.setAwardList(awardList);
		getHelper().returnSucc(rsp);
	}
}
