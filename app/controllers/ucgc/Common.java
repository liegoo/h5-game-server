package controllers.ucgc;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.codec.digest.DigestUtils;

import common.core.UcgcController;
import exception.BusinessException;
import jws.Jws;
import utils.ChecksumHelper;

/**
 * 公共 
 */
public class Common extends UcgcController{
	
	/**
	 * 获取开心豆-人民币 兑换比率
	 */
	public static void getRMBRate() throws BusinessException{
		getHelper().returnSucc(Integer.parseInt(Jws.configuration.getProperty("rmb.rate")));
	}
	
	/**
	 * 开心豆转换人民币
	 */
	public static void getRmbAmount() throws BusinessException{
		Map params = getDTO(Map.class);
		int bean = (int)Double.parseDouble((params.get("bean").toString()));
		int rmbrate = Integer.parseInt(Jws.configuration.getProperty("rmb.rate"));
		double amount = Double.parseDouble(  String.format("%.2f",bean/(double)rmbrate ));
		getHelper().returnSucc(amount);
	}
	
	public static void getSign(){
		String vcode = "";
		Map params = getDTO(Map.class);
		String caller = params.get("caller").toString();
		Map args = (Map)params.get("params");
		if(args == null){
			getHelper().returnSucc(vcode);
		}
		Map<String,String> md5params = new TreeMap<String,String>();
		for (Object key : args.keySet()) {
			Object value = args.get(key);
			if (value == null) {
				value = "";
			}
			md5params.put(key.toString(), value.toString());
		}
		StringBuilder sb = new StringBuilder();
		sb.append(caller);
		for (String key : md5params.keySet()) {
			String value = md5params.get(key);
			if (value == null) {//为null时，替换为空字符
				value = "";
			}
			sb.append(key + "=" + value);
		}
		sb.append(Jws.configuration.getProperty(caller+".signSecretKey",""));
		String mysign = DigestUtils.md5Hex(sb.toString());
 		getHelper().returnSucc(mysign);
	}
	
}
