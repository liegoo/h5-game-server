package controllers.ucgc;

import java.util.List;
import java.util.Map;

import jws.Logger;
import jws.module.response.news.ListNewsRspDto;
import jws.module.response.news.NewsDto;
import moudles.news.ddl.NewsDDL;
import moudles.news.service.NewsService;
import common.core.UcgcController;
import exception.BusinessException;

/**
 * 资讯新闻
 * 
 * @author fish
 *
 */
public class News extends UcgcController {

	/**
	 * 获取资讯列表
	 */
	public static void listNews() throws BusinessException {
		Map params = getDTO(Map.class);
		int type = (int) Double.parseDouble(params.get("type").toString());
		int page = (int) Double.parseDouble(params.get("page").toString());
		int pageSize = (int) Double.parseDouble(params.get("pageSize").toString());
		ListNewsRspDto result = new ListNewsRspDto();
		List<NewsDto> list = NewsService.listNews4Index(type, 1, page, pageSize);
		result.setNewsdList(list);
		getHelper().returnSucc(result);
	}

	/**
	 * 通过游戏id获取资讯
	 */
	public static void listNewsByGameId() {
		Map params = getDTO(Map.class);
		int gameId = Integer.valueOf(params.get("gameId").toString());
		int type = Integer.valueOf(params.get("type").toString());
		int count = Integer.valueOf(params.get("count").toString());

		if (gameId <= 0) {
			Logger.error("News.listByGameId - invalid gameId %s:", gameId);
			getHelper().returnSucc();
		}

		ListNewsRspDto result = new ListNewsRspDto();
		List<NewsDto> list = NewsService.listByGameId(gameId, type, count);
		result.setNewsdList(list);
		getHelper().returnSucc(result);

	}
}
