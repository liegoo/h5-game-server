package common.dao;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import constants.GlobalConstants;
import jws.Logger;
import jws.dal.ConnectionHandler;
import jws.dal.Dal;
import jws.dal.common.DbType;
import jws.dal.common.DbTypeUtils;
import jws.dal.common.EntityField;
import jws.dal.common.EntityPacket;
import jws.dal.common.SqlParam;
import jws.dal.manager.EntityManager;
 import utils.PropertyUtil;
/**
 * 用户sql统计
 * @author fish
 *
 */
public class DaoHandler {

	public static String stat(final String sql){
		Logger.debug("executeStatSql.executeStatSql->%s", sql);
		 return Dal.getConnection(GlobalConstants.dbSource, new ConnectionHandler(){
				@Override
				public String handle(Connection connection) throws Exception {
					String stat = "0";
					ResultSet rs = null;
					Statement stm = null;
					try{
						stm = connection.createStatement();
						rs = stm.executeQuery(sql);
						while(rs.next()){
							stat = rs.getString(1);
						}  
					}catch(Exception e){
						Logger.error(e, "");
					}finally{
						if(stm!=null)stm.close();
						if(rs!=null)rs.close();
						if(connection!=null)connection.close();
					} 
					return stat==null?"0":stat;
				}
			});
	}
	 
	public static String stat(final String sql,final List<SqlParam> sqlParams){
		 Logger.debug("executeStatSql.executeStatSql->%s", sql);
		 return Dal.getConnection(GlobalConstants.dbSource, new ConnectionHandler(){
				@Override
				public String handle(Connection connection) throws Exception {
					String stat = "0";
					PreparedStatement pst = null;
					ResultSet rs  = null;
					try {
						pst = connection.prepareStatement(sql);
			            for (int i=0; i<sqlParams.size(); i++) {
			                SqlParam sqlParam = sqlParams.get(i);
			                String fname = sqlParam.getFname();
			                EntityPacket pair = EntityManager.getInstance().getClassField(fname);
			                EntityField entityField = pair.getEntityField();
			                if (Logger.isDebugEnabled()) {
			                	if (entityField.getDbType()==DbType.Blob) {
			                		String show = null;
			                		if (sqlParam.getValue()!=null) { 
			                			InputStream input = (InputStream) sqlParam.getValue();
			                			show = input.available() + "(blob-len)";
			                		}
			                		Logger.debug ("[param] : %s", show);
			                	} else {
			                		Logger.debug ("[param] : %s", sqlParam.getValue());
			                	}
			                }
			                DbTypeUtils.setParams (pst, entityField.getDbType(), i+1, sqlParam.getValue());
			            }
			            rs = pst.executeQuery();
						while(rs.next()){
							stat = rs.getString(1);
						}
			        } catch (Exception e) {
			            throw e;
			        }finally{
			        	if(rs!=null)rs.close();
						if(pst!=null)pst.close();
						if(connection!=null)connection.close();
			        } 
					return stat==null?"0":stat;
				}
			});
	}
}
