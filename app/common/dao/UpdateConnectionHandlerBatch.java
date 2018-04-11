package common.dao;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import jws.Logger;
import jws.dal.ConnectionHandler;
import utils.PropertyUtil;
/**
 * 特殊场景使用，一般使用在后台
 * 业务量大的不允许使用
 * @author fish
 *
 * @param <T>
 */
public class UpdateConnectionHandlerBatch extends ConnectionHandler{
	 
	private String[] sqls;
 
	public UpdateConnectionHandlerBatch(String[] sqls){
		this.sqls = sqls;
	}
	@Override
	public Integer handle(Connection conn) throws Exception {
		int count = 0;
		Statement stm = null;
		try{
			Logger.debug("UpdateConnectionHandlerBatch execute sql size %s", sqls.length);
			if(sqls == null || sqls.length == 0){
				return 0;
			}
			conn.setAutoCommit(false);
			stm  = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			for(String sql:sqls){
				stm.addBatch(sql);
			}
			count = stm.executeBatch().length;
			conn.commit();
		}catch(Exception e){
			Logger.error(e, "");
		}finally{
			if(stm!=null)stm.close();
			if(conn!=null)conn.close();
		}
		return count;
	}
	
} 
