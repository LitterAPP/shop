package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import jws.Jws;
import jws.Logger;
import jws.cache.Cache;
import jws.mvc.Controller;
import sun.misc.BASE64Decoder;
import util.API;
import util.MD5Util;
import util.RtnUtil;

public class Upload extends Controller{
	
	public static void uploadFile(String session,File file,int cos){
		FileChannel channel = null;  
        FileInputStream fs = null; 
		try{ 
			/*CookBookUsersDDL user = UserService.findBySession(session);
			if(user==null){
				renderJSON(RtnUtil.returnLoginFail());
			}*/
			if(file==null || !file.exists()){
				renderJSON(RtnUtil.returnFail("文件不存在"));
			}
			//判断文件二进制流MD5是否已经上传过阿里云
			fs = new FileInputStream(file);  
            channel = fs.getChannel();  
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) channel.size());  
            while ((channel.read(byteBuffer)) > 0) {  
            } 
			String md5FileKey = MD5Util.md5(byteBuffer.array());
			Logger.info("文件%s,对应的cachekey为%s", file.getName() ,md5FileKey);
			Object o = Cache.get(md5FileKey);
			if(o!=null){
				Logger.info("get object key from cache , md5FileKey=%s,key=%s", md5FileKey,String.valueOf(o));
				renderJSON(RtnUtil.returnSuccess("OK",String.valueOf(o)));
			}
			String objectKey = null;
			//判断是否图片，图片进行压缩
			String fileFix = file.getName().substring(file.getName().lastIndexOf(".")).toLowerCase();
			if(fileFix.equals(".jpg") || fileFix.equals(".gif") ||
					fileFix.equals(".jpeg") || 
					fileFix.equals(".png")){
				if(cos==1){
					objectKey = API.uploadImageToTencent("hongjiu-1252785849", file, 400, 0.9f);
				}else{
					objectKey = API.uploadImage("tasty", file, 400, 0.9f);
				}
				
			}else{
				objectKey = API.uploadToAliOss("tasty", file);
			}
			Cache.set(md5FileKey, objectKey, "28d");
			renderJSON(RtnUtil.returnSuccess("OK",objectKey));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail(e.getMessage()));
		}finally{
            try {
				channel.close();
				  fs.close();  
			} catch (Exception e) {
				Logger.error(e, e.getMessage());
				renderJSON(RtnUtil.returnFail(e.getMessage()));
			}  
                      
		}
	}
	
	public static void uploadImageOfBase64(String session,String base64Str,int cos){
		FileChannel channel = null;   
        OutputStream out = null;
        FileInputStream fs = null;
        
		try{  
			/*CookBookUsersDDL user = UserService.findBySession(session);
			if(user==null){
				renderJSON(RtnUtil.returnLoginFail());
			}*/
			
			if (StringUtils.isEmpty(base64Str)){
	        	renderJSON(RtnUtil.returnFail("Base64为空"));
	        }
			
			 
			String fix = "."+base64Str.substring(11,base64Str.indexOf(";"));
			if(fix.equals(".jpg") || fix.equals(".gif") || fix.equals(".jpeg") || fix.equals(".png")){
				
			}else{
				renderJSON(RtnUtil.returnFail("不支持的图片格式"));
			}
			String tmpName = UUID.randomUUID().toString()+fix;
			
			//去掉插件携带的信息
			
			base64Str = base64Str.substring(base64Str.indexOf(",")+1);
			BASE64Decoder decoder = new BASE64Decoder();
			byte[] b = decoder.decodeBuffer(base64Str);
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {
					b[i] += 256;
				}
			}
			
			Logger.info("JWS的路径是怎么样的 %s,%s,%s", Jws.applicationPath.getAbsolutePath(),tmpName,File.separatorChar);
			
			
			
			File file = new File(Jws.applicationPath.getAbsolutePath()+File.separatorChar+"tmp"+File.separatorChar+tmpName);
			out = new FileOutputStream(file);
			out.write(b);
			out.flush(); 
			
			//判断文件二进制流MD5是否已经上传过阿里云
			fs = new FileInputStream(file);  
            channel = fs.getChannel();  
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) channel.size());  
            while ((channel.read(byteBuffer)) > 0) {  
            } 
			String md5FileKey = MD5Util.md5(byteBuffer.array());
			Logger.info("文件%s,对应的cachekey为%s", file.getName() ,md5FileKey);
			Object o = Cache.get(md5FileKey);
			if(o!=null){
				Logger.info("get object key from cache , md5FileKey=%s,key=%s", md5FileKey,String.valueOf(o));
				renderJSON(RtnUtil.returnSuccess("OK",String.valueOf(o)));
			}
			String objectKey = null;
			//判断是否图片，图片进行压缩
			if(cos==1){
				objectKey = API.uploadImageToTencent("hongjiu-1252785849", file, 400, 0.9f);
			}else{
				objectKey = API.uploadImage("tasty", file, 400, 0.9f);
			}
			Cache.set(md5FileKey, objectKey, "28d");
			renderJSON(RtnUtil.returnSuccess("OK",objectKey));
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			renderJSON(RtnUtil.returnFail(e.getMessage()));
		}finally{
            try {
				channel.close();
				out.close();  
				fs.close();
			} catch (Exception e) {
				Logger.error(e, e.getMessage());
				renderJSON(RtnUtil.returnFail(e.getMessage()));
			}  
                      
		}
	}
	
	
	
	
}
