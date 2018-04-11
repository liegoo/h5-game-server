package utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class JsonUtil {
	
	private static JsonParser parser = new JsonParser();
	
	/**
	 * 根据json字符串解析成Map对象
	 * @param json
	 * @return
	 * @author chenxx
	 */
	public static Map parse(String json) {
		return (Map)parse(parser.parse(json));
	}
	
	private static Object parse(JsonElement je) {
	    
	    if (je.isJsonNull()) {
			return null;
		} else if (je.isJsonPrimitive()) {
			return handlePrimitive(je.getAsJsonPrimitive());
		} else if (je.isJsonArray()) {
			return handleArray(je.getAsJsonArray());
		} else {
			return handleObject(je.getAsJsonObject());
		}
	}
	
	private static Object handleArray(JsonArray json) {
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < json.size(); i++) {
			list.add(parse(json.get(i)));
		}
		return list;
	}
	
	private static Object handleObject(JsonObject json) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
			map.put(entry.getKey(), parse(entry.getValue()));
		}
		return map;
	}
	
	private static Object handlePrimitive(JsonPrimitive json) {
		if (json.isBoolean()) {
			return json.getAsBoolean();
		} else if (json.isString()) {
			return json.getAsString();
		} else {
			BigDecimal bigDec = json.getAsBigDecimal();
			// Find out if it is an int type
			try {
				bigDec.toBigIntegerExact();
				try {
					return bigDec.intValueExact();
				} catch (ArithmeticException e) {
				}
				return bigDec.longValue();
			} catch (ArithmeticException e) {
			}
			// Just return it as a double
			return bigDec.doubleValue();
		}
	}
}
