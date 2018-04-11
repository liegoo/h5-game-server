package moudles.gae.assist;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jws.Logger;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

/**
 * 开奖
 * 
 * @author caixb
 * 
 * 1.	将红包金额随机分成不等的5份，按由大到小排序 
 * 2.	将参与抢红包的5个用户按“用户资金池”由小到大排序（输->赢）
 * 3.	5个用户按顺序随机抽取红包：
 *
 */
public class Lottery {

	private String roomDrawSettings; //抽奖场次 概率配置
	private int planTotalBeans; //抽奖总金额 
	private int planHeadCount; //抽奖 总人数
	private Map<Integer, Integer> userPl; //参与用户历史盈亏
	private String drawId; //期数id
	public Lottery(String drawId, String roomDrawSettings, int planTotalBeans, int planHeadCount, Map<Integer, Integer> userPl){
		this.drawId = drawId;
		this.roomDrawSettings = roomDrawSettings;
		this.planTotalBeans = planTotalBeans;
		this.planHeadCount = planHeadCount;
		this.userPl = sortByValue(userPl);
	}
	
	public Map<Integer, Integer> gotoLottery(){
		Map<Integer, Integer> hitDetail = new HashMap<Integer, Integer>();
		
		//当前抽奖房间 中奖概率配置 【概率多少排序】
		List<int[]> roomAttrs= sortByValueLength(parseDrawSettings(roomDrawSettings));
		
		//当前抽奖房间　奖品随机概率
		List<Integer> prizeAttrs = new LotteryHash().pay(planTotalBeans, planHeadCount);
		
		Logger.debug("开奖===>>:抽奖期数id：%s;房间限额:$s;中奖概率分配:%s;红包分配:%s;限制参与人数：%s;实际参与人数:%s;参与用户历史盈亏：%s", drawId, planTotalBeans, roomDrawSettings, StringUtils.join(prizeAttrs, "|"), planHeadCount, userPl.size(), new Gson().toJson(userPl));
		
		int factor = 0;
		for (int uid : userPl.keySet()) {
			int[] roomAttr = roomAttrs.get(factor); //当前人抽奖概率分配
			List<Prize> prizes = new ArrayList<Prize>();
			for (int j = 0; j < roomAttr.length; j++) {
				Prize prize = new Prize();
				prize.setId(j + 1);
				prize.setProbability(roomAttr[j]);
				prize.setBeans(prizeAttrs.get(j));
				prizes.add(prize);
			}
			int hitBeans = new LotteryArithmetic().pay(prizes);
			hitDetail.put(uid, hitBeans);
			System.out.println("当前抽奖人： " + uid + ",剩余概率：" + new Gson().toJson(roomAttr) + ";剩余奖品:" + new Gson().toJson(prizeAttrs)+";【命中】:" + hitBeans);
			prizeAttrs.remove((Integer)hitBeans);
			factor++;
		}
		return hitDetail;
	}
	
	
	/**
	 * 解析中奖概率配置
	 * 
	 * @return
	 */
	private List<int[]> parseDrawSettings(String roomDrawSettings){
		try {
			List<int[]> list = new ArrayList<int[]>();
			JsonArray jsonArr = new JsonParser().parse(roomDrawSettings).getAsJsonArray();
			for (int i = 0; i < jsonArr.size(); i++) {
				JsonArray items = jsonArr.get(i).getAsJsonArray();
				int[] in = new int[items.size()];
				for (int j = 0; j < items.size(); j++) {
					in[j] = items.get(j).getAsInt();
				}
				list.add(in);
			}
			return list;
			
			} catch (Exception e) {
				Logger.error(e, "");
			}
		return null;
	}
	
	/**
	 * list<int[]> 值按长度排序
	 * 
	 * @param map
	 * @return
	 */
	private static List<int[]> sortByValueLength(List<int[]> list) {
        Collections.sort(list, new Comparator<int[]>() {
			@Override
			public int compare(int[] o1, int[] o2) {
				if((o1.length) < (o2.length)){
            		return 1;
            	}else if((o1.length) > (o2.length)){
            		return -1;
            	}
                return 0;
			}
        });
        return list;
    }
	
	/**
	 * map 按值大小排序
	 * 
	 * @param map
	 * @return
	 */
	private static Map<Integer, Integer> sortByValue(Map<Integer, Integer> map) {
        List<Map.Entry<Integer, Integer>> list = new LinkedList<Map.Entry<Integer, Integer>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {
            @Override
            public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<Integer, Integer> result = new LinkedHashMap<Integer, Integer>();
        for (Map.Entry<Integer, Integer> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
	
	public static void main(String[] args) {
		//获取用户的中奖盈亏历史【按由大到小排序 】
		Map<Integer, Integer> userPl = new HashMap<Integer, Integer>();
		userPl.put(1, -100);
		userPl.put(2, -800);
		userPl.put(3, -20);
		userPl.put(4, 100);
		userPl.put(5, 500);
		
		String roomDrawSettings = "[[40,30,20,10],[100],[40,25,15,10,10],[35,35,30],[50,50]]";
		int planTotalBeans = 200;
		int planHeadCount =5;
		Lottery lottery = new Lottery("", roomDrawSettings, planTotalBeans, planHeadCount, userPl);
				
		for (int i = 0; i < 100; i++) {
			lottery.gotoLottery();
			System.out.println("======================");
		}
	}
}
