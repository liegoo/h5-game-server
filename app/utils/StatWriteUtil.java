package utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jws.Jws;
import jws.Logger;
import jws.modules.client.ClientUtil;
import jws.modules.client.dto.ClientInfoDto;
import jws.modules.client.dto.GameInfoDto;
import jws.modules.client.exceptions.ClientException;

/**
 * 统计数据打点
 * @author fish
 *
 */
public class StatWriteUtil {
	
	private static int statSwitch = Integer.parseInt(Jws.configuration.getProperty("stat.switch","0"));
	private static final String url = Jws.configuration.getProperty("stat.write.url", "");
	
	static{
		if(statSwitch==0){
			Logger.info("StatWriteUtil stat switch is closed.statSwitch=%s", statSwitch);
		}
	}
	public enum Caller{
		h5gameservice
	}
	
	enum Action
	{
		ACT_ACTION,//激活动作
	    REG_ACTION,//注册动作
	    LOGIN_ACTION,//登录动作
	    PAY_ACTION,//支付动作
	    LVUP_ACTION,//升级动作
	    ROLECREATE_ACTION,//角色建立动作
	    UV_ACTION,//UV
		PV_ACTION;//PV
	    public String raw(){
	        return this.name().toLowerCase();
	    }
	}
	/**
	 * 激活统计
	 * @param caller
	 * @param appKey
	 * @param gameId
	 * @param ch
	 * @param imei
	 */
	public static void actAction(final Caller caller,final  int gameId,
			final  String ch,final String imei){
		if(statSwitch==0){
 			return ;
		}
		
		ClientInfoDto clientDto = new ClientInfoDto();
		clientDto.setCaller(caller.name());
		clientDto.setAppKey(Jws.configuration.getProperty("stat."+caller+".appkey","zxc123wenxy%"));
		
		GameInfoDto gameDto = new GameInfoDto();
		gameDto.setCh(ch);
		gameDto.setDate(DateUtil.parseDate2Str(new Date(), "yyyy-MM-dd"));
		gameDto.setId(gameId);
		
		Map<String,String> data = new HashMap<String,String>();
		data.put("imei", imei);
		Logger.info("StatWriteUtil.actAction caller[%s] gameId[%s] ch[%s] imei[%s]", caller,gameId,ch,imei);
		try {
			ClientUtil.write(clientDto, gameDto, Action.ACT_ACTION.raw(),data, url);
		} catch (ClientException e) {
			Logger.error(e, "");
		}
	}
	/**
	 * 用户注册动作
	 * @param caller
	 * @param gameId
	 * @param ch
	 * @param imei
	 * @param uid
	 */
	public static void regAction(final Caller caller,final  int gameId,
			final  String ch,final String imei,final String uid){
		if(statSwitch==0){
 			return ;
		}
		
		ClientInfoDto clientDto = new ClientInfoDto();
		clientDto.setCaller(caller.name());
		clientDto.setAppKey(Jws.configuration.getProperty("stat."+caller+".appkey","zxc123wenxy%"));
		
		GameInfoDto gameDto = new GameInfoDto();
		gameDto.setCh(ch);
		gameDto.setDate(DateUtil.parseDate2Str(new Date(), "yyyy-MM-dd"));
		gameDto.setId(gameId);
		
		Map<String,String> data = new HashMap<String,String>();
		data.put("imei", imei);
		data.put("uid", uid);
		Logger.info("StatWriteUtil.regAction caller[%s] gameId[%s] ch[%s] uid[%s] imei[%s]", caller,gameId,ch,uid,imei);
		try {
			ClientUtil.write(clientDto, gameDto, Action.REG_ACTION.raw(),data, url);
		} catch (ClientException e) {
			Logger.error(e, "");
		}
	}
	/**
	 * 登陆动作
	 * @param caller
	 * @param gameId
	 * @param ch
	 * @param imei
	 * @param uid
	 */
	public static void loginAction(final Caller caller,final  int gameId,
			final  String ch,final String imei,final String uid){
		
		if(statSwitch==0){
 			return ;
		}
		
		ClientInfoDto clientDto = new ClientInfoDto();
		clientDto.setCaller(caller.name());
		clientDto.setAppKey(Jws.configuration.getProperty("stat."+caller+".appkey","zxc123wenxy%"));
		
		GameInfoDto gameDto = new GameInfoDto();
		gameDto.setCh(ch);
		gameDto.setDate(DateUtil.parseDate2Str(new Date(), "yyyy-MM-dd"));
		gameDto.setId(gameId);
		
		Map<String,String> data = new HashMap<String,String>();
		data.put("imei", imei);
		data.put("uid", uid);
		Logger.info("StatWriteUtil.loginAction caller[%s] gameId[%s] ch[%s] imei[%s]", caller,gameId,ch,imei);
		try {
			ClientUtil.write(clientDto, gameDto, Action.LOGIN_ACTION.raw(),data, url);
		} catch (ClientException e) {
			Logger.error(e, "");
		}
	}
	/**
	 * 支付动作
	 * @param caller
	 * @param gameId
	 * @param ch
	 * @param amount
	 * @param uid
	 */
	public static void payAction(final Caller caller,final  int gameId,
			final  String ch,final String amount,final String uid){
		
		if(statSwitch==0){
 			return ;
		}
		ClientInfoDto clientDto = new ClientInfoDto();
		clientDto.setCaller(caller.name());
		clientDto.setAppKey(Jws.configuration.getProperty("stat."+caller+".appkey","zxc123wenxy%"));
		
		GameInfoDto gameDto = new GameInfoDto();
		gameDto.setCh(ch);
		gameDto.setDate(DateUtil.parseDate2Str(new Date(), "yyyy-MM-dd"));
		gameDto.setId(gameId);
		
		Map<String,String> data = new HashMap<String,String>();
		data.put("amount", amount);
		data.put("uid", uid);
		Logger.info("StatWriteUtil.payAction caller[%s] gameId[%s] ch[%s] ", caller,gameId,ch);
		try {
			ClientUtil.write(clientDto, gameDto, Action.PAY_ACTION.raw(),data, url);
		} catch (ClientException e) {
			Logger.error(e, "");
		}
	}
	
	
	/**
	 * 支付动作
	 * @param caller 调用方名称
	 * @param page 统计pv的页面
	 * @param ts 访问时间System.currentTimeMillis()
	 */
	public static void pvAction(final Caller caller,final  String page,long ts){
		
		if(statSwitch==0){
 			return ;
		}
		
		ClientInfoDto clientDto = new ClientInfoDto();
		clientDto.setCaller(caller.name());
		clientDto.setAppKey(Jws.configuration.getProperty("stat."+caller+".appkey","zxc123wenxy%"));
		
		GameInfoDto gameDto = new GameInfoDto();
		gameDto.setCh(page);
		
		Map<String,String> data = new HashMap<String,String>();
		data.put("ts", String.valueOf(ts));
	 
		Logger.info("StatWriteUtil.pvAction caller[%s]  page[%s] ", caller,page);
		try {
			ClientUtil.write(clientDto, gameDto, Action.PV_ACTION.raw(),data, url);
		} catch (ClientException e) {
			Logger.error(e, "");
		}
	}
	
}
