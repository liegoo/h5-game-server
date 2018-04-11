package task.order;

import java.util.Calendar;
import java.util.List;

import jws.Logger;
import jws.module.constants.order.PayStatus;
import moudles.order.ddl.ZhifuOrderDDL;
import moudles.order.service.OrderService;

import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import common.task.Task;

import externals.pay.PayCenterService;

/**
 * 
 * 订单退款 定时任务
 * 
 * @author Coming
 */
public class OrderRefundTask extends Task {
	static org.slf4j.Logger logger = LoggerFactory.getLogger(OrderRefundTask.class);
	
	@Override
	public void run() {

		Logger.info("Begin order refund task ~~~");

		int payStatus = PayStatus.UNPAY.getStatus();

		long time = 0;
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -15);
		time = cal.getTimeInMillis();

		// 超过15分钟仍未支付的订单
		List<ZhifuOrderDDL> orders = OrderService.listByStatus(payStatus, time, 0, -1);

		// 更新支付状态为 超时取消
		for (ZhifuOrderDDL order : orders) {

			order.setPayStatus(PayStatus.TIMEOUT_CANCEL.getStatus());
			boolean result = OrderService.updateOrderStatus(order);

			String orderId = order.getOrderId();
			String refundResult = "";

			if (result) {
				Logger.info("更新支付状态成功,order.id:%s", order.getId());
				String buyerRefundAmount = "0";
				refundResult = PayCenterService.orderRefund(orderId, buyerRefundAmount);
			} else {
				Logger.error("更新支付状态失败,order.id:%s", order.getId());
			}

			if (refundResult == null) {
				Logger.error("订单退款失败,返回为空,orderId:%s", orderId);
			}

			if (refundResult.equalsIgnoreCase("success")) {
				Logger.info("订单退款成功,orderId:%s", orderId);
			} else {
				Logger.error("订单退款失败,orderId:%s,result:%s", orderId, new Gson().toJson(refundResult));
			}
		}

		Logger.info("End order refund task ~~~");
	}
}
