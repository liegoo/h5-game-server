package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import jws.Jws;
import jws.Logger;

import org.apache.commons.lang.StringUtils;

public class ExpressionUtil {

	private static Properties expProp = new Properties();
	
	static {
		FileInputStream inputFile = null;
		try {
			//加载表情配置
			inputFile = new FileInputStream(Jws.configuration.getProperty("expression.conf"));
			expProp.load(new InputStreamReader(inputFile, "UTF-8")); 
		} catch (Exception e) {
			Logger.error(e, "failed to read expression config");
		} finally {
			try {
				if(inputFile != null) {
					inputFile.close();
				}
			} catch (IOException e) {
				Logger.error(e, "failed to close expression file stream");
			}
		}
	}
	
	/**
	 * 解析表情
	 * @param content
	 * @return
	 * @author chenxx
	 */
	public static String parseContent(String content) {
		if(StringUtils.isEmpty(content) || content.indexOf("[") < 0 || content.indexOf("]") < 0) {
			return content;
		}
		
		int size = expProp.keySet().size();
		String[] replacementList = new String[size];
		String[] searchList = new String[size];
		searchList = expProp.keySet().toArray(searchList);
		
		int i=0;
		for(Object o : searchList) {
			replacementList[i] = "<img src=\"/public/images/expression/" + expProp.getProperty(o.toString()) + "\" noselect=\"true\" width=\"24\" height=\"24\" />";
			i++;
		}
		return StringUtils.replaceEach(content, searchList, replacementList);
	}
}
