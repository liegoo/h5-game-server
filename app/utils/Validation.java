package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jws.Logger;

/**
 * 校验工具类
 * 
 * @author caidx
 * 
 */
public class Validation {
	/**
	 * 判断手机号码的合法性
	 * 
	 * @param mobile
	 *            用户手机号码
	 * @return
	 */
	public static boolean isMobile(String mobile) {
		boolean flag = false;
		try {
			// 目前只支持手机以1开头的国内合法手机号码
			Pattern p = Pattern.compile("^1[0-9]\\d{9}$");
			Matcher m = p.matcher(mobile);
			flag = m.matches();
		} catch (Exception e) {
			Logger.error("错误信息：%s", e.getMessage(), e.getStackTrace());
			flag = false;
		}
		return flag;
	}
	
	/**
	 * 判断字符长度合法性
	 * @param str
	 * @param min
	 * @param max
	 * @return
	 */
	public static boolean validateLen(String str, int min, int max) {
		return (str.length() >= min && str.length() <= max); 
	}
}
