package com.bamboo.alibak.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;

public class DateUtils {

	/**
	 * 获取格林氏时间
	 * @param date
	 * @return
	 */
	public static String getISO8601Time(Date date, String FORMAT_ISO8601, String TIME_ZONE) {
    	Date nowDate = date;
    	if (null == date){
    		nowDate = new Date();
    	}
        SimpleDateFormat df = new SimpleDateFormat(FORMAT_ISO8601);
        df.setTimeZone(new SimpleTimeZone(0, TIME_ZONE));
        
        return df.format(nowDate);
    }
	
}
