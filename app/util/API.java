package util;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.PutObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;

import jws.Jws;
import jws.Logger;
import jws.cache.Cache;
import jws.http.Request;
import jws.http.Response;
import jws.http.sf.HTTP;
import modules.common.service.FormIdService;
import modules.shop.service.WXAccessTokenService;
import net.coobird.thumbnailator.Thumbnails;
import util.baidu.BaiduHttpUtil;
import util.baidu.Base64Util;

public class API {
	// 1 初始化用户身份信息(secretId, secretKey)
	private static final COSCredentials cred = new BasicCOSCredentials("AKIDCQmDxrO1cRNB7mfZzNfD76KPLH2NBXKB", "W3nbOHA8CEc6etE8pbIB7uhWQM9lDNEz");
	// 2 设置bucket的区域, COS地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
	private static final ClientConfig clientConfig = new ClientConfig(new Region("ap-guangzhou"));

	
	 
	public static <T> T aliAPI(String host,String path,String method, Map<String, String> querys,Type type,String appcode){
		
		 Map<String, String> headers = new HashMap<String, String>();
		 //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
		 headers.put("Authorization", "APPCODE " + appcode);
		 try {
		    	 
		    	HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
		    	
		    	if( response == null ){
		    		Logger.error("API.aliAPI path:%s,return null.", path);
		    		return null;
		    	} 
		    	String jsonStr =inputStreamToString(response.getEntity().getContent());
		    	
		    	Logger.info("aliAPI request[path=%s,querys=%s],result=%s",path,querys ,jsonStr);
		    	
		    	if(StringUtils.isEmpty(jsonStr)){
		    		return null;
		    	} 
		    	 
		    	return (T)new Gson().fromJson(jsonStr, type);
		    } catch (Exception e) {
		    	Logger.error(e, e.getMessage());
		    	return null;
		    }
	}
	/**
	 * 菜谱大全 - 杭州网尚科技有限公司
	 * @param path
	 * @param method
	 * @param querys
	 * @param t
	 * @return
	 */
	public static <T> T hzwsAPI(String path,String method, Map<String, String> querys,Type type,String appcode){
		 String host = "http://jisusrecipe.market.alicloudapi.com";
		 return  aliAPI(host,path,method,querys,type,appcode); 
	}
	
	private static String inputStreamToString(InputStream is) { 
        String line = "";
        StringBuilder total = new StringBuilder(); 
        BufferedReader rd = new BufferedReader(new InputStreamReader(is)); 
        try { 
            while ((line = rd.readLine()) != null) {
                total.append(line);
            }
        } catch (IOException e) {
           Logger.error(e, e.getMessage());
        } 
        return total.toString();
    }
	
	public static JsonObject getBaiDuAccessToken(String appKey,String secretKey){
		Map<String, String> headers = new HashMap<String,String>();
		headers.put("Content-Type", "application/json");
	    Map<String, String> querys = new HashMap<String,String>();
	    querys.put("grant_type", "client_credentials");
	    querys.put("client_id", appKey);
	    querys.put("client_secret", secretKey);
	    try {
	    	HttpResponse response =  HttpUtils.doGet("https://aip.baidubce.com", "/oauth/2.0/token", null, headers, querys);
			String jsonStr =inputStreamToString(response.getEntity().getContent());
			System.out.println(jsonStr);
			return new JsonParser().parse(jsonStr).getAsJsonObject();
	    } catch (Exception e) {
			 Logger.error(e, e.getMessage());
	    }
	    return null;
	}
	/**
	 * 词法分析
	 * @param access_token
	 * @param text
	 */
	public static JsonObject nlpBaiDuLexer(String access_token,String text){
		Map<String, String> headers = new HashMap<String,String>();
		headers.put("Content-Type", "application/json");
		headers.put("Content-Encoding", "GBK");
		Map<String, String> querys = new HashMap<String,String>();
		querys.put("access_token", access_token);
		try{
			Map<String, String> body = new HashMap<String,String>();
  			body.put("text",text);
			HttpResponse response =	HttpUtils.doPost("https://aip.baidubce.com", "/rest/2.0/image-classify/v2/dish", headers,querys , new Gson().toJson(body),"GBK");
			String jsonStr = EntityUtils.toString(response.getEntity(), "GBK");
			return new JsonParser().parse(jsonStr).getAsJsonObject();
		}catch(Exception e){
			
		}
		return null;
	}
	
	/**
	 * 菜品识别接口
	 * @param access_token
	 * @param base64Img
	 * @return
	 */
	public static JsonObject dishBaidu(String accessToken,byte[] imgData,int num){
		String url = "https://aip.baidubce.com/rest/2.0/image-classify/v2/dish";
		try {
			
			String imgStr = Base64Util.encode(imgData);
			String imgParam = URLEncoder.encode(imgStr, "UTF-8");
			String param = "image=" + imgParam + "&top_num=" + num;
			String result = BaiduHttpUtil.post(url, accessToken, param);
			return new JsonParser().parse(result).getAsJsonObject();
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
		return null;
	
	}
	
	public static String uploadToAliOss(String bucketName,File file) throws Exception{
		OSSClient ossClient = new OSSClient("oss-cn-beijing.aliyuncs.com",
				"HW77gOwWnQiwQIuB", 
				"0N36kSmuIapg7352cX23fOGxUyXMoq");  
		try{ 
			if (!ossClient.doesBucketExist(bucketName)) {
                /*
                 * Create a new OSS bucket
                 */
                ossClient.createBucket(bucketName);
                CreateBucketRequest createBucketRequest= new CreateBucketRequest(bucketName);
                createBucketRequest.setCannedACL(CannedAccessControlList.Private);
                ossClient.createBucket(createBucketRequest);
            }
			String myObjectKey = UUID.randomUUID().toString().replace("-", "");
			ossClient.putObject(
					new PutObjectRequest(bucketName, myObjectKey,file)
				);
			
			Date expiration = new Date(new Date().getTime() + 60 * 1000);
			ossClient.generatePresignedUrl(bucketName, myObjectKey, expiration); 
			
			Logger.info("文件[%s]对应的ObjectKey[%s]", file.getAbsolutePath(),myObjectKey);
			
			return myObjectKey;
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			throw new Exception("上传失败");
		}finally{
			ossClient.shutdown();
		} 
	}
	
	/**
	 * 
	 * @param bucketName
	 * @param file
	 * @param width 图片压缩宽度 默认320
	 * @param quality 图片压缩质量 小于1的float类型 默认0.9
	 * @return
	 * @throws Exception
	 */
	public static String uploadImageToTencent(String bucketName,File file,int width,float quality ) throws Exception{
		
		width = width==0?320:width;
		quality = quality==0.0f?0.9f:quality;	
		 
		COSClient cosclient = null;
		InputStream input = new FileInputStream(file);
		BufferedImage bufImg = ImageIO.read(input);// 把图片读入到内存中
		try{
			//压缩图片
			bufImg = Thumbnails.of(bufImg).width(width).keepAspectRatio(true).outputQuality(quality).asBufferedImage();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();// 存储图片文件byte数组
			ImageIO.write(bufImg, "png", bos); // 图片写入到 ImageOutputStream
			input = new ByteArrayInputStream(bos.toByteArray());
			
			cosclient = new COSClient(cred, clientConfig);
			// bucket的命名规则为{name}-{appid} ，此处填写的存储桶名称必须为此格式
			bucketName = StringUtils.isEmpty(bucketName)?"hongjiu-1252785849":bucketName;
			if(!cosclient.doesBucketExist(bucketName)){
				 com.qcloud.cos.model.CreateBucketRequest createBucketRequest = new com.qcloud.cos.model.CreateBucketRequest(bucketName);
				 createBucketRequest.setCannedAcl(com.qcloud.cos.model.CannedAccessControlList.PublicRead);
				 com.qcloud.cos.model.Bucket bucket = cosclient.createBucket(createBucketRequest);
			}
			String myObjectKey = "COS_"+UUID.randomUUID().toString().replace("-", "");
			com.qcloud.cos.model.ObjectMetadata  objectMetadata = new com.qcloud.cos.model.ObjectMetadata ();
			objectMetadata.setContentLength(input.available());
			objectMetadata.setContentType("image/png");
			com.qcloud.cos.model.PutObjectRequest  putObjectRequest =
	                new com.qcloud.cos.model.PutObjectRequest(bucketName, myObjectKey, input, objectMetadata);
			
			com.qcloud.cos.model.PutObjectResult putObjectResult = cosclient.putObject(putObjectRequest);
			 
			Logger.info("upload image to tencent,myObjectKey %s",myObjectKey);
			return myObjectKey;
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			throw new Exception("上传失败");
		}finally{
			if(cosclient!=null)cosclient.shutdown();
			input.close(); 
		} 
	}
	
	/**
	 * 
	 * @param bucketName
	 * @param file
	 * @param width 图片压缩宽度 默认320
	 * @param quality 图片压缩质量 小于1的float类型 默认0.9
	 * @return
	 * @throws Exception
	 */
	public static String uploadImage(String bucketName,File file,int width,float quality ) throws Exception{
		
		width = width==0?320:width;
		quality = quality==0.0f?0.9f:quality;
		
		OSSClient ossClient = new OSSClient("oss-cn-beijing.aliyuncs.com",
				"HW77gOwWnQiwQIuB", 
				"0N36kSmuIapg7352cX23fOGxUyXMoq");
		
		InputStream input = new FileInputStream(file);
		BufferedImage bufImg = ImageIO.read(input);// 把图片读入到内存中
		try{
			//压缩图片
			bufImg = Thumbnails.of(bufImg).width(width).keepAspectRatio(true).outputQuality(quality).asBufferedImage();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();// 存储图片文件byte数组
			ImageIO.write(bufImg, "jpg", bos); // 图片写入到 ImageOutputStream
			input = new ByteArrayInputStream(bos.toByteArray());
			
			if (!ossClient.doesBucketExist(bucketName)) {
                /*
                 * Create a new OSS bucket
                 */
                ossClient.createBucket(bucketName);
                CreateBucketRequest createBucketRequest= new CreateBucketRequest(bucketName);
                createBucketRequest.setCannedACL(CannedAccessControlList.Private);
                ossClient.createBucket(createBucketRequest);
            }
			String myObjectKey = UUID.randomUUID().toString().replace("-", "");
			ossClient.putObject(
					new PutObjectRequest(bucketName, myObjectKey,input)
				);
			
			Date expiration = new Date(new Date().getTime() + 60 * 1000);
			ossClient.generatePresignedUrl(bucketName, myObjectKey, expiration); 
			
			Logger.info("文件[%s]对应的ObjectKey[%s]", file.getAbsolutePath(),myObjectKey);
			
			return myObjectKey;
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			throw new Exception("上传失败");
		}finally{
			ossClient.shutdown();
			input.close(); 
		} 
	}
	
	public static String getObjectAccessUrlSimple(String objectKey) throws Exception{
		if(StringUtils.isEmpty(objectKey)) return null;
		Object urlFromCache = Cache.get(objectKey);
		if(urlFromCache!=null){
			return String.valueOf(urlFromCache);
		}
		
		if(objectKey.startsWith("COS_")){//腾讯云COS新增后，兼容代码
			return getTencentCosAccessUrl("hongjiu-1252785849",objectKey,0);
		}else{
			return getObjectAccessUrl("tasty",objectKey,0);
		}
	}
	
	/**
	 * 
	 * @param bucketName
	 * @param objectKey
	 * @param expiresInSecond 失效，统一fix为1hour
	 * @return
	 * @throws Exception
	 */
	private static String getObjectAccessUrl(String bucketName,String objectKey,int expiresInSecond) throws Exception{
		if(StringUtils.isEmpty(objectKey)) return null;
		
		Object urlFromCache = Cache.get(objectKey);
		if(urlFromCache!=null){
			return String.valueOf(urlFromCache);
		}
		
		if(objectKey.startsWith("COS_")){//腾讯云COS新增后，兼容代码
			return getTencentCosAccessUrl(bucketName,objectKey,expiresInSecond);
		}
		OSSClient ossClient = null;
		try{
			
			ossClient = new OSSClient("oss-cn-beijing.aliyuncs.com",
					"HW77gOwWnQiwQIuB", 
					"0N36kSmuIapg7352cX23fOGxUyXMoq");
			expiresInSecond = 1*60*60+5*60;
			Date expiration = new Date(new Date().getTime() + expiresInSecond * 1000);
			URL url = ossClient.generatePresignedUrl(bucketName, objectKey, expiration);
			String urlStr = url.toString();
			urlStr = urlStr.replace("http://tasty.oss-cn-beijing.aliyuncs.com/", "https://91loving.cn/shoposs/");
			Logger.info("根据阿里object key %s 获取URL：%s", objectKey,urlStr);
			Cache.set(objectKey, urlStr, "1h");
			return urlStr;
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			throw new Exception("获取OSS URL失败");
		}finally{
			  if(ossClient!=null)ossClient.shutdown();
		} 
	}
	
	private static String getTencentCosAccessUrl(String bucketName,String objectKey,int expiresInSecond) throws Exception{
		COSClient cosclient = null;
		try{
			cosclient = new COSClient(cred, clientConfig);
			com.qcloud.cos.model.GeneratePresignedUrlRequest  req =
	                new com.qcloud.cos.model.GeneratePresignedUrlRequest(bucketName, objectKey, com.qcloud.cos.http.HttpMethodName.GET);
			com.qcloud.cos.model.ResponseHeaderOverrides responseHeaders = new com.qcloud.cos.model.ResponseHeaderOverrides();
			String cacheExpireStr =com.qcloud.cos.utils.DateUtils.formatRFC822Date(new Date(System.currentTimeMillis() + 24 * 3600 * 1000));
			responseHeaders.setContentType("image/png");
		    responseHeaders.setContentLanguage("zh-CN");
		    responseHeaders.setContentDisposition(null);
		    responseHeaders.setCacheControl("no-cache");
		    responseHeaders.setExpires(cacheExpireStr);
		    req.setResponseHeaders(responseHeaders);
		    
		    expiresInSecond = expiresInSecond==0?1*60*60:expiresInSecond;
		    
		    Date expirationDate = new Date(System.currentTimeMillis() + expiresInSecond * 1000);
	        req.setExpiration(expirationDate);
	        String url = cosclient.generatePresignedUrl(req).toString();
	        
	        //	https://hongjiu-1252785849.cos.ap-guangzhou.myqcloud.com/
	        url = url.replace("http://hongjiu-1252785849.cos.ap-guangzhou.myqcloud.com/", "https://weixunshi.com/shopcos/");

	        Cache.set(objectKey, url, "1h");
	        return url;
		}catch(Exception e){
			Logger.error(e, e.getMessage());
			throw new Exception("获取COS URL失败");
		}finally{
			if(cosclient!=null)cosclient.shutdown(); 
		} 
	}
	
	/**
	 * 微信统一下单接口
	 * @param appId
	 * @param mch_id
	 * @param body
	 * @param out_trade_no
	 * @param total_fee
	 * @param spbill_create_ip
	 * @param notify_url
	 * @param trade_type
	 * @param ext 扩展参数，也就是微信接口非必填的字段
	 * 微信接口文档地址：https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=9_1
	 * 提交微信统一下单接口数据：
		<?xml version="1.0" encoding="UTF-8"?>
		
		<xml>
		  <appid><![CDATA[wx1aebecae797c6598]]></appid>
		  <body><![CDATA[JSAPI支付测试]]></body>
		  <mch_id><![CDATA[1492743772]]></mch_id>
		  <nonce_str><![CDATA[XYY65XX10IY4FXU1X1E5G95Z495WJCD6]]></nonce_str>
		  <notify_url><![CDATA[http://wxpay.wxutil.com/pub_v2/pay/notify.v2.php]]></notify_url>
		  <openid><![CDATA[of54i0Qr5ikn8r2Ha09F6dueL61w]]></openid>
		  <out_trade_no><![CDATA[1512713446608]]></out_trade_no>
		  <spbill_create_ip><![CDATA[14.23.150.211]]></spbill_create_ip>
		  <total_fee><![CDATA[100]]></total_fee>
		  <trade_type><![CDATA[JSAPI]]></trade_type>
		  <sign><![CDATA[DD090DB2271A9AC82B16E0CF11414453]]></sign>
		</xml>
		
		提交微信统一下单接口响应数据：
		<xml><return_code><![CDATA[SUCCESS]]></return_code>
		<return_msg><![CDATA[OK]]></return_msg>
		<appid><![CDATA[wx1aebecae797c6598]]></appid>
		<mch_id><![CDATA[1492743772]]></mch_id>
		<nonce_str><![CDATA[eA15GCPhUGiU40Fq]]></nonce_str>
		<sign><![CDATA[CB53B768120BCBF7FC815FFE5AD1B1FB]]></sign>
		<result_code><![CDATA[SUCCESS]]></result_code>
		<prepay_id><![CDATA[wx20171208141049d6cb411f930275078530]]></prepay_id>
		<trade_type><![CDATA[JSAPI]]></trade_type>
		</xml>
	 *  @throws Exception 
	 */
	private static final String[] noceStrs = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
	public static Map<String,String> weixin_unifiedorder(String appid,String mch_id,String body,String out_trade_no,int total_fee,
			String spbill_create_ip,String notify_url,String trade_type,String key,Map<String,Object> ext) throws Exception{
		if(trade_type.equals("JSAPI") && (ext==null || !ext.containsKey("openid"))){
			throw new Exception("微信交易类型:JSAPI,必须有openid参数");
		}
		Map<String,String> treeMap = new TreeMap<String,String>();
		treeMap.put("appid", appid);
		treeMap.put("mch_id", mch_id);
		treeMap.put("body", body);
		treeMap.put("out_trade_no", out_trade_no);
		treeMap.put("total_fee", String.valueOf(total_fee));
		treeMap.put("spbill_create_ip", spbill_create_ip);
		treeMap.put("notify_url", notify_url);
		treeMap.put("trade_type", trade_type); 
		StringBuffer noceStr = new StringBuffer();
		for(int i=0;i<32;i++){
			int random = new Random().nextInt(noceStrs.length);
			noceStr.append(noceStrs[random]);
		}
		treeMap.put("nonce_str", noceStr.toString());
		//非必须字段
		if(ext!=null && ext.size()>0){
			Iterator<Map.Entry<String, Object>> it = ext.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry<String, Object> entry = it.next();
				Object value = entry.getValue();
				if(value == null ||  StringUtils.isEmpty(String.valueOf(value))){
					continue;
				}
				treeMap.put(entry.getKey(), String.valueOf(value)); 
			}
		}
		
		Document document = DocumentHelper.createDocument();  
		Element xmlElement = document.addElement("xml");
		
		StringBuffer params = new StringBuffer();
		Iterator<Map.Entry<String, String>> paramIt = treeMap.entrySet().iterator();
		while(paramIt.hasNext()){
			Map.Entry<String, String> entry = paramIt.next(); 
			String theKey = entry.getKey();
			String value = entry.getValue();
			params.append(theKey).append("=").append(value).append("&");
			Element theKeyElement = xmlElement.addElement(theKey);
			theKeyElement.addCDATA(value);  
		}
		params.append("key=").append(key);
		String StringA = params.toString();
		String sign = MD5Util.md5(StringA);
		treeMap.put("sign",sign);
		
		Element theKeyElement = xmlElement.addElement("sign");
		theKeyElement.addCDATA(sign);  
		
		OutputFormat format = OutputFormat.createPrettyPrint();  
		format.setEncoding("UTF-8");  
		
		StringWriter sw = new StringWriter();
		XMLWriter writer = new XMLWriter(sw, format);
		writer.write(document);
		writer.flush();
		writer.close(); 
		String requestBody = sw.toString();
		
		System.out.println("提交微信统一下单接口数据：");
		System.out.println(requestBody); 
		
		HttpResponse response = HttpUtils.doPost("https://api.mch.weixin.qq.com/", "pay/unifiedorder",  null, null, requestBody, "utf-8");
	
		if(response==null || response.getStatusLine()==null || response.getStatusLine().getStatusCode() != 200){
			throw new Exception("微信统一下单接口异常"+ new Gson().toJson(response));
		} 
		String respXml = EntityUtils.toString(response.getEntity(), "utf-8");
		
		System.out.println("提交微信统一下单接口响应数据：");
		System.out.println(respXml); 
		 
		Map<String,String> respTreeMap = new TreeMap<String,String>();
		Document reader = DocumentHelper.parseText(respXml);  
		Iterator<Element> childIt = reader.getRootElement().elementIterator();
		String respSign = "";
		while(childIt.hasNext()){
			Element child = childIt.next();
			String name = child.getName();
			String value = child.getText();
			if(name.equals("sign")){
				respSign = value;
				continue;
			}
			respTreeMap.put(name, value);
		}
		if(!respTreeMap.get("return_code").equals("SUCCESS")){
			throw new Exception("微信统一下单接口return_code!=SUCCESS,requestBody="+requestBody);
		}
		
		
		//验证相应签名是否正确
		StringBuffer respParams = new StringBuffer();
		Iterator<Map.Entry<String, String>> respParamsIt = respTreeMap.entrySet().iterator();
		while(respParamsIt.hasNext()){
			Map.Entry<String, String> entry = respParamsIt.next(); 
			String theKey = entry.getKey();
			String value = entry.getValue();
			respParams.append(theKey).append("=").append(value).append("&"); 
		}
		respParams.append("key=").append(key);
		String respStringA = respParams.toString();
		String mySign = MD5Util.md5(respStringA);
		if(!mySign.equals(respSign)){
			throw new Exception("响应的签名不正确，mySign="+mySign+",mySingString="+respStringA);
		}
		
		if(!respTreeMap.get("result_code").equals("SUCCESS")){
			throw new Exception("微信统一下单接口result_code!=SUCCESS,requestBody="+requestBody);
		}
		respTreeMap.put("nonce_str", noceStr.toString());
 		return respTreeMap;
		
	}
	
	
	public static Map<String,String> getLitterAppPayParams(String appid,String prepay_id,String key,String nonce_str){
		if(StringUtils.isEmpty(appid) || StringUtils.isEmpty(prepay_id)  ){
			return null;
		}
		Map<String,String> treeMap = new TreeMap<String,String>();
		treeMap.put("appId", appid);
		treeMap.put("timeStamp", String.valueOf(System.currentTimeMillis()/1000));
		StringBuffer noceStr = new StringBuffer();
		for(int i=0;i<32;i++){
			int random = new Random().nextInt(noceStrs.length);
			noceStr.append(noceStrs[random]);
		}
		treeMap.put("nonceStr", noceStr.toString());
		treeMap.put("package", "prepay_id="+prepay_id);
		treeMap.put("signType", "MD5");
		
		Logger.info("小程序调起参数：%s", treeMap);
		StringBuffer params = new StringBuffer();
		Iterator<Map.Entry<String, String>> paramIt = treeMap.entrySet().iterator();
		while(paramIt.hasNext()){
			Map.Entry<String, String> entry = paramIt.next(); 
			String theKey = entry.getKey();
			String value = entry.getValue();
			params.append(theKey).append("=").append(value).append("&");
		}
		params.append("key=").append(key);
		String StringA = params.toString();
		Logger.info("生成小程序前端支付签名串：%s", StringA);
		String sign = MD5Util.md5(StringA);
		treeMap.put("paySign",sign);
		return treeMap;
	}
	private static Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	public static JsonObject requestAccessToken(String appid){
		try{ 
			String queryString = String.format("?grant_type=client_credential&appid=%s&secret=%s",
					appid,
					Jws.configuration.getProperty(appid+".secret")
				);
			
			Request request = new Request("wx","token",queryString);
			Response response = HTTP.GET(request);			
		 
			if(response.getStatusCode()!=200){
				Logger.info("请求微信获取accessToken响应内容失败，返回状态非200");
				return null;
			}
			String respStr = response.getContent();
			Logger.info("请求微信获取accessToken响应内容，%s",respStr);
			return new JsonParser().parse(respStr).getAsJsonObject();
		} catch (Exception e) {
			Logger.error(e, e.getMessage());
			return null;
		} 
	}
	public static void sendWxMessage(String appId,String touserOpenid,String template_id,String page,String form_id,Map<String,Map> dataMap){
		try{
			if(Jws.configuration.getProperty("application.mode").equals("dev")){
				Logger.info("dev mode , do not send wx message.");
				return ;
			}
			String token = WXAccessTokenService.fromCache(appId);
			if(token==null)return ;
			
			FormIdService.updateUsed(appId, touserOpenid, form_id);
			
			String queryString = String.format("?access_token=%s",token);
			Request request = new Request("wx","msgSend",queryString);
			
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("touser", touserOpenid);
			params.put("template_id",template_id);
			params.put("page", page);
			params.put("form_id",form_id);
			params.put("data", dataMap); 
			
			String bodyStr = gson.toJson(params);
			
			Logger.info("请求微信发送模板消息body=%s", bodyStr);
			request.setBody(bodyStr.getBytes("UTF-8"));
			
			Response response = HTTP.POST(request);
			if(response.getStatusCode()==200){
				String respStr = response.getContent();
				Logger.info("请求微信发送模版消息响应内容，%s",respStr); 
			} 
		}catch(Exception e){
			Logger.error(e, e.getMessage());
		}
	}
	
	public static void main(String[] args) throws Exception{
		System.out.println(System.currentTimeMillis()/1000);
		/*System.setProperty("file.encoding", "GBK"); 
		System.out.println(System.getProperty("file.encoding"));*/
		//getBaiDuAccessToken("Gt6CSNg5vnBTGK5oPIRO6l4M","HA97b4Q2KjxfUC9p6La7DrSCGe1OvCoN");
	//	 nlpBaiDuLexer("24.a19ba76f051dce74c10a24d9ca8b8db4.2592000.1513134124.282335-10354281","我想吃番茄炒蛋");
		/*for(int i=1;i<=5;i++){
			String key = uploadToAliOss("tasty",new File("C:\\Users\\fish\\Desktop\\我是烹饪大师\\商城\\红枣\\s"+i+".jpg"));
			System.out.println(key);
		}*/		

		/*for(int i=1;i<=4;i++){
			String path = "C:\\Users\\fish\\Desktop\\青蛙读本\\元宵节\\png\\yuanxiaojie"+i+".png";
			String key = uploadToAliOss("tasty",new File(path));
			System.out.println(key);
		}*/
		
		//String path = "C:\\Users\\fish\\Desktop\\青蛙读本\\logo.png";
		//uploadImage("tasty",new File(path),0,0);
		//uploadImageToTencent(null,new File(path),0,0);
		//System.out.println(getObjectAccessUrl("tasty","0290abbbbcb94b048d8fa06360cbb453",60*10));
	//	System.out.println(getObjectAccessUrl("hongjiu-1252785849","COS_be7e706ddd024451944617610181e437",60*10));
		//String key = uploadToAliOss("tasty",new File(path));
		//System.out.println(key);
		
		
		//System.out.print(getObjectAccessUrl("tasty",key,3600));
		
		/*StringBuffer noceStr = new StringBuffer();
		for(int i=0;i<32;i++){
			int random = new Random().nextInt(noceStrs.length);
			noceStr.append(noceStrs[random]);
		}
		//CYQZS5KG2CI3DX5N201FAUD9EXU0P1YL
		System.out.println(noceStr.toString());*/
		
		
		//(String appid,String mch_id,String body,String out_trade_no,int total_fee,
		//		String spbill_create_ip,String notify_url,String trade_type,String key,Map<String,Object> ext)
		/*Map<String,Object> ext = new HashMap<String,Object>();
		ext.put("openid", "of54i0Qr5ikn8r2Ha09F6dueL61w");
		Map<String,String> result = weixin_unifiedorder("wx1aebecae797c6598","1492743772","青乐科技-测试",
				 "QL-20171208180046-1512727246429",1,
				"10.164.16.48","https://91loving.cn/proxy/cook/cookbook/wxPaynotify",
				"JSAPI","CYQZS5KG2CI3DX5N201FAUD9EXU0P1YL",ext);
		System.out.println(result);*/
		 
	}
	
}
