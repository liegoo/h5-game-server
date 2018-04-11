package utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jws.Logger;

/**
 * 疯狂夺宝 Util
 * 
 * @author Coming
 */
public class FkdbUtil {

	private static final int RAND_AMOUNT = 100;
	private static Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();

	/**
	 * 生成随机购买份数
	 * 
	 * @param gameLevel
	 * @param critical
	 * @param max
	 */
	public static void setRandomCopiesX(int gameLevel, int critical, int max) {
		List randomList = new ArrayList();

		long length_80 = Math.round(0.8 * RAND_AMOUNT);
		for (int i = 0; i < length_80; i++) {
			int rand = (int) (Math.random() * critical) + 1;
			randomList.add(Integer.valueOf(rand));
		}

		long length_15 = Math.round(0.15 * RAND_AMOUNT);
		for (int i = 0; i < length_15; i++) {
			int rand = (int) (Math.random() * critical) + 1;
			randomList.add(Integer.valueOf(rand));
		}

		long length_5 = Math.round(0.05 * RAND_AMOUNT);
		for (int i = 0; i < length_5; i++) {
			int rand = (int) (Math.random() * max) + 1;
			randomList.add(Integer.valueOf(rand));
		}
		map.put(String.valueOf(gameLevel), randomList);
	}

	/**
	 * 生成随机购买份数
	 * 
	 * @param gameLevel
	 * @param max
	 */
	public static void setRandomCopies(int gameLevel, int max) {
		List randomList = new ArrayList();

		long length_80 = Math.round(0.8 * RAND_AMOUNT);
		for (int i = 0; i < length_80; i++) {
			int rand = (int) (Math.random() * max * 0.5) + 1;
			randomList.add(Integer.valueOf(rand));
		}

		long length_15 = Math.round(0.15 * RAND_AMOUNT);
		for (int i = 0; i < length_15; i++) {
			int rand = (int) (Math.random() * max * 0.8) + 1;
			randomList.add(Integer.valueOf(rand));
		}

		long length_5 = Math.round(0.05 * RAND_AMOUNT);
		for (int i = 0; i < length_5; i++) {
			int rand = (int) (Math.random() * max) + 1;
			randomList.add(Integer.valueOf(rand));
		}
		map.put(String.valueOf(gameLevel), randomList);
	}

	/**
	 * 获取随机数
	 * 
	 * @param gameLevel
	 * @return
	 */
	public static int getRandomNum(int gameLevel) {
		List<Integer> list = map.get(String.valueOf(gameLevel));
		if (list == null) {
			return 0;
		}
		int index = (int) (Math.random() * (double) list.size());
		return list.get(index);
	}

}
