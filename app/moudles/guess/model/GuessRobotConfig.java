package moudles.guess.model;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;

public class GuessRobotConfig {

	private int timeBegin;
	private int timeEnd;
	private int randSleepBegin;
	private int randSleepEnd;
	private int max;

	public GuessRobotConfig() {
	}

	public GuessRobotConfig(int beginTime, int endTime, int randSleepBegin, int randSleepEnd, int max) {
		this.timeBegin = beginTime;
		this.timeEnd = endTime;
		this.randSleepBegin = randSleepBegin;
		this.randSleepEnd = randSleepEnd;
		this.max = max;
	}

	public int getTimeBegin() {
		return timeBegin;
	}

	public void setTimeBegin(int beginTime) {
		this.timeBegin = beginTime;
	}

	public int getRandSleepBegin() {
		return randSleepBegin;
	}

	public void setRandSleepBegin(int randSleepBegin) {
		this.randSleepBegin = randSleepBegin;
	}

	public int getRandSleepEnd() {
		return randSleepEnd;
	}

	public void setRandSleepEnd(int randSleepEnd) {
		this.randSleepEnd = randSleepEnd;
	}

	public int getTimeEnd() {
		return timeEnd;
	}

	public void setTimeEnd(int endTime) {
		this.timeEnd = endTime;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	private static <T> List<T> stringToArray(String s, Class<T[]> clazz) {
		T[] arr = new Gson().fromJson(s, clazz);
		return Arrays.asList(arr);
	}

	public static List<GuessRobotConfig> fromJson(String str) {
		
		if(StringUtils.isEmpty(str)){
			return null;
		}
		
		List<GuessRobotConfig> robots = stringToArray(str, GuessRobotConfig[].class);
		return robots;
	}

}
