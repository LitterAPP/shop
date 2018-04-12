package controllers;

import jws.Jws;
import jws.mvc.Controller;
import util.RtnUtil;

public class Version extends Controller {
	
	public static final String approvedLastVersion = Jws.configuration.getProperty("approved.last.version");
	
	public static void isApproved(String version){
		//当前版本小于等于已经审核通过的版本
		if(version.compareTo(approvedLastVersion) <= 0){
			renderJSON(RtnUtil.returnSuccess("OK",true));
		}else{
			renderJSON(RtnUtil.returnSuccess("OK",false));
		}
	}
	
	public static void main(String[] args){
		System.out.println("1.0.7".compareTo("1.0.8"));
	}

}
