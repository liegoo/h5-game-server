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
public class UpdateConnectionHandler extends ConnectionHandler{
	 
	private String sql;
	public UpdateConnectionHandler(String sql){
		this.sql = sql;
	}
	@Override
	public Integer handle(Connection conn) throws Exception {
		int count = 0;
		Statement stm = null;
		try{
			Logger.debug("UpdateConnectionHandler execute sql %s", sql); 
			stm  = conn.createStatement(); 
			count =  stm.executeUpdate(sql); 
		}catch(Exception e){
			Logger.error(e, "");
		}finally{
			if(stm!=null)stm.close();
			if(conn!=null)conn.close();
		}
		return count;
	}
	
} 
