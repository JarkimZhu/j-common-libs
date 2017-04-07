package me.jarkimzhu.libs.redis.utils;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DateUtils {
	
	//当前时间
    public static Timestamp getNowTime(){
        return new java.sql.Timestamp(System.currentTimeMillis());
    }
	
	// 取得一个自定义事件
	public static String getCurrentDate( String dataFormat ){
		SimpleDateFormat format = new SimpleDateFormat( dataFormat );
		return format.format( new Date() );
	}
 
	
}
