package moudles.gae.assist;

import java.math.BigDecimal;

/**
 * 提供统一计算所需开心豆方法
 * 
 * @author caixb
 *
 */
public class HappyBeans {

	/**
	 * 计算本金+抽水
	 * 
	 * @param thisWantBeans 当前进行的抢红包需要的豆子
	 * @param thisDrawRatio 当前进行的抢红包需要抽水比例
	 * 
	 * @return 实际所需豆豆
	 */
	public static int beansCount(int thisWantBeans, int thisDrawRatio){
		//当前参与抽奖最少需要豆子数
		return thisWantBeans + profit(thisWantBeans, thisDrawRatio);
	}
	
	/**
	 * 计算抽水
	 * 
	 * @param thisWantBeans 当前进行的抢红包需要的豆子
	 * @param thisDrawRatio 当前进行的抢红包需要抽水比例
	 * 
	 * @return profit
	 */
	public static int profit(int thisWantBeans, int thisDrawRatio){
		//当前参与抽奖获利抽水
		return new BigDecimal((float)thisWantBeans * (float)thisDrawRatio / 100).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
	}
}
