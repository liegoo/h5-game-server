package utils;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import constants.MessageCode;
import jws.i18n.Lang;
import jws.i18n.Messages;
/**
 * 国际化处理
 * @author fish
 *
 */
public class MessageUtil {
	
	/**
	 * 最差的情况返回英文
	 * @param messageCode
	 * @return
	 */
	public static String getMessage(MessageCode messageCode){
		String lang = Lang.get().toLowerCase();
		Properties p = Messages.locales.get(lang);
		String key = messageCode.name().toUpperCase();
		//存在设定的语言，返回
		if(p!=null && p.containsKey(key) &&  !StringUtils.isEmpty(p.getProperty(key))){
			return p.getProperty(key, "unknow");
		}
		
		p = Messages.locales.get(lang.split("-")[0]);  
		if(p != null && p.containsKey(key) && !StringUtils.isEmpty(p.getProperty(key))){
			return p.getProperty(key, "unknow");
		}
		
		p = Messages.locales.get("en-us");
		if(p != null && p.containsKey(key) && !StringUtils.isEmpty(p.getProperty(key))){
			return p.getProperty(key, "unknow");
		}
		
		return "unknow";
	}
	
}
