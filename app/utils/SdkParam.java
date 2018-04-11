package utils;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

public class SdkParam {

	/**
	 * 获取扩展参数MAP对象
	 * @param param
	 * @return
	 */
	public static Map<String,String> getValueMap(String param){
		Map<String,String> map=new HashMap<String,String>();
		if(!StringUtils.isBlank(param)){
			StringTokenizer st=new StringTokenizer(param,"|");
			while(st.hasMoreElements()){
				String obj=st.nextToken();
				String key="",value="";
				if(!StringUtils.isBlank(obj)){
					int start=obj.indexOf(":");
					if(start!=-1){
						key=obj.substring(0,start);
						value=obj.substring(start+1);
						map.put(key,value);
					}
				}
			}
		}
		return map;
	}
	
}
