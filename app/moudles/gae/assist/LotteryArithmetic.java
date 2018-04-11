package moudles.gae.assist;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

/**
 * 计算开奖
 * 
 * @author caixb
 *
 */
public class LotteryArithmetic {
	// 放大倍数
	private static final int mulriple = 10;

	public int pay(List<Prize> prizes) {
		int lastScope = 0;
		// 洗牌，打乱奖品次序
		Collections.shuffle(prizes);
		Map<Integer, int[]> prizeScopes = new HashMap<Integer, int[]>();
		for (Prize prize : prizes) {
			int beans = prize.getBeans();
			// 划分区间
			int currentScope = lastScope + prize.getProbability().multiply(new BigDecimal(mulriple)).intValue();
			prizeScopes.put(beans, new int[] { lastScope + 1, currentScope });
			lastScope = currentScope;
		}

		int luckyNumber = new Random().nextInt(mulriple);
		int luckyPrizeAmount = 0;
		
		// 看脸　查找随机数所在的区间
		if ((null != prizeScopes) && !prizeScopes.isEmpty()) {
			Set<Entry<Integer, int[]>> entrySets = prizeScopes.entrySet();
			for (Map.Entry<Integer, int[]> map : entrySets) {
				int key = map.getKey();
				if (luckyNumber >= map.getValue()[0] && luckyNumber <= map.getValue()[1]) {
					luckyPrizeAmount = key;
					break;
				}
			}
		}
		
		//脸不好看　　就看命了
		if(luckyPrizeAmount == 0){
			return prizes.get(new Random().nextInt(prizes.size())).getBeans();
		}
		
		return luckyPrizeAmount;
	}
	
	
	public static void main(String[] args) {
    	Prize prize = new Prize();
		prize.setId(10);
		prize.setBeans(10);
		prize.setProbability(10);
    	
		Prize prize2 = new Prize();
		prize2.setId(15);
		prize.setBeans(15);
		prize2.setProbability(15);
		
		Prize prize3 = new Prize();
		prize3.setId(20);
		prize.setBeans(25);
		prize3.setProbability(20);
		
		Prize prize4 = new Prize();
		prize4.setId(25);
		prize.setBeans(25);
		prize4.setProbability(25);
		
		Prize prize5 = new Prize();
		prize5.setId(30);
		prize.setBeans(30);
		prize5.setProbability(30);
		
		List<Prize> prizes = new ArrayList<Prize>();
		prizes.add(prize);
		prizes.add(prize2);
		prizes.add(prize3);
		prizes.add(prize4);
		prizes.add(prize5);
		
		for (int j = 0; j < 20; j++) {
			System.out.println("==============第" + j + "轮测试:=========");
			int li_10 = 0;
			int li_15 = 0;
			int li_20 = 0;
			int li_25 = 0;
			int li_30 = 0;
			for (int i = 0; i < 100; i++) {
				int p = new LotteryArithmetic().pay(prizes);
				switch (p) {
				case 10:
					li_10++;
					break;
				case 15:
					li_15++;
					break;
				case 20:
					li_20++;		
					break;
				case 25:
					li_25++;	
					break;
				case 30:
					li_30++;	
				break;
				default:
					break;
				}
			}
			System.out.println("中10的：" + li_10 + "个");
			System.out.println("中15的：" + li_15 + "个");
			System.out.println("中20的：" + li_20 + "个");
			System.out.println("中25的：" + li_25 + "个");
			System.out.println("中30的：" + li_30 + "个");
			System.out.println("总中奖次数：" + (li_10 + li_15 + li_20 +li_25 + li_30));
			System.out.println("");
		}
	}
}