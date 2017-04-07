package me.jarkimzhu.libs.utils;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DateUtils {

    private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);

    public static final String FMT_MONTH = "yyyy-MM";
    public static final String FMT_SHORT_DATE = "yyyy-MM-dd";
    public static final String FMT_LONG_DATE = "yyyy-MM-dd HH:mm:ss";
    public static final String FMT_TIME = "HH:mm";
    public static final SimpleDateFormat SDF_MONTH = new SimpleDateFormat(FMT_MONTH);
    public static final SimpleDateFormat SDF_SHORT_DATE = new SimpleDateFormat(FMT_SHORT_DATE);
    public static final SimpleDateFormat SDF_LONG_DATE = new SimpleDateFormat(FMT_LONG_DATE);
    public static final SimpleDateFormat SDF_TIME = new SimpleDateFormat(FMT_TIME);

    public static String getString(Date date) {
        if (date != null) {
            return SDF_LONG_DATE.format(date);
        } else {
            return null;
        }
    }

    public static String getDateString(Date date) {
        if (date != null) {
            return SDF_SHORT_DATE.format(date);
        } else {
            return null;
        }
    }

    public static String getString(Date date, DateFormat df) {
        if (date != null) {
            return df.format(date);
        } else {
            return null;
        }
    }

    public static String getString(Date date, String format) {
        if (date != null) {
            SimpleDateFormat f = new SimpleDateFormat(format);
            return f.format(date);
        } else {
            return null;
        }
    }

    public static Time getTime(String value) {
        try {
            if (!CommonUtils.isBlank(value)) {
                return new Time(SDF_TIME.parse(value).getTime());
            }
        } catch (ParseException e) {
            logger.error("getTime", e);
        }
        return null;
    }

    public static Date getDate(String value) {
        return getDate(value, SDF_LONG_DATE);
    }

    public static Date getDay(String value) {
        return getDate(value, SDF_SHORT_DATE);
    }

    public static Date getDate(String value, DateFormat df) {
        try {
            if (!CommonUtils.isBlank(value)) {
                return df.parse(value);
            }
        } catch (ParseException e) {
            logger.error("getDate", e);
        }
        return null;
    }

    public static Long getTimestamp(String value, DateFormat df) {
        Date date = getDate(value, df);
        if(date != null) {
            return date.getTime();
        } else {
            return null;
        }
    }

    public static Date getDateWithDifferDay(int differDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, differDay);
        return new Date(calendar.getTimeInMillis());
    }

    public static Date getDateWithDifferMonth(Date current, int differ) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current);
        calendar.add(Calendar.MONTH, differ);
        return new Date(calendar.getTimeInMillis());
    }


    public static Date getDateWithDifferHour(int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, hour);
        return new Date(calendar.getTimeInMillis());
    }

    public static Date getDateWithDifferDay(Date current, int differDay) {
        if (current != null) {
            return getDateWithDifferDay(current.getTime(), differDay);
        } else {
            return getDateWithDifferDay(differDay);
        }
    }

    public static Date getDateWithDifferDay(long current, int differDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(current);
        calendar.add(Calendar.DAY_OF_YEAR, differDay);
        return new Date(calendar.getTimeInMillis());
    }

    public static Date getDateWithDifferHour(long current, int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(current);
        calendar.add(Calendar.HOUR_OF_DAY, hour);
        return new Date(calendar.getTimeInMillis());
    }

    public static Date getDayStart() {
        return getDayStart(System.currentTimeMillis());
    }


    public static Date getDayEnd() {
        Calendar calendar = Calendar.getInstance();
        setDateToEndHMS(calendar);
        return new Date(calendar.getTimeInMillis());
    }

    public static Date getDayStart(long current) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(current);
        setDateToZeroHMS(calendar);
        return new Date(calendar.getTimeInMillis());
    }

    public static Date getDayStart(Date current) {
        if (current != null) {
            return getDayStart(current.getTime());
        } else {
            return getDayStart();
        }
    }

    public static Date getDayEnd(long current) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(current);
        setDateToEndHMS(calendar);
        return new Date(calendar.getTimeInMillis());
    }

    public static Date getDayEnd(Date current) {
        if (current != null) {
            return getDayEnd(current.getTime());
        } else {
            return getDayEnd();
        }
    }

    /**
     * 获取指定时间的周一日期对象
     *
     * @param current 指定时间戳
     * @return 周一00:00:00
     */
    public static Date getWeekStart(long current) {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(current);
        now.set(Calendar.DAY_OF_WEEK, now.getActualMinimum(Calendar.DAY_OF_WEEK));
        now.add(Calendar.DAY_OF_WEEK, 1);
        setDateToZeroHMS(now);
        return new Date(now.getTimeInMillis());
    }

    public static Date getWeekStart(Date current) {
        if (current != null) {
            return getWeekStart(current.getTime());
        } else {
            return getWeekStart();
        }
    }

    public static Date getWeekStart() {
        return getWeekStart(System.currentTimeMillis());
    }

    /**
     * 获取指定时间的周天日期对象
     *
     * @param current 指定时间戳
     * @return 周天23:59:59
     */
    public static Date getWeekEnd(long current) {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(current);
        now.set(Calendar.DAY_OF_WEEK, now.getActualMaximum(Calendar.DAY_OF_WEEK));
        now.add(Calendar.DAY_OF_WEEK, 1);
        setDateToEndHMS(now);
        return new Date(now.getTimeInMillis());
    }

    public static Date getWeekEnd(Date current) {
        if (current != null) {
            return getWeekEnd(current.getTime());
        } else {
            return getWeekEnd();
        }
    }

    public static Date getWeekEnd() {
        return getWeekEnd(System.currentTimeMillis());
    }

    public static Date getMonthStart() {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.DAY_OF_MONTH, now.getActualMinimum(Calendar.DAY_OF_MONTH));
        setDateToZeroHMS(now);
        return new Date(now.getTimeInMillis());
    }

    public static Date getMonthStart(long date) {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(date);
        now.set(Calendar.DAY_OF_MONTH, now.getActualMinimum(Calendar.DAY_OF_MONTH));
        setDateToZeroHMS(now);
        return new Date(now.getTimeInMillis());
    }

    public static Date getMonthStart(Date date) {
        if (date != null) {
            return getMonthStart(date.getTime());
        } else {
            return getMonthStart();
        }
    }

    public static Date getMonthEnd() {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.DAY_OF_MONTH, now.getActualMaximum(Calendar.DAY_OF_MONTH));
        setDateToZeroHMS(now);
        return new Date(now.getTimeInMillis());
    }

    public static Date getMonthEnd(long date) {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(date);
        now.set(Calendar.DAY_OF_MONTH, now.getActualMaximum(Calendar.DAY_OF_MONTH));
        setDateToZeroHMS(now);
        return new Date(now.getTimeInMillis());
    }

    public static Date getMonthEnd(Date date) {
        if (date != null) {
            return getMonthEnd(date.getTime());
        } else {
            return getMonthEnd();
        }
    }

    public static Set<java.sql.Date> getSerialSqlDate(java.sql.Date startDate, java.sql.Date endDate) {
        return getSerialSqlDate(startDate.getTime(), endDate.getTime());
    }

    public static Set<java.sql.Date> getSerialSqlDate(long startTime, long endTime) {
        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(endTime);
        setDateToZeroHMS(calendar);
        java.sql.Date endDate = new java.sql.Date(calendar.getTimeInMillis());

        calendar.setTimeInMillis(startTime);
        setDateToZeroHMS(calendar);
        java.sql.Date startDate = new java.sql.Date(calendar.getTimeInMillis());

        Set<java.sql.Date> results = new LinkedHashSet<java.sql.Date>();
        results.add(startDate);
        int differDay = getDifferDay(startDate, endDate);
        for (int i = 0; i < differDay; i++) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            java.sql.Date next = new java.sql.Date(calendar.getTimeInMillis());
            results.add(next);
        }
        return results;
    }

    /**
     * 计算两个日期相差的天数
     *
     * @param startDate 起始日期
     * @param endDate   结束日期
     * @return BEWTEEN startDate AND endDate
     */
    public static int getDifferDay(Date startDate, Date endDate) {
        long startTime = startDate.getTime();
        long endTime = endDate.getTime();
        return getDifferDay(startTime, endTime);
    }


    /**
     * 判断end时间是否在start之后
     *
     * @param startDate 起始日期
     * @param endDate   结束日期
     * @return
     */
    public static boolean isAfterDate(Date startDate, Date endDate) {
        return endDate.after(startDate);
    }

    public static int getDifferDay(long startTime, long endTime) {
        return (int) ((endTime - startTime) / 1000 / 60 / 60 / 24) + 1;
    }

    public static int getDifferHour(Date startDate, Date endDate) {
        long startTime = startDate.getTime();
        long endTime = endDate.getTime();
        return (int) ((endTime - startTime) / 1000 / 60 / 60);
    }

    private static void setDateToZeroHMS(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private static void setDateToEndHMS(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    public static int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        return calendar.get(Calendar.YEAR);
    }

    public static int getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        return calendar.get(Calendar.MONTH);
    }

    public static int getDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static boolean betweenDate(long timestamp, Date date) {
        return timestamp >= getDayStart(date.getTime()).getTime() && timestamp <= getDayEnd(date.getTime()).getTime();
    }

    public static boolean between(Date start, Date end, long test) {
        if (start != null && end != null) {
            return test >= start.getTime() && test <= end.getTime();
        } else if (start != null && end == null) {
            return test >= start.getTime();
        } else if (start == null && end != null) {
            return test <= end.getTime();
        } else {
            return true;
        }
    }

    public static boolean between(Date start, Date end, Date test) {
        return test != null && between(start, end, test.getTime());
    }

    public static boolean between(Date start, Date end) {
        return between(start, end, System.currentTimeMillis());
    }

    /**
     * 计算2个【日期之间相差】的天数,用现在的日期减去以前的日期
     *
     * @param now  现在的时间，格式为yyyy-MM-dd
     * @param fore 以前的时间，格式为yyyy-MM-dd
     * @return
     * @author chenhui
     */
    public static Long betweenDay(String now, String fore) {
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date nowDate = myFormatter.parse(now);
            Date foreDate = myFormatter.parse(fore);
            long count = (nowDate.getTime() - foreDate.getTime()) / (24 * 60 * 60 * 1000);
            return count;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    //当前时间
    public static Timestamp getNowTime() {
        return new java.sql.Timestamp(System.currentTimeMillis());
    }

    // 取得一个自定义事件
    public static String getCurrentDate(String dataFormat) {
        SimpleDateFormat format = new SimpleDateFormat(dataFormat);
        return format.format(new Date());
    }

    public static String getAge(Date date) {
        int year = getYear(date);
        int now = getYear(new Date());
        return now - year + "";
    }

    public static LocalDate getLastDate4Today(int number) {
        LocalDate time = LocalDate.now().minus(number, ChronoUnit.DAYS);
        return time;
    }


    /**
     * 根据给定 [格式] 取得当前日期
     *
     * @param dateFormat
     * @return
     * @author chenhui
     */
    public static String getCurrentDay(String dateFormat) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        return format.format(new Date());
    }


    /**
     * 取前N或后N天的Timestamp型日期,amount为正数时取后N天,为负时,取前N天
     * 思路:
     * 先取yyyy-MM-dd格式的当前日期字符串,
     * 再解析成Date类型,提供给Calendar,
     * 利用Calendar的add方法进行前N或后N天
     * 最后取Calendar的TimeInMillis,
     * 并提供给Timestampe的构造
     *
     * @param dateStr 日期,格式必须是yyyy-MM-dd
     * @param amount
     * @return
     * @author chenhui  2006-2-16
     */
    public static Timestamp rollDayTimestamp(String dateStr, int amount) {

        //先取yyyy-MM-dd格式的当前日期字符串
        String nowDate = dateStr;

        if (null == nowDate || nowDate.trim().equals("")) {
            nowDate = getCurrentDay("yyyy-MM-dd");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


        //再解析成Date类型,提供给Calendar
        Calendar nowCalendar = Calendar.getInstance();

        try {
            nowCalendar.setTime(sdf.parse(nowDate));
        } catch (ParseException e) {
            // TODO 自动生成 catch 块
            e.printStackTrace();
        }

        //利用Calendar的add方法进行前N或后N天
        nowCalendar.add(Calendar.DATE, amount);    //滚动

        //最后取Calendar的TimeInMillis,并提供给Timestampe的构造
        Timestamp timestamp = new Timestamp(nowCalendar.getTimeInMillis());

//		System.err.println("rollDayTimestamp: " + timestamp.toString());

        return timestamp;
    }

    public static LocalDate today() {
        return LocalDate.now();
    }
}
