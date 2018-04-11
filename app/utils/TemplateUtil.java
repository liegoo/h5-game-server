package utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import jws.Jws;
import jws.Logger;
import org.apache.commons.lang3.StringUtils;

public class TemplateUtil {

	private static TemplateUtil tu = new TemplateUtil();
	
	private static Map<String, StringBuilder> tplList = new HashMap<String, StringBuilder>();
	
	public static TemplateUtil getInstance() {
		return tu;
	}
	
	private TemplateUtil() {
		
	}
	
	/**
	 * 获取模板内容
	 * @param tplName
	 * @return
	 */
	public String getTemlate(String tplName) {
		if(StringUtils.isEmpty(tplName)) {
			return "";
		}
		if(tplList.containsKey(tplName)) {
			return tplList.get(tplName).toString();
		}
		StringBuilder sb = readTemplate(tplName);
		tplList.put(tplName, sb);
		return sb.toString();
	}

	/**
	 * 读取模板信息
	 * @param tplName
	 * @return
	 */
	private StringBuilder readTemplate(String tplName) {
		String filePath = "public/bizResource/" + tplName + ".html";
		BufferedReader br = null;
		StringBuilder sb = null;
		try {
			//初始化HTML文件內容 
			InputStreamReader isr = new InputStreamReader(new FileInputStream(Jws.getFile(filePath)),"utf-8");
			br = new BufferedReader(isr);
			
			//拼接html字符串
			sb = new StringBuilder();
			String tmpStr = null;
			while((tmpStr = br.readLine()) != null) {
				sb.append(tmpStr);
			}
		} catch (FileNotFoundException e) {
			Logger.error(e, "can not find file:%s", filePath);
		} catch (IOException e) {
			Logger.error(e, "can not find file:%s", filePath);
		} catch (Exception e) {
			Logger.error(e, e.getMessage());
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				Logger.error(e, e.getMessage());
			}
		}
		return sb;
	}
}
