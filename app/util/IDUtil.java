package util;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class IDUtil {
	
	public static String gen(String prefix){
		String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		int random = new Random().nextInt(1000)+1000;
		return prefix+"-"+date+"-"+random;
	}
	
}
