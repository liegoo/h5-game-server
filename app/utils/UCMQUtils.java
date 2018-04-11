package utils;

import org.apache.commons.lang3.StringUtils;

import jws.Jws;
import jws.Logger;
import jws.http.HTTP;
import jws.http.Request;
import jws.http.Response;

public class UCMQUtils {
	private static final String UCMQ_HTTP_OK="UCMQ_HTTP_OK";
	private static final String OPT_URL="http://%s/?name=%s&opt=%s&num=%s&ver=2";
	interface OPT{ 
		public static final String SET_QUEUE_SIZE="maxqueue";
		public static final String SET_DELAY_NUM="delay";
		public static final String SET_SYNC_NUM="synctime"; 
		public static final String GET_UCMQ_STATUS="status"; 
	}
	
	 
	/**
	 * 配置ucmq
	 * @param mqName mq名字
	 * @param size	mq队列大小
	 * @param delay	延迟时长 s
	 * @param sync	持久化同步数据间隔 s
	 * @throws Exception 
	 */
	public static void configMQ(String mqName) throws Exception{
		String host = Jws.configuration.getProperty("mq.server."+mqName, "");
		if(StringUtils.isEmpty(host)){
			throw new Exception("mq.["+mqName+"].server is empty.");
		}
		String mqSizeKey = "ucmq."+mqName+"maxqueue";
    	String delayKey = "ucmq."+mqName+"delay";
    	String syncKey = "ucmq."+mqName+"sync";
		
    	int mqSize = Integer.parseInt(Jws.configuration.getProperty(mqSizeKey, "20000"));
    	mqSize = mqSize==0?2000:mqSize;
    	
    	int delay = Integer.parseInt(Jws.configuration.getProperty(delayKey, "0"));
    	
    	int sync = Integer.parseInt(Jws.configuration.getProperty(syncKey, "100"));
    	
    	requestMqServer(String.format(OPT_URL, host,mqName,OPT.SET_QUEUE_SIZE,mqSize));
    	requestMqServer(String.format(OPT_URL, host,mqName,OPT.SET_DELAY_NUM,delay));
    	requestMqServer(String.format(OPT_URL, host,mqName,OPT.SET_SYNC_NUM,sync));
	}
	/**
	 * 打印mq status
	 * @param mqName
	 */
	public static void printStatus(String mqName){
		String host = Jws.configuration.getProperty("mq.server."+mqName, "");
		String url = String.format("http://%s/?name=%s&opt=%s&ver=2", host,mqName,OPT.GET_UCMQ_STATUS);
		if(StringUtils.isEmpty(url)){
			return ;
		}
		try{
			Request request = new Request(url);
			Response response = HTTP.GET(request, 5);
			if(response.getStatusCode() != 200){
				throw new Exception("request mq server error,status["+response.getStatusCode()+"]");
			}
			String content = response.getContent();
			 
			Logger.info("mq[%s] status->%s", mqName,content);
		}catch(Exception e){
			Logger.error(e, ""); 
		}
	}
	
	/**
	 * 请求ucmq设置属性
	 * @param url
	 * @throws Exception
	 */
	private static void requestMqServer(String url) throws Exception{
		if(StringUtils.isEmpty(url)){
			throw new Exception("url is empty.");
		}
		Logger.info("request mq server,url[%s]", url);
		try{
			Request request = new Request(url);
			Response response = HTTP.GET(request, 5);
			if(response.getStatusCode() != 200){
				throw new Exception("request mq server error,status["+response.getStatusCode()+"]");
			}
			String content = response.getContent();
			if(StringUtils.isEmpty(content) || !content.replaceAll("\r|\n", "").equals(UCMQ_HTTP_OK)){
				throw new Exception("request mq server fail,content["+content+"]");
			}
		}catch(Exception e){
			Logger.error(e, "");
			throw e;
		}
		
	}
}
