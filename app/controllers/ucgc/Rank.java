package controllers.ucgc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import jws.cache.Cache;
import jws.module.response.rank.ListRankRespDto;
import jws.module.response.rank.RankDto;
import moudles.member.ddl.MemberDDL;
import moudles.member.service.MemberService;
import moudles.rank.ddl.RankDDL;
import moudles.rank.service.RankService;
import common.core.UcgcController;

public class Rank extends UcgcController {

	/**
	 * 排行榜
	 */
	public static void listRankByDate() {
		Map params = getDTO(Map.class);
		int gameId = Integer.parseInt(params.get("gameId").toString());
		long beginDate = Long.parseLong(params.get("beginDate").toString());
		long endDate = Long.parseLong(params.get("endDate").toString());
		int page = Integer.parseInt(params.get("page").toString());
		int pageSize = Integer.parseInt(params.get("pageSize").toString());
		ListRankRespDto resp = new ListRankRespDto();
		List<RankDDL> ranks = RankService.listRankByDate(gameId, beginDate, endDate, page, pageSize);
		List<RankDto> list = new ArrayList<RankDto>();
		int index = 0;
		for (RankDDL rank : ranks) {
			RankDto dto = new RankDto();
			dto.setGameId(rank.getGameId());
			dto.setUid(rank.getUid());
			dto.setHappyBean(rank.getHappyBean());
			index++;
			dto.setRank(index);
			MemberDDL member = null; //FIXME use cache
			if (member == null) {
				member = MemberService.getMemberByUid(rank.getUid());
				if (member != null) {
					if(StringUtils.isBlank(member.getNickName())){
						dto.setNickname(member.getMobile());
					}else{
						dto.setNickname(member.getNickName());
					}
					dto.setAvatar(member.getAvatar());
					Cache.set("MemberDDL-" + rank.getUid(), member, "1h");
				}
			}
			list.add(dto);
		}
		resp.setList(list);
		getHelper().returnSucc(resp);
	}

}
