package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import jws.Jws;
import jws.Logger;

public class YygUtils {
	private static String dir = Jws.configuration.getProperty("rand.file.dir", "");
	private static final int RANDNUM_AMOUNT = 100;

	public static boolean deleteRandomFile(int awardId, int gameLevel) {
		String fileName = (new StringBuilder(String.valueOf(dir))).append("/").append(awardId).append("-").append(gameLevel).append(".rd").toString();
		File f = new File(fileName);
		if (f.delete()) {
			Logger.info("delete random file,seasonNum:%s,gameLevel:%s", awardId, gameLevel);
			return true;
		}
		return false;
	}

	public static void setRandomCopies(int awardId, int gameLevel, int critical, int max, int randNumAmount) {
		if (randNumAmount <= 0 || randNumAmount >= 500) {
			randNumAmount = RANDNUM_AMOUNT;
		}
		String fileName = (new StringBuilder(String.valueOf(dir))).append("/").append(awardId).append("-").append(gameLevel).append(".rd").toString();
		Logger.info((new StringBuilder("set random file:")).append(fileName).toString());
		List randomList = new ArrayList();

		long length_80 = Math.round(0.8 * randNumAmount);
		for (int i = 0; (long) i < length_80; i++) {
			int rand = (int) (Math.random() * critical) + 1;
			randomList.add(Integer.valueOf(rand));
		}

		long length_15 = Math.round(0.15 * randNumAmount);
		for (int i = 0; (long) i < length_15; i++) {
			int rand = (int) (Math.random() * critical) + 1;
			randomList.add(Integer.valueOf(rand));
		}

		long length_5 = Math.round(0.05 * randNumAmount);
		for (int i = 0; (long) i < length_5; i++) {
			int rand = (int) (Math.random() * max) + 1;
			randomList.add(Integer.valueOf(rand));
		}
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = new FileOutputStream(fileName);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(randomList);
			oos.flush();
			oos.close();
		} catch (Exception e) {
			Logger.error("YygUtil - setRandomCopies write, err:%s", e.getMessage());
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
				if (oos != null) {
					oos.close();
				}
			} catch (IOException e2) {
				Logger.error("YygUtil - setRandomCopies colse, err:%s", e2.getMessage());
			}
		}
	}

	public static int getRandomNum(int awardId, int gameLevel) {
		String fileName = (new StringBuilder(String.valueOf(dir))).append("/").append(awardId).append("-").append(gameLevel).append(".rd").toString();
		if (!new File(fileName).exists()) {
			return 0;
		}
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		List allNum = null;
		try {
			fis = new FileInputStream(fileName);
			ois = new ObjectInputStream(fis);
			allNum = (List) ois.readObject();
		} catch (Exception e) {
			Logger.error("YygUtil - getRandomNum read, err:%s", e.getMessage());
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
				if (ois != null) {
					ois.close();
				}
			} catch (IOException e) {
				Logger.error("YygUtil - getRandomNum close, err:%s", e.getMessage());
			}
		}
		int index = (int) (Math.random() * (double) allNum.size());
		return ((Integer) allNum.get(index)).intValue();
	}

}
