package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;

public class HtmlUtil {

	private final static Pattern sFilterHtmlTagPattern = Pattern.compile("</?[^<>]*>|&nbsp;|&lt;|&gt;");
    
    /**
     * 过滤html标签
     * @param str
     * @return
     * @author chenxx
     */
    public static String filterHtmllTag(String str) {
    	if(StringUtils.isEmpty(str)) {
    		return "";
    	}
    	Matcher m = sFilterHtmlTagPattern.matcher(str);
    	return m.replaceAll("").trim();
    }
    
	/**
	 * 得到网页中图片的地址
	 */
	public static List<String> getImgUrl(String htmlStr) {
		String img = "";
		Pattern pattern;
		Matcher matcher;
		List<String> pics = new ArrayList<String>();

		String patternStr = "<img.*src=(.*?)[^>]*?>"; // 图片链接地址
		pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(htmlStr);
		while (matcher.find()) {
			img = matcher.group();
			Matcher m = Pattern.compile("[^<>]*?\\ssrc=['\"]?(.*?)['\"]?\\s.*?>").matcher(img); // 匹配src
			while (m.find()) {
				String url = m.group(1);
				if(url.lastIndexOf("_") != -1) {
					url = url.substring(0, url.lastIndexOf("_")) + url.substring(url.lastIndexOf("."));
				}
				pics.add(url);
			}
		}
		return pics;
	}
	
	/**
	 * 替换空白字符
	 * @param content
	 * @return
	 * @author chenxx
	 */
	public static String replaceBlankChar(String content) {
		if(StringUtils.isEmpty(content)) {
    		return "";
    	}
		content = StringUtils.replace(content, "\n", "<br/>");
    	return content;
	}
	
}
