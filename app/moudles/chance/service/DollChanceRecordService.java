package moudles.chance.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jws.Jws;
import jws.Logger;
import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import jws.dal.sqlbuilder.Sort;
import jws.module.constants.doll.ChanceReadStatus;
import jws.module.constants.doll.ChanceStatus;
import jws.module.constants.doll.ChanceType;
import jws.module.constants.member.MemberLogOpResult;
import jws.module.constants.member.MemberLogOpType;
import moudles.chance.ddl.DollChanceRecordDDL;
import moudles.member.ddl.MemberDDL;
import moudles.member.service.MemberLogService;
import moudles.member.service.MemberService;
import utils.DaoUtil;
import utils.DateUtil;
import utils.JsonToMap;

import com.google.gson.JsonObject;
import common.dao.QueryConnectionHandler;

import constants.GlobalConstants;
import constants.SelfGame;
import exception.BusinessException;

public class DollChanceRecordService {

	private static String baseUrl = Jws.configuration.getProperty("h5game.web.inex", "http://game.8868.cn/");

	/**
	 * 创建夹娃娃赠送记录
	 */
	public static boolean create(DollChanceRecordDDL ddl) {
		return Dal.insert(ddl) > 0;
	}

	/**
	 * 获取未读特权
	 * 
	 * @param uid
	 * @param type
	 * @return
	 */
	public static int getUnreadCount(int uid, int type[]) {
		Condition cond = new Condition("DollChanceRecordDDL.uid", "=", uid);
		cond.add(new Condition("DollChanceRecordDDL.isRead", "=", ChanceReadStatus.UNREAD.getValue()), "and");
		if (type != null && type.length > 0) {
			cond.add(new Condition("DollChanceRecordDDL.type", "in", type), "and");
		}
		return Dal.count(cond);
	}

	/**
	 * 更新特权机会阅读状态
	 * 
	 * @param uid
	 * @return
	 */
	public static boolean updateReadStatus(int uid, int type[]) {
		Condition cond = new Condition("DollChanceRecordDDL.uid", "=", uid);
		cond.add(new Condition("DollChanceRecordDDL.isRead", "=", ChanceReadStatus.UNREAD.getValue()), "and");
		if (type != null && type.length > 0) {
			cond.add(new Condition("DollChanceRecordDDL.type", "in", type), "and");
		}
		DollChanceRecordDDL record = new DollChanceRecordDDL();
		record.setIsRead(ChanceReadStatus.READ.getValue());
		record.setUpdateTime(System.currentTimeMillis());
		return Dal.update(record, "DollChanceRecordDDL.updateTime,DollChanceRecordDDL.isRead", cond) > 0;
	}

	/**
	 * 减少特权机会
	 * 
	 * @param uid
	 * @return
	 */
	public static boolean minusChance(int uid) {
		List<DollChanceRecordDDL> chances = list(uid);
		DollChanceRecordDDL ch = null;
		if (chances != null && chances.size() > 0) {
			ch = chances.get(0);
		} else {
			return false;
		}
		ch.setRemain(ch.getRemain() - 1);
		ch.setIsRead(ChanceReadStatus.READ.getValue());
		ch.setUpdateTime(System.currentTimeMillis());
		return update(ch);
	}

	/**
	 * 更新夹娃娃赠送记录
	 */
	public static boolean update(DollChanceRecordDDL ddl) {
		Condition cond = new Condition("DollChanceRecordDDL.id", "=", ddl.getId());
		String updated = "DollChanceRecordDDL.isRead,DollChanceRecordDDL.chance,DollChanceRecordDDL.title,DollChanceRecordDDL.remain,DollChanceRecordDDL.updateTime";
		return Dal.update(ddl, updated, cond) > 0;
	}

	/**
	 * 通过uid获取赠送列表
	 */
	public static List<DollChanceRecordDDL> list(int uid) {
		StringBuilder sql = new StringBuilder("select * from doll_chance_record where uid = ");
		sql.append(uid);
		sql.append(" and status = ");
		sql.append(ChanceStatus.AVAILABLE.getValue());
		sql.append(" and remain > 0 ");
		sql.append(" and (UNIX_TIMESTAMP(expire) * 1000 >");
		sql.append(System.currentTimeMillis());
		sql.append(" or expire is null)");

		DollChanceRecordDDL dto = new DollChanceRecordDDL();
		List<DollChanceRecordDDL> list = Dal.getConnection(GlobalConstants.dbSource, new QueryConnectionHandler(dto, sql.toString()));
		return list;
	}

	/**
	 * 获取可用特权次数
	 * 
	 * @param uid
	 * @return
	 */
	public static int getChance(int uid) {
		StringBuffer sql = new StringBuffer("select sum(remain) from doll_chance_record where uid = ");
		sql.append(uid);
		sql.append(" and status = ");
		sql.append(ChanceStatus.AVAILABLE.getValue());
		sql.append(" and (UNIX_TIMESTAMP(expire) * 1000 >");
		sql.append(System.currentTimeMillis());
		sql.append(" or expire is null)");
		int count = Dal.executeCount(DollChanceRecordDDL.class, sql.toString());
		return count;
	}

	/**
	 * 获取特权机会(做任务赠送)
	 * 
	 * @param uid
	 * @return
	 */
	public static int getTrails(int uid) {
		StringBuffer sql = new StringBuffer("select sum(remain) from doll_chance_record where uid = ");
		sql.append(uid);
		sql.append(" and status = ");
		sql.append(ChanceStatus.AVAILABLE.getValue());
		sql.append(" and UNIX_TIMESTAMP(create_time)*1000 >= ");
		sql.append(DateUtil.getTodayStartEndTime()[0]);
		sql.append(" and UNIX_TIMESTAMP(create_time)*1000 <= ");
		sql.append(DateUtil.getTodayStartEndTime()[1]);
		sql.append(" and type = ");
		sql.append(ChanceType.PLAY_GAME.getType());
		sql.append(" and is_read = ");
		sql.append(ChanceReadStatus.UNREAD.getValue());
		int count = Dal.executeCount(DollChanceRecordDDL.class, sql.toString());
		return count;
	}

	/**
	 * 创建特权机会
	 * 
	 * @param params
	 * @return
	 */
	public static Map<String, String> createChance(Map params) {

		Logger.info("DollChanceRecordService.createChance --> params: %s", params);

		String title = params.get("title").toString();
		int uid = Integer.valueOf(params.get("uid").toString());
		String zhifuOrderId = params.get("zhifuOrderId").toString();
		String channel = params.get("caller").toString().concat("order");

		MemberDDL member = null;
		try {
			member = MemberService.getOrCreate(uid);
		} catch (BusinessException e) {
			Logger.error(e.getMessage());
		}

		if (member == null) {
			Logger.error("获取or创建用户失败");
			return null;
		}

		JsonObject data = JsonToMap.parseJson(params.get("gameExtra").toString());
		double price = data.get("price").getAsDouble();
		int amount = data.get("gameLevel").getAsInt();
		amount = amount > 0 ? amount : 1;

		// 创建特权机会
		DollChanceRecordDDL chance = new DollChanceRecordDDL();
		chance.setChance(amount);
		chance.setRemain(amount);
		chance.setUid(uid);
		chance.setType(ChanceType.BUNDLE_SALES.getType());
		chance.setIsRead(ChanceReadStatus.UNREAD.getValue()); // 未读
		chance.setStatus(ChanceStatus.AVAILABLE.getValue()); // 可用
		chance.setTitle(title);
		chance.setRemark("捆绑销售");
		chance.setChannel(channel);
		chance.setZhifuOrderId(zhifuOrderId);
		chance.setCreateTime(System.currentTimeMillis());
		chance.setUpdateTime(System.currentTimeMillis());
		chance.setExpire(null);
		if (!create(chance)) {
			Logger.error("创建特权机会失败");
			return null;
		}

		// 创建游戏-订单记录
		// GameOrderDDL gameOrder = new GameOrderDDL();
		// gameOrder.setGameId(SelfGame.GAME_DOLL.getGameId());
		// gameOrder.setZhifuOrderId(zhifuOrderId);
		// GameOrderService.create(gameOrder);

		// 只记录开心豆明细，不操作开心豆
		int balance = member.getHappyBean() + member.getHappyBeanFromOp();
		if (price > 0) {
			createMemberLog(uid, balance, price, title, channel, zhifuOrderId);
		}

		int count = getChance(uid);
		return formatChance(title, count, channel);
	}

	/**
	 * 创建操作开心豆明细
	 * 
	 * @param uid
	 * @param balance
	 * @param price
	 * @param title
	 * @param ch
	 */
	private static void createMemberLog(int uid, int balance, double price, String title, String ch, String zhifuOrderId) {
		int rate = Integer.valueOf(Jws.configuration.getProperty("rmb.rate", "1000"));
		int happyBean = (int) (price * rate);
		int opResult = MemberLogOpResult.SUCCESS.getType();
		int newBalance = balance + happyBean;
		Map params = new HashMap();
		params.put("gameId", SelfGame.GAME_DOLL.getGameId());
		params.put("happyBean", String.valueOf(happyBean));
		params.put("channel", ch);
		params.put("billId", zhifuOrderId);
		params.put("remark", "充值-" + title);
		MemberLogService.createMemberLog(uid, opResult, MemberLogOpType.RECHARGE.getType(), newBalance, params);
		params.put("remark", "夹娃娃-" + title);
		MemberLogService.createMemberLog(uid, opResult, MemberLogOpType.CONSUME.getType(), balance, params);
	}

	/**
	 * 通过订单号获取赠送记录
	 */
	public static DollChanceRecordDDL getByOrderId(String zhifuOrderId) {
		Condition cond = new Condition("DollChanceRecordDDL.zhifuOrderId", "=", zhifuOrderId);
		List<DollChanceRecordDDL> list = Dal.select(DaoUtil.genAllFields(DollChanceRecordDDL.class), cond, null, 0, 1);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 获取特权次数
	 * 
	 * @param params
	 * @return
	 */
	public static Map getChance(Map params) {
		int uid = Double.valueOf(params.get("uid").toString()).intValue();
		String zhifuOrderId = params.get("zhifuOrderId").toString();
		DollChanceRecordDDL chance = getByOrderId(zhifuOrderId);
		if (chance == null) {
			return null;
		}
		int count = getChance(uid);
		return formatChance(chance.getTitle(), count, chance.getChannel());
	}

	/**
	 * 格式化返回结果
	 * 
	 * @param title
	 * @param count
	 * @param ch
	 * @return
	 */
	public static Map<String, String> formatChance(String title, int count, String ch) {
		Map<String, String> result = new HashMap<String, String>();
		String content = "您的夹娃娃机会已用完，点击查看中奖情况，继续抽奖吧！";
		String status = "已用完";
		if (count > 0) {
			content = String.format("您累计还剩%d次夹娃娃机会，快去一展身手拿大奖！", count);
			status = "待抽奖";
		}

		String url = String.format("%sDoll/index?isDollTiro=1", baseUrl);
		result.put("title", title);
		result.put("content", content);
		result.put("status", status);
		result.put("url", url);
		return result;
	}
}
