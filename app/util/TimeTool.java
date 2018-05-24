package util;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by shanmao on 15-12-5.
 */


public class TimeTool {

    /**
     * 获得当前时间的unix时间戳
     * @return
     */
    public static long getCurrentUnixTimestamp(){
        return System.currentTimeMillis()/1000;
    }

    public static long getCurrentUnixTimestampInMillis(){
        return System.currentTimeMillis();
    }

    public static String getTimeStringByUnixTimestampInMillis(String pattern,long UnixTimestampInMillis){
        Date date = null;
        if(UnixTimestampInMillis < 0){
            date = new Date();
        }else{
            date = new Date(UnixTimestampInMillis);
        }
        if(pattern == null){
            return date.toString();
        }
        return new java.text.SimpleDateFormat(pattern).format(date);
    }

    public static String getTimeStringByUnixTimestamp(String pattern,long UnixTimestamp){
        return getTimeStringByUnixTimestampInMillis(pattern,UnixTimestamp*1000);
    }

    public static String getNowString(String pattern){
        Date date = new Date();
        if(pattern == null){
            return date.toString();
        }
        return new java.text.SimpleDateFormat(pattern).format(date);
    }

    public static long getUnixTimestampByStr(String pattern,String time) throws ParseException {
        Date n = new SimpleDateFormat(pattern).parse(time);
        return n.getTime()/1000;
    }

    public static long getUnixTimestampByyyyyMMdd(String timestr) throws ParseException {
        return getUnixTimestampByStr("yyyyMMdd",timestr);
    }

    public static String getByyyyyMMdd(long t) throws ParseException {
        return getTimeStringByUnixTimestamp("yyyyMMdd",t);

    }

    public static long getTodayUnixTimestamp() throws ParseException {
        return getUnixTimestampByyyyyMMdd(getByyyyyMMdd(getCurrentUnixTimestamp()));

    }
}