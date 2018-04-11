package externals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Strings;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import common.core.JFinalPlugin;
import jws.cache.Cache;

public class DicService {
	public static final DicService service = new DicService();
	
	/**
	 * 获取优惠券参考价配置折扣，如：7.0,8.5
	 * @param gameId
	 * @return
	 */
	public Double getReferenceDiscount(String gameId){
		if(Strings.isNullOrEmpty(gameId)){
			gameId = "0";
		}
		Record item = getDicItem("COUPON_RESELL_CONFIG_DISCOUNT", gameId);
		if(item == null){
			return null;
		}
		try{
			return Double.parseDouble(item.get("name").toString());
		}catch(Exception e){
		}
		return null;
	}
	
	/**
	 * 查询某个用户是否开启了转卖功能
	 * @param uid
	 * @return
	 */
	public boolean resaleEnabled(String uid,Integer gameId){
		Record item = getDicItem("COUPON_RESELL_CONFIG", "RESELL_SWITCH");
		if(item == null || !"1".equals(item.get("name").toString())){// 未配置或者不为1则不开启
			return false;
		}
		Record itemUid = getDicItem("COUPON_RESELL_CONFIG", "RESELL_SWITCH_UID");
		if(itemUid == null){
			return true;
		}
		Record itemGameId = getDicItem("COUPON_RESELL_CONFIG", "RESELL_SWITCH_GAMEID");
		if(itemGameId == null){
			return true;
		}
		/**
		 * 判断游戏是否在字典中
		 * */
		boolean isExist = Arrays.asList(itemGameId.get("name").toString().split(",")).contains(String.valueOf(gameId));
		if(isExist){
			return false;
		}
		return !Arrays.asList(itemUid.get("name").toString().split(",")).contains(uid);
	}
	
	/**
	 * 获取字典组
	 * @param groupCode
	 * @return
	 */
	public List<Record> getDicItems(String groupCode){
		return getDicItems(groupCode, null);
	}
	
	/**
	 * 获取字典项
	 * @param groupCode
	 * @param code
	 * @return
	 */
	public Record getDicItem(String groupCode, String code){
		List<Record> items = getDicItems(groupCode, code);
		if(items.size() > 0){
			return items.get(0);
		}
		return null;
	}
	
	private List<Record> getDicItems(String groupCode, String code){
		if(Strings.isNullOrEmpty(groupCode)){
			return new ArrayList<Record>();
		}
		String cacheKey = "DicService.getDicItems>"+groupCode+"|"+Strings.nullToEmpty(code);
		Object cache = Cache.get(cacheKey);
		if(cache != null){
			return (List<Record>) cache;
		}
		
		List<Object> params = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		sql.append("select * from dictionary_item where status = 1 and group_code = ? ");
		params.add(groupCode);
		if(!Strings.isNullOrEmpty(code)){
			sql.append("and code = ? ");
			params.add(code);
		}
		sql.append("order by sort ");
		if(!Strings.isNullOrEmpty(code)){
			sql.append("limit 1 ");
		}
		List<Record> recs = Db.use(JFinalPlugin.DICTIONARY_DB_NAME).find(sql.toString(), params.toArray());
		if(recs != null){
			Cache.add(cacheKey, recs, "1min");
		}
		return recs;
	}
}
