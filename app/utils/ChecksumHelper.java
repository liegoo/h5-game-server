package utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import jws.Logger;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;

public class ChecksumHelper {

	/**
	 * 获取业务校验码
	 * 
	 * @param params URL参数对，需要使用TreeMap来保持参数字典序
	 * @param secretKey 业务密钥
	 * 
	 * @return 业务校验码
	 */
	public static String getChecksum(Map<String, String> params, String secretKey) {
		StringBuilder sb = new StringBuilder();

		for (String key : params.keySet()) {
			String value = params.get(key);
			if (value == null) {
				value = "";
			}
			sb.append(value);
		}

		if (!StringUtils.isEmpty(secretKey)) {
			sb.append(secretKey);
		}

		String vcode = DigestUtils.md5Hex(sb.toString());
		Logger.info("产生vcode的源串: %s, vcode=%s", sb, vcode);
		return vcode;
	}
	
	/**
	 * 签名加密
	 * 
	 * @param params
	 * @param caller
	 * @param secretKey
	 * @return
	 */
	public static String getChecksum(Map<String, String> params, String caller,String secretKey) {
		StringBuilder sb = new StringBuilder();

		if (!StringUtils.isEmpty(caller)) {
			sb.append(caller);
		}
		
		for (String key : params.keySet()) {
			String value = params.get(key);
			if (value == null) {
				value = "";
			}
			sb.append(key);
			sb.append("=");
			sb.append(value);
		}

		if (!StringUtils.isEmpty(secretKey)) {
			sb.append(secretKey);
		}
		String vcode = DigestUtils.md5Hex(sb.toString());
		Logger.info("产生vcode的源串: %s, vcode=%s", sb, vcode);
		return vcode;
	}
	
	public static String getCpChecksum(Map<String, String> params, String cpid,
			String appkey) {
		StringBuilder sb = new StringBuilder();

		sb.append(cpid);
		for (String key : params.keySet()) {
			String value = (String) params.get(key);
			if (value == null) {
				value = "";
			}
			sb.append(key + "=" + value);
		}
		if (!StringUtils.isEmpty(appkey)) {
			sb.append(appkey);
		}
		Logger.info("产生vcode的源串: %s" , sb.toString());

		return DigestUtils.md5Hex(sb.toString());
	}
	
	public static void addSign(String caller, String signKey, TreeMap<String, Object> paramMap) {
		paramMap = (paramMap == null) ? new TreeMap() : paramMap;
		StringBuilder sb = new StringBuilder();
		sb.append(caller);
		for (String key : paramMap.keySet()) {
			Object o = paramMap.get(key);
			String request = ""; 
			if (o instanceof String){
				request = o.toString();
				sb.append(new StringBuilder().append(key).append("=").append(request).toString());
			}else {
				request = new Gson().toJson(paramMap.get(key)).toString();
				sb.append(new StringBuilder().append(key).append("=").append(request).toString());
			}
			paramMap.put(key,request);
		}
		sb.append(signKey);
		MessageDigest md5 = null;
		StringBuffer mySign = new StringBuffer();
		try {
			md5 = MessageDigest.getInstance("MD5");
			byte[] mySignByte = md5.digest(sb.toString().getBytes("utf-8"));

			for (int i = 0; i < mySignByte.length; ++i) {
				int val = mySignByte[i] & 0xFF;
				if (val < 16)
					mySign.append("0");
				mySign.append(Integer.toHexString(val));
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		Logger.info(new StringBuilder().append("待验签的字符串:").append(sb.toString()).append(",生成的签名为:").append(mySign).toString());
		paramMap.put("caller", caller);
		paramMap.put("sign", mySign.toString());
	}
	
//	public static String body(TreeMap<String, Object> paramMap) {
//		if ((paramMap == null) || (paramMap.size() == 0)) {
//			return null;
//		}
//		if (!(paramMap.containsKey("sign"))) {
//			Logger.error(new StringBuilder().append("paramMap miss sign key paramMap=").append(paramMap).toString());
//		}
//		StringBuilder sb = new StringBuilder();
//		int index = 1;
//		for (String key : paramMap.keySet()) {
//			Object o = paramMap.get(key);
//			if (o instanceof String)
//				sb.append(new StringBuilder().append(key).append("=").append(o.toString()).toString());
//			else {
//				sb.append(new StringBuilder().append(key).append("=").append(new Gson().toJson(paramMap.get(key)))
//						.toString());
//			}
//			if (index < paramMap.size()) {
//				sb.append("&");
//			}
//			++index;
//		}
//		return sb.toString();
//	}
	
	public static void main(String[] args){
		long time = System.currentTimeMillis();
		System.out.println(time);
		System.out.println(DigestUtils.md5Hex("8337117ffd678ecee932a58854a02c31"+time));
	}
	

}
