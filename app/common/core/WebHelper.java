package common.core;

import java.lang.reflect.Type;

import jws.Logger;
import jws.mvc.Http;
import jws.mvc.results.RenderJson;

import org.h2.util.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class WebHelper {

	private static Gson gson = new GsonBuilder().registerTypeAdapter(Long.class,new LongAdapter()).create();
    /**
     * 发送成功结果的消息到客户端
     */
    public static void renderOK() {
        Http.Response.current().setContentTypeIfNotSet("text/html");
        throw new RenderJson("{\"success\":true}");
    }
    
    /**
     * 发送成功结果的消息到客户端
     * @param message
     */
    public static void renderOK(String message){
        Http.Response.current().setContentTypeIfNotSet("text/html");
        throw new RenderJson("{\"success\":true, \"message\":" + StringUtils.quoteJavaString(message) + "}");
    }
    
    /**
     * 发送成功结果的消息到客户端
     */
    public static void renderOK(Object obj){
        Http.Response.current().setContentTypeIfNotSet("text/html");
        JsonObject jo = new JsonObject();
        jo.add("success", gson.toJsonTree(true));
        jo.add("data", gson.toJsonTree(obj));
        if(Logger.isDebugEnabled()) {
			Logger.debug("return  - " + jo.toString());
		}
        throw new RenderJson(jo.toString());
    }
    
    /**
     * 发送错误结果的消息到客户端
     * @param message
     */
    public static void renderError(int errorCode, String message){
        if( message == null ){
            message = "null";
        }
        Http.Response.current().setContentTypeIfNotSet("text/html");
        throw new RenderJson("{\"errorCode\":" + errorCode + ",\"errorMsg\":" + StringUtils.quoteJavaString(message) + "}");
    }
    
    
    public static class LongAdapter implements  JsonSerializer<Long>, JsonDeserializer<Long> {

		@Override
		public Long deserialize(JsonElement json, Type type,
				JsonDeserializationContext context) throws JsonParseException {
			if (json != null) {
				try {
					return new Long(json.getAsLong());
				} catch (JsonParseException e) {
					throw e;
				}
			}
			return null;
		}

		//1432525133439000064
		//1431879850000
		//json 最多到16位
		@Override 
		public JsonElement serialize(Long value, Type type,
				JsonSerializationContext context) {
			if (value != null && value.toString().length() > 16) {
				return new JsonPrimitive(value.toString());
			}else{
				return  new JsonPrimitive(value);
			} 
		}  
    	
    }
}
