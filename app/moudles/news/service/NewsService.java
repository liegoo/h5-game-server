package moudles.news.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jws.Logger;
import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import jws.dal.sqlbuilder.Sort;
import jws.module.response.news.NewsDto;
import moudles.news.ddl.NewsDDL;
import utils.DaoUtil;
import constants.GlobalConstants;

public class NewsService {

	/**
	 * 根据类型获取资讯列表
	 * 
	 * @param type
	 *            1-名人堂，2-攻略， 小于等于0-全部
	 * @param size
	 *            读取条数，-1为不限制
	 * @return
	 */
	public static List<NewsDto> listNews4Index(int type, int isIndexShow, int page, int pageSize) {
		if (page <= 0) {
			page = 1;
		}
		if (pageSize <= 0) {
			pageSize = 10;
		}

		List<NewsDto> newsList = new ArrayList<NewsDto>();
		Condition cond = new Condition("NewsDDL.id", ">", 0);
		cond.add(new Condition("NewsDDL.status", "=", 1), "AND");
		if (type > 0) {
			cond.add(new Condition("NewsDDL.type", "=", type), "AND");
		}
		if (isIndexShow != 0) {
			cond.add(new Condition("NewsDDL.isIndexShow", "=", isIndexShow), "AND");
		}
		List<NewsDDL> list = Dal.select(DaoUtil.genAllFields(NewsDDL.class), cond, new Sort("NewsDDL.sort", true), (page - 1) * pageSize, pageSize);

		if (null == list || list.size() <= 0) {
			return null;
		}
		for (NewsDDL ddl : list) {
			NewsDto news = new NewsDto();
			news.setTitle(ddl.getTitle());
			news.setIntro(ddl.getIntro());
			news.setImgUrl(ddl.getImgUrl());
			news.setDetailUrl(ddl.getDetailUrl());
			news.setType(ddl.getType());
			news.setNoticeId(ddl.getNoticeId());
			newsList.add(news);
		}

		return newsList;
	}

	/**
	 * 获取资讯列表
	 * 
	 * @param gameId
	 * @param type
	 * @param count
	 * @return
	 */
	public static List<NewsDto> listByGameId(int gameId, int type, int count) {
		StringBuilder sql = new StringBuilder("select * from news where find_in_set('");
		sql.append(gameId);
		sql.append("', games)");
		if (type > 0) {
			sql.append(" and type = ");
			sql.append(type);
		}
		sql.append(" and status = 1");
		sql.append(" order by `sort`");
		if (count > 0) {
			sql.append(" limit 0,");
			sql.append(count);
		}
		ResultSet result = Dal.executeQuery(GlobalConstants.dbSource, sql.toString());
		List<NewsDto> newsList = new ArrayList<NewsDto>();
		try {
			if (result != null) {
				while (result.next()) {
					NewsDto dto = new NewsDto();
					dto.setIntro(result.getString("intro"));
					dto.setTitle(result.getString("title"));
					dto.setImgUrl(result.getString("img_url"));
					dto.setDetailUrl(result.getString("detail_url"));
					dto.setType(result.getInt("type"));
					dto.setNoticeId(result.getInt("notice_id"));
					newsList.add(dto);
				}
			}
		} catch (SQLException e) {
			Logger.error(e.getMessage(), "");
		} finally {
			if (result != null) {
				try {
					result.close();
				} catch (SQLException e) {
					Logger.error(e.getMessage(), "");
				}
			}
		}
		return newsList;
	}
}
