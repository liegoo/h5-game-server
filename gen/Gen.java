import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import log.Log;
import template.DDLTemplateImp;
import template.DaoTemplateImp;
import template.TemplateAbstract;
import database.DBInfo;
import database.TableInfo;

/**
 *  JAVA代码部分，包含DDL，Dao，Service部分
 *  
 *  JWS配置部分，包含biz/cache及biz/database
 * memcache默认为client0
 * database默认为dbbase
 * 需要修改，请在代码生成之后调整
 * @author Administrator
 *
 */
public class Gen {


	final class Config{
		//生成代码的表名
		public static final String tableName = "game_coin_exchange";
		//workspace路径，如果是在相同工程运行，则可以不制定，默认是当前工作目录
		public static final String workspace="";
		//代码生成的路径，自动加上ddl，service，dao包名
		public static final String path = "D:\\workspace\\h5-game-server\\app\\moudles\\gae";
		//数据源连接信息
		public static final String url = "jdbc:mysql://192.168.0.56:3306/h5-game-server?user=game_sdk&password=game_sdk&useUnicode=true&characterEncoding=utf8";
		public static final String driver = "com.mysql.jdbc.Driver";
	}
	 
	public static void main(String[] args) {
		
		Log.print("Gen Hello.");
		DBInfo dbInfo = null;
		try{
			dbInfo = new DBInfo(Config.driver,Config.url);
			TableInfo tableInfo = dbInfo.tableInfo(Config.tableName);
			TemplateAbstract ddl = new DDLTemplateImp(tableInfo,Config.path,Config.workspace);
			ddl.template(); 
			
			//生成DAO，需要根据查询字段生成list方法时，指定by条件；用map分组多个list方法
//			Map<String,String[]> qg = new HashMap<String,String[]>();
//			qg.put("1", new String[]{"status"}); 
//			TemplateAbstract dao = new DaoTemplateImp(tableInfo,Config.path,Config.workspace,qg);
//			dao.template();
			//Log.print(tableInfo.toString());
			//Log.print(code);
		}catch(Exception e){
			Log.err(e.getMessage());
		}finally{
			if( null != dbInfo){dbInfo.closeConnection();}
			Log.print("Gen finally.");
		}
	}  
}
