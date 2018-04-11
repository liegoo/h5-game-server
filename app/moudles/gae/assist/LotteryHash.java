package moudles.gae.assist;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.google.gson.Gson;


/**
 * 红包随机打散
 * 
 * @author caixb
 *
 */
public class LotteryHash {

	public List<Integer> pay(int amount, int cell) {
		Random random = new Random();
		int surplus = amount;
		Integer[] result = new Integer[cell];
		
		for (int i = 1; i <= cell; i++) {
			if(i == cell){
				result[i - 1] = surplus;
				break;
			}
			int su = cell - i;
			int thisCell = random.nextInt(surplus - (su * 2)) + 1;
			result[i - 1] = thisCell;
			surplus = surplus - thisCell;
		}
		//排序
		Arrays.sort(result, new Comparator<Integer>() {
			@Override
			public int compare(Integer o1,Integer o2) {
				if(o1 > o2){
					return -1;
				}else if(o1 < o2){
					return 1;
				}else {
					return 0;
				}
			}
		});
		//保留最小的只能有一个
		int len = result.length;
		if(len > 2 && result[len - 1] == result[len - 2]){
			System.out.println("fuck");
			return pay(amount, cell);
		}
		List<Integer> list = new ArrayList<Integer>();
		list.addAll(Arrays.asList(result));
		return list;
	}
	
	public static void main(String[] args) {
		for (int i = 0; i < 100; i++) {
			new LotteryHash().pay(200, 5);
			List<Integer> result = new LotteryHash().pay(200, 5);
			System.out.println(new Gson().toJson(result));
			int sum = 0;
			for (int j = 0; j < result.size(); j++) {
				sum += result.get(j);
			}
			System.out.println("总和："+ sum);
			System.out.println("");
		}
	}

}
