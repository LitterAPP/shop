package util;

import java.util.HashMap;
import java.util.Map;

public class RtnUtil {
	public static final String CODE="code";
	public static final String MSG="msg";
	public static final String DATA="data";
	public static final int OK = 1;
	public static final int FAIL = -1;
	public static final int LOGIN_FAIL = -2;
	public static final int NOT_SELLER_FAIL = -3;
	public static final int YOUARE_SELLER_FAIL = -4;
	
	public static Map<String,Object> returnFail(){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put(CODE, FAIL);
		map.put(MSG, "服务器异常，请稍后再试");
		return map;
	}
	
	public static Map<String,Object> returnFail(String msg){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put(CODE, FAIL);
		map.put(MSG, msg);
		return map;
	}
	
	public static Map<String,Object> returnNotSeller(String msg){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put(CODE, NOT_SELLER_FAIL);
		map.put(MSG, msg);
		return map;
	}
	
	public static Map<String,Object> returnLoginFail(){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put(CODE, LOGIN_FAIL);
		map.put(MSG, "登录失败");
		return map;
	}
	
	
	public static Map<String,Object> returnFail(String msg,Object o){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put(CODE, FAIL);
		map.put(MSG, msg);
		map.put(DATA, o);
		return map;
	}
	
	public static Map<String,Object> returnSuccess(){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put(CODE, OK);
		map.put(MSG, "ok");
		return map;
	}
	
	public static Map<String,Object> returnSuccess(String msg){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put(CODE, OK);
		map.put(MSG, msg);
		return map;
	}
	
	public static Map<String,Object> returnSuccess(String msg,Object o){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put(CODE, OK);
		map.put(MSG, msg);
		map.put(DATA, o);
		return map;
	}
}
