package common;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.sun.jmx.snmp.Timestamp;

import jws.Logger;
import jws.dal.ConnectionHandler;
import util.PropertyUtil;

/**
 * 特殊场景使用，一般使用在后台
 * 业务量大的不允许使用
 * @author fish
 *
 * @param <T>
 */
public class QueryConnectionHandler <T> extends ConnectionHandler{
	private T t;
	private String sql;
	public QueryConnectionHandler(T t,String sql){
		this.t = t; 
		this.sql = sql;
	}
	@Override
	public List<T> handle(Connection conn) throws Exception {
		List<T> tList = new ArrayList<T>();
		Statement stm = null;
		ResultSet rs = null;
		try{
			stm  = conn.createStatement();
			Logger.debug("QueryConnectionHandler execute sql %s", sql); 
			rs = stm.executeQuery(sql);
			 
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
							if(f.getAnnotation(DbType.class) !=null && f.getAnnotation(DbType.class).name()!=null && f.getAnnotation(DbType.class).name().equals("DateTime")){
								PropertyUtil.setProperty(object, f.getName(),rs.getTimestamp(f.getName()).getTime());
							}else{
								PropertyUtil.setProperty(object, f.getName(), rs.getLong(f.getName()));
							}
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
		}catch(Exception e){
			Logger.error(e, "");
		}finally{
			if(stm!=null)stm.close();
			if(rs!=null)rs.close();
			if(conn!=null)conn.close();
		} 
		return tList;
	}
	
} 
