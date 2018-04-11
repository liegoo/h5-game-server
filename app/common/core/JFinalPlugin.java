package common.core;

import java.util.List;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.wall.WallFilter;
import com.google.common.base.Strings;
import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.druid.DruidPlugin;

public class JFinalPlugin {
//	private static boolean isInit = false;// 是否已初始化过
	public static final String DICTIONARY_DB_NAME = "dic";
	
	/**
	 * 初始化jFinal的插件
	 */
	public synchronized static void initDb(String dbName, String dbUserName, String dbPass, String conUrl, int minConCount, int maxConCount, int conTimeout){
//		if(isInit){
//			return;
//		}
		// 配置druid连接池
		DruidPlugin druidDefault = new DruidPlugin(conUrl, dbUserName, dbPass, "com.mysql.jdbc.Driver");
		// StatFilter提供JDBC层的统计信息
		druidDefault.addFilter(new StatFilter());
		// WallFilter的功能是防御SQL注入攻击
		WallFilter wallDefault = new WallFilter();
		wallDefault.setDbType(JdbcConstants.MYSQL);
		druidDefault.addFilter(wallDefault);
		
		druidDefault.setInitialSize(minConCount);
		druidDefault.setMaxPoolPreparedStatementPerConnectionSize(maxConCount);
		druidDefault.setTimeBetweenConnectErrorMillis(conTimeout);
		ActiveRecordPlugin arp = null;
		if(Strings.isNullOrEmpty(dbName)){
			arp = new ActiveRecordPlugin(druidDefault);
		}else{
			arp = new ActiveRecordPlugin(dbName, druidDefault);
		}
		druidDefault.start();
		arp.start();
		
//		isInit = true;
	}
}
