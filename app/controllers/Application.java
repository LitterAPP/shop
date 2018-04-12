package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jws.Logger;
import jws.mvc.Controller;
import jws.mvc.With;
import util.RtnUtil;

@With(Handler.class)
public class Application extends Controller {

    public static void index() {
        render();
    }
    
    public static void error() {
    	throw new RuntimeException("a test exception");
    }
    public static void testAutoComplete(String keyword){
    	Logger.info("testAutoComplete keyword=%s", keyword);    	
    	List<Map<String,Object>> list= new ArrayList<Map<String,Object>>();
    	for(int i=1;i<50;i++){
    		Map<String,Object> o = new HashMap<String,Object>();
    		o.put("id", i);
    		o.put("value", "我是第 "+i+"个");
    		list.add(o);
    	}
    	renderJSON(list);
    }
}