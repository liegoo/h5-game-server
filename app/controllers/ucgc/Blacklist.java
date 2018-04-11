package controllers.ucgc;

import java.util.Map;

import moudles.blacklist.service.BlacklistService;
import common.core.UcgcController;

/**
 * 黑名单
 * 
 * @author Coming
 */
public class Blacklist extends UcgcController {

	/**
	 * 根据uid判断用户是否在黑名单内
	 */
	public static void inBlacklist() {
		Map params = getDTO(Map.class);
		int uid = Integer.parseInt(params.get("uid").toString());

		boolean exist = BlacklistService.exists(uid);
		getHelper().returnSucc(exist);
	}
}
