package util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class QQSMSUtil {
	
	private static Gson gson = new Gson();
	private static final String appkey="d0bf30c2db1a84f1d0e9ff06761294b8";
	private static final int appid= 1400049575;
	private static final String smsUrl = "https://yun.tim.qq.com/v5/tlssmssvr/sendsms";

    protected static Random random = new Random();

    public static String stringMD5(String input) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        byte[] inputByteArray = input.getBytes();
        messageDigest.update(inputByteArray);
        byte[] resultByteArray = messageDigest.digest();
        return byteArrayToHex(resultByteArray);
    }

    protected static String strToHash(String str) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] inputByteArray = str.getBytes();
        messageDigest.update(inputByteArray);
        byte[] resultByteArray = messageDigest.digest();
        return byteArrayToHex(resultByteArray);
    }

    public static String byteArrayToHex(byte[] byteArray) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] resultCharArray = new char[byteArray.length * 2];
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }
        return new String(resultCharArray);
    }

    public static int getRandom() {
    	return random.nextInt(999999)%900000+100000;
    }

    public static HttpURLConnection getPostHttpConn(String url) throws Exception {
        URL object = new URL(url);
        HttpURLConnection conn;
        conn = (HttpURLConnection) object.openConnection();
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(60000);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestMethod("POST");
        return conn;
	}

    public static String calculateSig(
    		String appkey,
    		long random,
    		String msg,
    		long curTime,
    		ArrayList<String> phoneNumbers) throws NoSuchAlgorithmException {
        String phoneNumbersString = phoneNumbers.get(0);
        for (int i = 1; i < phoneNumbers.size(); i++) {
            phoneNumbersString += "," + phoneNumbers.get(i);
        }
        return strToHash(String.format(
        		"appkey=%s&random=%d&time=%d&mobile=%s",
        		appkey, random, curTime, phoneNumbersString));
    }

    public static String calculateSigForTempl(
    		String appkey,
    		long random,
    		long curTime,
    		ArrayList<String> phoneNumbers) throws NoSuchAlgorithmException {
        String phoneNumbersString = phoneNumbers.get(0);
        for (int i = 1; i < phoneNumbers.size(); i++) {
            phoneNumbersString += "," + phoneNumbers.get(i);
        }
        return strToHash(String.format(
        		"appkey=%s&random=%d&time=%d&mobile=%s",
        		appkey, random, curTime, phoneNumbersString));
    }

    public static String calculateSigForTempl(
    		String appkey,
    		long random,
    		long curTime,
    		String phoneNumber) throws NoSuchAlgorithmException {
    	ArrayList<String> phoneNumbers = new ArrayList<String>();
    	phoneNumbers.add(phoneNumber);
    	return calculateSigForTempl(appkey, random, curTime, phoneNumbers);
    }
    
    
    public static boolean sendWithParam(
			String nationCode,
			String phoneNumber,
			int templId,
			List<String> params,
			String sign,
			String extend,
			String ext) throws Exception {
    	
			/*
			请求包体
			{
			    "tel": {
			        "nationcode": "86",
			        "mobile": "13788888888"
			    },
			    "sign": "腾讯云",
			    "tpl_id": 19,
			    "params": [
			        "验证码",
			        "1234",
			        "4"
			    ],
			    "sig": "fdba654e05bc0d15796713a1a1a2318c",
			    "time": 1479888540,
			    "extend": "",
			    "ext": ""
			}
			应答包体
			{
			    "result": 0,
			    "errmsg": "OK",
			    "ext": "",
			    "sid": "xxxxxxx",
			    "fee": 1
			}
		*/
		if (null == nationCode || 0 == nationCode.length()) {
			nationCode = "86";
		}
		if (null == params) {
			params = new ArrayList<String>();
		}
		if (null == sign) {
			sign = "";
		}
		if (null == extend) {
			extend = "";
		}
		if (null == ext) {
			ext = "";
		}

		long randomValue = random.nextInt(999999)%900000+100000;
		long curTime = System.currentTimeMillis()/1000;

		Map<String,Object> tel = new HashMap<String,Object>();
		Map<String,Object> data = new HashMap<String,Object>();
        
        tel.put("nationcode", nationCode);
        tel.put("mobile", phoneNumber);

        data.put("tel", tel);
        data.put("sig", calculateSigForTempl(appkey, randomValue, curTime, phoneNumber));
        data.put("tpl_id", templId);
        data.put("params", params);
        data.put("sign", sign);
        data.put("time", curTime);
        data.put("extend", extend);
        data.put("ext", ext);

		String wholeUrl = String.format("%s?sdkappid=%d&random=%d", smsUrl, appid, randomValue);
        HttpURLConnection conn = getPostHttpConn(wholeUrl);

        System.out.println(gson.toJson(data));
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
        wr.write(gson.toJson(data));
        wr.flush();

        // 显示 POST 请求返回的内容
        StringBuilder sb = new StringBuilder();
        int httpRspCode = conn.getResponseCode();
      
        if (httpRspCode == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            JsonObject json = new JsonParser().parse(sb.toString()).getAsJsonObject();
            System.out.println(json);
            if( json.get("result").getAsInt() == 0){
            	return true;
            }else{
            	return false;
            }
        } else {
        	return false;
        } 
	}
    
    /**\
     * String nationCode,
			String phoneNumber,
			int templId,
			ArrayList<String> params,
			String sign,
			String extend,
			String ext)
     * @param args
     */
    public static void main(String[] args){
    	try {
    		List<String> params = new ArrayList<String>();
    		params.add("123456");
    		params.add("15");
			boolean result = sendWithParam("86","13726759844",70567,null,"青乐科技",null,null);
			System.out.println(result);
		} catch (Exception e) { 
			e.printStackTrace();
		}
    }
    
}