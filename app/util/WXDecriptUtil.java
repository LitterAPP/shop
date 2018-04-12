package util;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class WXDecriptUtil { 
	 private static final Charset CHARSET = Charset.forName("utf-8");  
	 private static final int BLOCK_SIZE = 32;  
	 public static byte[] encode(int count) {  
	        // 计算需要填充的位数  
	        int amountToPad = BLOCK_SIZE - (count % BLOCK_SIZE);  
	        if (amountToPad == 0) {  
	            amountToPad = BLOCK_SIZE;  
	        }  
	        // 获得补位所用的字符  
	        char padChr = chr(amountToPad);  
	        String tmp = new String();  
	        for (int index = 0; index < amountToPad; index++) {  
	            tmp += padChr;  
	        }  
	        return tmp.getBytes(CHARSET);  
	    }  
	    /** 
	     * 删除解密后明文的补位字符 
	     * 
	     * @param decrypted 解密后的明文 
	     * @return 删除补位字符后的明文 
	     */  
	    public static byte[] decode(byte[] decrypted) {  
	        int pad = decrypted[decrypted.length - 1];  
	        if (pad < 1 || pad > 32) {  
	            pad = 0;  
	        }  
	        return Arrays.copyOfRange(decrypted, 0, decrypted.length - pad);  
	    }  
	    /** 
	     * 将数字转化成ASCII码对应的字符，用于对明文进行补码 
	     * 
	     * @param a 需要转化的数字 
	     * @return 转化得到的字符 
	     */  
	    public static char chr(int a) {  
	        byte target = (byte) (a & 0xFF);  
	        return (char) target;  
	    }  
	    
	    public static String SHA1(String str) {
	        try {
	            MessageDigest digest = java.security.MessageDigest
	                    .getInstance("SHA-1"); //如果是SHA加密只需要将"SHA-1"改成"SHA"即可
	            digest.update(str.getBytes());
	            byte messageDigest[] = digest.digest();
	            // Create Hex String
	            StringBuffer hexStr = new StringBuffer();
	            // 字节数组转换为 十六进制 数
	            for (int i = 0; i < messageDigest.length; i++) {
	                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
	                if (shaHex.length() < 2) {
	                    hexStr.append(0);
	                }
	                hexStr.append(shaHex);
	            }
	            return hexStr.toString();

	        } catch (NoSuchAlgorithmException e) {
	            e.printStackTrace();
	        }
	        return null;
	    }

	    public static String MD5(String str) {
	        try {
	            // 获得MD5摘要算法的 MessageDigest 对象
	            MessageDigest mdInst = MessageDigest.getInstance("MD5");
	            // 使用指定的字节更新摘要
	            mdInst.update(str.getBytes());
	            // 获得密文
	            byte[] md = mdInst.digest();
	            // 把密文转换成十六进制的字符串形式
	            StringBuffer hexString = new StringBuffer();
	            // 字节数组转换为 十六进制 数
	            for (int i = 0; i < md.length; i++) {
	                String shaHex = Integer.toHexString(md[i] & 0xFF);
	                if (shaHex.length() < 2) {
	                    hexString.append(0);
	                }
	                hexString.append(shaHex);
	            }
	            return hexString.toString();
	        } catch (NoSuchAlgorithmException e) {
	            e.printStackTrace();
	        }
	        return null;
	    } 
}
