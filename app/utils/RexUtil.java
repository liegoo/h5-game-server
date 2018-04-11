package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * 正则匹配
 * @author fish
 *
 */
public class RexUtil {

	public static boolean isMobile(String str){
		if(StringUtils.isEmpty(str)){
			return false;
		}
		//#^13[\\d]{9}$|^14[5,7]{1}\\d{8}$|^15[^4]{1}\\d{8}$|^17[0,6,7,8]{1}\\d{8}$|^18[\\d]{9}$#
		String regExp = "^13[\\d]{9}$|^14[5,7]{1}\\d{8}$|^15[^4]{1}\\d{8}$|^17[0,6,7,8]{1}\\d{8}$|^18[\\d]{9}$";  
		Pattern p = Pattern.compile(regExp);  
		Matcher m = p.matcher(str);  
		return m.find();
	}
	
	public static void main(String[] args){
		System.out.println(isMobile("17026759844"));
	}
}
