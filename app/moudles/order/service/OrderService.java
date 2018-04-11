package moudles.order.service;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Strings;
import com.google.gson.Gson;

import constants.MessageCode;
import constants.SelfGame;
import exception.BusinessException;
import externals.pay.PayCenterService;
import jws.Jws;
import jws.Logger;
import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import jws.http.Request;
import jws.http.Response;
import jws.module.constants.member.MemberLogOpResult;
import jws.module.constants.member.MemberLogOpType;
import jws.module.constants.order.OrderType;
import jws.module.constants.order.PayStatus;
import jws.mvc.Http;
import moudles.activity.ddl.RechargePresentedRuleDDL;
import moudles.activity.service.ActivityService;
import moudles.member.service.MemberLogService;
import moudles.member.service.MemberService;
import moudles.order.ddl.ZhifuOrderDDL;
import moudles.wechat.WeChatService;
import utils.ChecksumHelper;
import utils.DaoUtil;
import utils.DateUtil;
import utils.DistributeCacheLock;
import utils.KeyPointLogUtil;

public class OrderService {
	private static DistributeCacheLock lock = DistributeCacheLock.getInstance();

	/**
	 * 参数具体见api定义
	 * 
	 * @param params
	 * @return map key={orderId,result}
	 * @throws BusinessException
	 */
	public static String createOrder(String productName, int happyBean, int presentedHappyBean, OrderType orderType,
			MemberLogOpType opType, int cpId, String backUrl, int gameId, int uid, int suid, String ch,
			String sourceDesc, String remark, String orderId){
		Map params = new HashMap();
		params.put("productName", productName);
		params.put("happyBean", String.valueOf(happyBean));
		params.put("presentedHappyBean", String.valueOf(presentedHappyBean));
		params.put("orderType", String.valueOf(orderType.getType()));
		params.put("opType", String.valueOf(opType.getType()));
		params.put("cpId", String.valueOf(cpId));
		params.put("backUrl", backUrl);
		params.put("gameId", String.valueOf(gameId));
		params.put("uid", String.valueOf(uid));
		params.put("suid", String.valueOf(suid));
		params.put("ch", ch);
		params.put("sourceDesc", sourceDesc);
		params.put("remark", remark);
		params.put("orderId", orderId);
		Map<String, String> orderResult = null;
		try {
			orderResult = createOrder(params);
		} catch (BusinessException e) {
		}
		if(null == orderResult || StringUtils.isBlank(orderResult.get("orderId").toString()) || 
				"FAIL".equals(orderResult.get("result"))){
			return null;
		}
		return orderResult.get("orderId").toString();
	}
	public static Map<String, String> createOrder(Map<String, String> params) throws BusinessException {
		Map<String, String> result = new HashMap<String, String>();
		result.put("orderId", "");
		result.put("result", "");
		if (params == null || params.size() == 0) {
			throw new BusinessException(MessageCode.ERROR_CODE_500, "参数校验不通过");
		}
		if (!params.containsKey("productName") || !params.containsKey("happyBean") || !params.containsKey("orderType") || !params.containsKey("uid")) {
			throw new BusinessException(MessageCode.ERROR_CODE_500, "参数校验不通过");
		}

		int uid = (int) Double.parseDouble(params.get("uid"));
		String lockKey = "Order-" + uid;

		try {

			if (!lock.tryCacheLock(lockKey, "", "5s")) {
				throw new BusinessException(MessageCode.ERROR_CODE_500, "重复下单");
			}
			String orderId = params.get("orderId");
			if(Strings.isNullOrEmpty(orderId)){
				int rnd = new Random().nextInt(899999) + 100000; // 生成随机六位数
				orderId = "H5GMORD-" + System.currentTimeMillis() + "-" + rnd;
			}
			result.put("orderId", orderId);
			int rmbRate = (int) Double.parseDouble(Jws.configuration.getProperty("rmb.rate"));
			int orderType = (int) Double.parseDouble(params.get("orderType"));
			int happyBean = (int) Double.parseDouble(params.get("happyBean"));

			if (happyBean < 0) {
				Logger.error("开心豆无效: %s,", happyBean);
				throw new BusinessException(MessageCode.ERROR_CODE_500, "开心豆无效");
			}

			int amount = (happyBean * 100) / rmbRate; // 得到分

			ZhifuOrderDDL order = new ZhifuOrderDDL();
			order.setOrderId(orderId);
			order.setExchangeRate(rmbRate);
			order.setAmount(amount);
			order.setHappyBean(happyBean);
			order.setUid(uid);
			order.setCreateTime(System.currentTimeMillis());
			order.setUpdateTime(System.currentTimeMillis());
			if (params.containsKey("presentedHappyBean")) {
				order.setPresentedHappyBean((int) Double.parseDouble(params.get("presentedHappyBean")));
			}
			order.setOrderType(orderType);
			if (params.containsKey("cpId")) {
				order.setCpId((int) Double.parseDouble(params.get("cpId")));
			}
			if (params.containsKey("gameId")) {
				order.setGameId((int) Double.parseDouble(params.get("gameId")));
			}
			if (params.containsKey("suid")) {
				order.setSuid((int) Double.parseDouble(params.get("suid")));
			}

			if (Http.Request.current() != null) {
				order.setCh(Http.Request.current().args.get("channel") == null ? "" : Http.Request.current().args.get("channel").toString());
			}

			if (params.containsKey("sourceDesc")) {
				order.setSourceDesc(params.get("sourceDesc"));
			}
			if (params.containsKey("remark")) {
				order.setRemark(params.get("remark"));
			}
			if (params.containsKey("productName")) {
				order.setProductName(params.get("productName"));
			}

			String html = "";
			if (orderType == OrderType.RECHARGE.getType()) {// 生成充值页面
				Map map = new TreeMap();
				map.put("busi_code", Jws.configuration.getProperty("pay.busiCode"));
				map.put("order_id", orderId);
				map.put("create_time", DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
				map.put("expire_time", DateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
				String totalAmount = String.format("%.2f", amount / (double) 100);
				map.put("total_amount", totalAmount);
				map.put("goods_name", params.get("productName"));
				map.put("unit_price", totalAmount);
				map.put("goods_count", "1");
				map.put("goods_area", "");
				map.put("buyer_uid", String.valueOf(uid));
				map.put("seller_uid", Jws.configuration.getProperty("pay.sellerUid"));
				map.put("remark", params.get("remark") == null ? "" : params.get("remark"));
				map.put("strategy_id", Jws.configuration.getProperty("pay.strategyId"));
				map.put("cb_front_url", params.get("backUrl"));
				map.put("cb_backend_url", Jws.configuration.getProperty("pay.notify_url"));
				map.put("channel_id", "0");
				map.put("product_type_group_id", "0");

				String vcode = ChecksumHelper.getChecksum(map, Jws.configuration.getProperty("pay.secretKey"));
				map.put("cmd", "payWhitRedeemCode");
				map.put("vcode", vcode);
				map.put("t", System.currentTimeMillis());
				html = doGet(Jws.configuration.getProperty("pay.url"), map, vcode);
				order.setPayStatus(PayStatus.UNPAY.getStatus());// 等支付回调
				if (StringUtils.isEmpty(html)) {
					order.setErrMsg("网关返回的支付html为空");
					order.setPayStatus(PayStatus.PAYFAIL.getStatus());// 直接失败
					html = "FAIL";
				}
				// order.setPresentedHappyBean(presentedHappyBean);
			} else if (orderType == OrderType.CONSUME.getType()) {
				order.setPayStatus(PayStatus.PAYSUCC.getStatus());// 直接成功
			} else if (orderType == OrderType.RECHARGE_OUTSIDE.getType()) {
				// orderId = params.get("zhifuOrderId");
				order.setOrderId(orderId);
				order.setPayStatus(PayStatus.PAYSUCC.getStatus());// 直接成功
			} else {
				throw new BusinessException(MessageCode.ERROR_CODE_500, "不支持的订单类型");
			}

			long lastId = Dal.insertSelectLastId(order);
			if (lastId <= 0) {
				throw new BusinessException(MessageCode.ERROR_CODE_500, "订单创建失败");
			}

			// 考虑给用户加开心豆或者减开心豆
			if (orderType == OrderType.CONSUME.getType()) {// 消费开心豆
				if (!MemberService.consume(uid, happyBean, params,"")) {
					order.setPayStatus(PayStatus.PAYFAIL.getStatus());
					Dal.update(order, "ZhifuOrderDDL.payStatus", new Condition("ZhifuOrderDDL.id", "=", lastId));
					result.put("result", "FAIL");
					return result;
				}
			}

			if (orderType == OrderType.RECHARGE_OUTSIDE.getType()) { // 外部充值
				Map<String, String> addBeanParams = new HashMap<String, String>();
				String title = params.containsKey("title") && params.get("title") != null ? params.get("title") : "";
				addBeanParams.put("happyBean", String.valueOf(happyBean));
				addBeanParams.put("remark", "充值-" + title);
				addBeanParams.put("gameId", (params.containsKey("gameId")) && params.get("gameId") != null ? String.valueOf(params.get("gameId")) : "");
				addBeanParams.put("channel", (params.containsKey("channel")) && params.get("channel") != null ? String.valueOf(params.get("channel")) : "");
				addBeanParams.put("billId", params.get("zhifuOrderId"));
				
				int gameId = (params.containsKey("gameId")) && params.get("gameId") != null ? Integer.valueOf(params.get("gameId")) : 0;
				String addBeanLockKey = String.format("gid_%d-act_%s-uid_%d", gameId, "Charge", uid);
				boolean effect = MemberService.addBean(uid, happyBean, MemberLogOpType.RECHARGE.getType(), addBeanParams,addBeanLockKey);
				if (!effect) {
					Logger.error("充值失败 --> addBeanParams:%s", new Gson().toJson(addBeanParams));
				}
			}
			result.put("result", html);
			return result;
		} catch (Exception e) {
			Logger.error(e, "");
		} finally {
			lock.cacheUnLock(lockKey);
		}

		return null;
	}

	/**
	 * 支付回调
	 * 
	 * @param status
	 * @param orderId
	 * @param recvAmount
	 * @param complateTime
	 * @param remark
	 * @return
	 */
	public static boolean payCallBack(int status, String orderId, double recvAmount, long complateTime, String remark, String productName) {
		String lockKey = "PayCallBack-" + orderId;
		try {
			if (!lock.tryCacheLock(lockKey, "", "5s")) {
				return false;
			}

			Condition cond = new Condition("ZhifuOrderDDL.orderId", "=", orderId);
			List<ZhifuOrderDDL> orders = Dal.select(DaoUtil.genAllFields(ZhifuOrderDDL.class), cond, null, 0, -1);

			if (orders == null || orders.size() == 0) {
				return false;
			}
			ZhifuOrderDDL order = orders.get(0);
			// order.setRemark(remark);
			order.setPayTime(complateTime);
			order.setProductName(productName);
			if (order.getOrderType() != OrderType.RECHARGE.getType()) {
				Logger.warn("paycallback not recharge,order=%s", new Gson().toJson(order));
				return false;
			}
			if (status == 30) {
				RechargePresentedRuleDDL rule = ActivityService.getRechargePresentedRule(order.getHappyBean(), order.getUid(), order.getCreateTime());
				int presented = rule == null ? 0 : rule.getPresentedBean();// 计算赠送的豆子

				int amount = order.getAmount();
				int recv = (int) (recvAmount * 100);
				boolean addBeanSucc = false;
				if (Math.abs(recv - amount) > 1) {// 差额超过1分钱,不进行加豆/赠送操作
					order.setPayStatus(PayStatus.PAYFAIL.getStatus());
					order.setErrMsg("金额不对，相差大于1分钱recvAmount=" + recvAmount);
					Logger.warn("pacallback fail orderId = %s,recvAmount=%s,amount=%s", orderId, recvAmount, amount);
				} else {
					if (order.getPayStatus() == PayStatus.PAYSUCC.getStatus()) {
						Logger.warn("callpack,but payStatus = 30,repeat call me.addBean fail uid=%s,beans=%s,opType=%s", order.getUid(), order.getHappyBean(),
								MemberLogOpType.RECHARGE.getDesc());
						return true;
					}
					Map<String, String> mlParams = new HashMap<String, String>();
					mlParams.put("remark", "充值");
					mlParams.put("gameId", String.valueOf(order.getGameId()));
					mlParams.put("channel", order.getCh());
					mlParams.put("rpaId", rule != null ? String.valueOf(rule.getRpaId()) : null);
					String addBeanLockKey = String.format("gid_%d-act_%s-uid_%d", order.getGameId(),"Charge",order.getUid());
					addBeanSucc = MemberService.addBean(order.getUid(), order.getHappyBean(), MemberLogOpType.RECHARGE.getType(), mlParams,addBeanLockKey);
					if (!addBeanSucc) {
						KeyPointLogUtil.log("addBean fail uid=%s,beans=%s,opType=%s", order.getUid(), order.getHappyBean(), MemberLogOpType.RECHARGE.getDesc());
					}
					if (presented > 0) {// 进行赠送豆子操作
						mlParams.put("remark", "充值赠送");
						mlParams.put("rpaId", String.valueOf(rule.getRpaId()));
						String presentBeanLockKey = String.format("gid_%d-act_%s-uid_%d", order.getGameId(),"ChargePresent",order.getUid());
						if (!MemberService.addBean(order.getUid(), presented, MemberLogOpType.PRESENTED.getType(), mlParams,presentBeanLockKey)) {
							KeyPointLogUtil.log("addBean fail uid=%s,beans=%s,opType=%s", order.getUid(), order.getHappyBean(), MemberLogOpType.PRESENTED.getDesc());
						}
					}
				}
				order.setPayStatus(PayStatus.PAYSUCC.getStatus());
				int effect = Dal.update(order, "ZhifuOrderDDL.payStatus,ZhifuOrderDDL.payTime,ZhifuOrderDDL.errMsg,ZhifuOrderDDL.remark", new Condition("ZhifuOrderDDL.id", "=",
						order.getId()));
				if (effect == 0) {
					KeyPointLogUtil.log("update zhifu_order fail ,%s", new Gson().toJson(order));
				}
				// 发豆成功通知支付中心 转账给2000
				int addBeanStatus = 0;
				if (addBeanSucc && effect > 0) {
					addBeanStatus = 1;
				}
				PayCenterService.payCallback(orderId, addBeanStatus);
				return effect > 0;
			} else if (status == 20) {
				order.setPayStatus(PayStatus.PAYFAIL.getStatus());
				int effect = Dal.update(order, "ZhifuOrderDDL.payStatus,ZhifuOrderDDL.payTime,ZhifuOrderDDL.remark", new Condition("ZhifuOrderDDL.id", "=", order.getId()));
				if (effect == 0) {
					KeyPointLogUtil.log("update zhifu_order fail ,%s", new Gson().toJson(order));
				}
				return effect > 0;
			} else {
				Logger.error("pay callback status =%s unknow", status);
			}
		} catch (Exception e) {
			Logger.error(e, "");
		} finally {
			lock.cacheUnLock(lockKey);
		}
		return false;
	}

	public static ZhifuOrderDDL getOrder(String orderId) {
		if (StringUtils.isEmpty(orderId)) {
			return null;
		}
		Condition cond = new Condition("ZhifuOrderDDL.orderId", "=", orderId);
		List<ZhifuOrderDDL> list = Dal.select(DaoUtil.genAllFields(ZhifuOrderDDL.class), cond, null, 0, 1);
		if (list == null || list.size() == 0) {
			return null;
		}
		return list.get(0);
	}

	private static String doGet(String url, Map<String, String> params, String vcode) {
		try {
			StringBuilder sb = new StringBuilder();
			for (String key : params.keySet()) {
				Object v = params.get(key);
				String value = null;
				if (v == null)
					value = "";
				else
					value = String.valueOf(v);
				try {
					value = URLEncoder.encode(value, "utf-8");
				} catch (Exception e) {
					value = "";
				}
				sb.append("&").append(key).append("=").append(value);
			}

			String paramString = sb.substring(1);
			String requestUrl = url + "?" + paramString;

			Request request = new Request(requestUrl);
			request.addHeader("Content-Type", "text/html");
			Response response = jws.http.HTTP.GET(request, 10);

			if (response.getStatusCode() == 200) {
				String content = response.getContent();
				Logger.info("payHtml->%s", content);
				if (!content.contains(vcode)) {
					throw new Exception("请求支付网关失败");
				}
				return content;
			} else {
				throw new Exception("请求支付网关失败code=" + response.getStatusCode());
			}
		} catch (Exception e) {
			Logger.error(e, "");
		}
		return null;
	}

	public static List<ZhifuOrderDDL> listByStatus(int payStatus, long createTime, int page, int pageSize) {
		Condition cond = new Condition("ZhifuOrderDDL.payStatus", "=", payStatus);
		if (createTime > 0) {
			cond.add(new Condition("ZhifuOrderDDL.createTime", "<", createTime), "and");
		}
		return Dal.select(DaoUtil.genAllFields(ZhifuOrderDDL.class), cond, null, (page - 1) * pageSize, pageSize);
	}

	public static boolean updateOrderStatus(ZhifuOrderDDL order) {
		Condition cond = new Condition("ZhifuOrderDDL.id", "=", order.getId());
		return Dal.update(order, "ZhifuOrderDDL.payStatus,ZhifuOrderDDL.updateTime", cond) > 0;
	}
}
