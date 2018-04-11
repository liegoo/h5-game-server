package common.dao;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.sun.jmx.snmp.Timestamp;

import jws.Logger;
import jws.dal.ConnectionHandler;
import jws.dal.common.DbType;
import jws.dal.common.DbTypeUtils;
import jws.dal.common.EntityField;
import jws.dal.common.EntityPacket;
import jws.dal.common.SqlParam;
import jws.dal.manager.EntityManager;
import utils.PropertyUtil;

/**
 * 特殊场景使用，一般使用在后台
 * 业务量大的不允许使用
 * @author fish
 *
 * @param <T>
 */
public class QueryConnectionAdvHandler <T> extends ConnectionHandler{
	private T t;
	private String sql;
	private List<SqlParam> sqlParams;
	public QueryConnectionAdvHandler(T t,String sql,List<SqlParam> sqlParams){
		this.t = t; 
		this.sql = sql;
		this.sqlParams = sqlParams;
	}
	@Override
	public List<T> handle(Connection conn) throws Exception {
			List<T> tList = new ArrayList<T>(); 
			PreparedStatement pst = null;
			ResultSet rs  = null;
			try { 
				pst = conn.prepareStatement(sql);
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
					Field[] fields= t.getClass().getDeclaredFields();
					Object object= Class.forName(t.getClass().getName()).newInstance();
					for(Field f:fields){
						if("this$0".equals(f.getName())){
							continue;
						}
						Object o = null;
						try{
						  o = rs.getObject(f.getName());
						}catch(SQLException e){
							Logger.warn("field %s may not exisit in coloumns.", f.getName()); 
						}
						if(o!=null){ 
							if(f.getType() == Integer.class || f.getType() == int.class){ 
								PropertyUtil.setProperty(object, f.getName(), rs.getInt(f.getName()));
							} else if(f.getType() == Timestamp.class) {
								PropertyUtil.setProperty(object, f.getName(), rs.getTimestamp(f.getName()));
							} else if(f.getType() == Long.class || f.getType() == long.class) {
								PropertyUtil.setProperty(object, f.getName(), rs.getLong(f.getName()));
							} else if(f.getType() == Double.class || f.getType() == double.class){
								PropertyUtil.setProperty(object, f.getName(), rs.getDouble(f.getName()));
							} else if(f.getType() == Float.class || f.getType() == float.class){
								PropertyUtil.setProperty(object, f.getName(), rs.getFloat(f.getName()));
							} else if(f.getType() == String.class){
								PropertyUtil.setProperty(object, f.getName(), rs.getString(f.getName()));
							} else{
								PropertyUtil.setProperty(object, f.getName(), rs.getObject(f.getName()));
							}
						}
					}
					tList.add((T)object);
				}
	        } catch (Exception e) {
	            throw e;
	        }finally{
	        	if(rs!=null)rs.close();
				if(pst!=null)pst.close();
				if(conn!=null)conn.close();
	        } 
			return tList; 
	}   
} 
