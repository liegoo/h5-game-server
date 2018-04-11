package utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class ParamsUtil {
	
	public static Map<String,String> paramToMap(String queryString){
		Map<String,String> result = new HashMap<String,String>();
		
		if(StringUtils.isEmpty(queryString)){
			return result;
		}
		
		for(String one : queryString.split("\\&")){
			String[] kv = one.split("\\=");
			if(kv.length != 2)continue;
			result.put(kv[0],kv[1]);
		}
		
		return result;
	}
	
	public static String getParam(String key,String qstr){
		return paramToMap(qstr).get(key);
	}
}
