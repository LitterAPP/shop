package util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jws.Logger;

public class DateUtil {
	private static final DateFormat defaultDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static String format(long time,String pt){
		DateFormat df = new SimpleDateFormat(pt);
		return df.format(new Date(time));
	}
	
	public static String format(long time){
		return defaultDf.format(new Date(time));
	}
	
	public static long getTime(String timeStr){
		try {
			return defaultDf.parse(timeStr).getTime();
		} catch (ParseException e) {
			Logger.error(e, e.getMessage());
		}
		return 0;
	}
	
	public static String timeDesc(long time){
		long diff = System.currentTimeMillis() - time;
		if(diff<0){
			return "外星人时间";
		}
		long day = diff/(24*60*60*1000);
		if(day>0){
			if(day>10){
				return format(time,"yyyy-MM-dd");
			}
			return day+"天前";
		}
		long hour = diff/(60*60*1000);
		if(hour>0){
			return hour+"小时前";
		}
		long min = diff/(60*1000);
		if(min>0){
			return min+"分钟前";
		}
		return "1分钟前";
	}
	
	public static void main(String[] args){
		System.out.println(timeDesc(10000));
	}
}
