package utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jws.Logger;

import org.apache.commons.lang.StringUtils;

public class DateUtil {

	public static final String FULL_SDM_MILL = "yyyy-MM-dd HH:mm:ss.SSS";
	
	/** 常规日期格式，24小时制格式  **/
    private static final String NORMAL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    private static final String SPEC_DATE_FORMAT = "yyyyMMddHHmm";
    
    private static final String ONLY_DATE_FORMAT = "yyyy-MM-dd";
    private static final String ONLY_DATE_FORMAT2 = "yyyyMMdd";
    
    private static final String NORMAL_DATE_FORMAT2 = "yyyy-MM-dd HH:mm";
    
    private static final String MONTH_FORMAT = "yyyyMM";
    
    private static final String HOUR_MIN_FORMAT = "HH:mm";
    
    private static final String ONLY_DAY_FORMAT = "MM-dd";
    private static final long THREE_MONTH_TS = 3*31*24 * 60 * 60 * 1000L;
    private static final long SIX_MONTH_TS = 180*24 * 60 * 60 * 1000L;
    public static final long ONE_HOUR_TS = 1 * 60 * 60 * 1000L - 1;
    public static final long ONE_DAY_TS = 24 * 60 * 60 * 1000L - 1;
    public static final long TWO_DAY_TS = 48 * 60 * 60 * 1000L - 1;
    public static final long ONE_WEEK_TS = 7*24* 60 * 60 * 1000L - 1;
    public static final long TWO_WEEK_TS = 14*24* 60 * 60 * 1000L - 1;
    public static final long FIVE_MINUTES_TS = 5 * 60 * 1000L - 1;
    private static final long FUTURE_TIME = 4070880000000L;
    
    public static void main(String[] args) throws ParseException {
//    	System.out.println(getDaysFromTo("2015-10-05", "2015-10-12"));
    	SimpleDateFormat sdf = new SimpleDateFormat(NORMAL_DATE_FORMAT);
    	System.err.println(sdf.format(getNextMonthTime(0)));
    }
    
    /**
     * 获取日期内的日期列表，最大日期取今天和to中的最小值
     * @param from
     * @param to
     * @return
     */
    public static List<String> getDaysFromTo(String from, String to) {
    	List<String> days = new ArrayList<String>();
    	SimpleDateFormat sdf = new SimpleDateFormat(ONLY_DATE_FORMAT);
    	Date todayDay = new Date();
    	
		try {
			Date tempDate = sdf.parse(from);
			Calendar calendar = Calendar.getInstance();
	    	calendar.setTime(tempDate);
	    	
	    	while(calendar.getTime().before(todayDay)) {
	    		String day = sdf.format(calendar.getTime());
	    		days.add(day);
	    		calendar.add(Calendar.DAY_OF_MONTH, 1);
	    		if (day.equals(to)) {
	    			break;
	    		}
	    	}
		} catch (ParseException e) {
			Logger.error(e, "");
		}
    	
    	return days;
    }
    
    /**
     * 获取该月的天数
	 * @param month yyyyMM
	 * @return
	 */
	 public static Integer getDaysOfMonth(int month) {
		 SimpleDateFormat sdf = new SimpleDateFormat(MONTH_FORMAT);
		 Calendar calendar = Calendar.getInstance();
		 String monthStr = String.valueOf(month);
	 	 try {
	 		 calendar.setTime(sdf.parse(monthStr));
	 	 } catch (ParseException e) {
	 		Logger.error(e, "getDaysOfMonth error");
	 	 }
	 	 
	 	 return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }
	 
	/**
	 * 获取该月的第一天
	 * @param month yyyyMM
	 * @return
	 */
	public static long getFirstDayOfMonth(int month) {
		SimpleDateFormat sdf = new SimpleDateFormat(MONTH_FORMAT);
		 Calendar calendar = Calendar.getInstance();
		 String monthStr = String.valueOf(month);
	 	 try {
	 		 calendar.setTime(sdf.parse(monthStr));
	 		 calendar.set(Calendar.DAY_OF_MONTH, 1);
	 	 } catch (ParseException e) {
	 		Logger.error(e, "getDaysOfMonth error");
	 	 }
	 	 
	 	 return calendar.getTimeInMillis();
	}
	 
	/**
	 * 计算两个日期之间的天数
	 * @param from
	 * @param to
	 * @return
	 */
	public static int getDaysBetween(long from, long to) {
		SimpleDateFormat sdf = new SimpleDateFormat(ONLY_DATE_FORMAT);
		String fromStr = sdf.format(new Date(from));
		String toStr = sdf.format(new Date(to));
		
		try {
			Date fromDate = sdf.parse(fromStr);
			Date toDate = sdf.parse(toStr);
			long between = toDate.getTime() - fromDate.getTime();
			int days = (int) (between / ONE_DAY_TS);
			return days;
			
		} catch (Exception e) {
			Logger.error(e, "getDaysBetween error");
		}

		return 0;
	}
    
	/**
	 * 获取当前时间，日期格式为默认的yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String getCurrentDate(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date());
	}
	
	/**
	 * 获取当前时间，日期格式为yyyyMMddHHmm
	 * @return
	 */
	public static String getSpecCurrentDate() {
		SimpleDateFormat sdf = new SimpleDateFormat(SPEC_DATE_FORMAT);
        return sdf.format(new Date());
	}
	
	/**
	 * 毫秒数转换为常见日期格式的字符串，如yyyy-MM-dd HH:mm:ss
	 * @param time
	 * @return
	 */
	public static String getDateString(Long time) {
		if(time == null || time <=0){
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(NORMAL_DATE_FORMAT);
		return sdf.format(new Date(time));
	}
	
	/**
	 * 常见日期格式的字符串，如yyyy-MM-dd HH:mm:ss转换为毫秒数，如果传入的是HH:mm:ss格式的，
	 * 则认为是当天的日期，加上当天的年月日，进行转换
	 * @param timeStr
	 * @return
	 */
	public static Long getDateTime(String timeStr) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat();
			
			if(timeStr.split(" ").length == 1) {
				sdf.applyPattern(ONLY_DATE_FORMAT);
				timeStr = sdf.format(new Date()) + " " + timeStr; 
			}
			sdf.applyPattern(NORMAL_DATE_FORMAT);
			return sdf.parse(timeStr).getTime();
		} catch (ParseException e) {
			Logger.error(e, "DateUtil - getDateTime");
		}
		return null;
	}
	
	
	
	public static String getDay() {
		SimpleDateFormat sdf = new SimpleDateFormat(ONLY_DAY_FORMAT);
		return sdf.format(new Date());
	}
	
	/**
	 * 获取某天凌晨的毫秒时间
	 * @param time
	 * @return
	 */
	public static long getMorning(Date d) {
		Calendar currentDate = Calendar.getInstance();   
		currentDate.setTime(d);
		currentDate.set(Calendar.HOUR_OF_DAY, 0);  
		currentDate.set(Calendar.MINUTE, 0);  
		currentDate.set(Calendar.SECOND, 0);  
		return currentDate.getTimeInMillis();
	}
	
	/**
	 * 获取某天凌晨的毫秒时间
	 * @param time
	 * @return
	 */
	public static long getOtherDayMorning(int offset) {
		Calendar currentDate = Calendar.getInstance();  
		currentDate.setTime(new Date());
		currentDate.add(Calendar.DATE, offset);
		currentDate.set(Calendar.HOUR_OF_DAY, 0);  
		currentDate.set(Calendar.MINUTE, 0);  
		currentDate.set(Calendar.SECOND, 0);  
		return currentDate.getTimeInMillis();
	}
	
	public static String getYesterdayDate(String format) {
		Calendar cal=Calendar.getInstance();
        cal.add(Calendar.DATE,-1);
        Date d=cal.getTime();

		SimpleDateFormat sp=new SimpleDateFormat(format);
		String date=sp.format(d);//获取昨天日期
		
		return date;
	}
	
	
	/**
	 * 获取上个月第一天凌晨的毫秒时间
	 * @param time
	 * @return
	 */
	public static long getMonthFristDayMorning(int offset) {
		Calendar currentDate = Calendar.getInstance();  
		currentDate.add(Calendar.MONTH, offset);
		currentDate.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天   
		currentDate.set(Calendar.HOUR_OF_DAY, 0);  
		currentDate.set(Calendar.MINUTE, 0);  
		currentDate.set(Calendar.SECOND, 0);  
		return currentDate.getTimeInMillis();
	}

	 /**
     * 以某种格式格式化当前时间
     * @param sdm
     * @return
     */
    public static String parseNow2Str(String sdm){
        return new SimpleDateFormat(sdm).format(new Date());
    }
    
    /**
     * 返回当前时间的int值。
     * 
     * @param date
     * @return
     */
	public static Integer parseNowToInt() {
		return (int)(System.currentTimeMillis() / 1000);
	}
	
	/**
	 * 给日期增加/减少指定天数,主要给统计格式用
	 * @param sdate
	 * @param days
	 * @return
	 */
	public static String addDay4StatFormat(String sdate, int days) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyyMMdd").parse(sdate);
		} catch (ParseException e) {
			return "";
		}
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(calendar.DATE, days);// 把日期往后增加days天.整数往后推,负数往前移动
		return sdf.format(calendar.getTime());
	}
	   
    /**
     * 根据传进来的时间往前或往后推一定天数的时间
     * @param src
     * @param days
     * @return
     */
    public static long addDay(long src, int days) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date(src));
        calendar.add(calendar.DATE, days);// 把日期往后增加days天.整数往后推,负数往前移动
        return calendar.getTimeInMillis();
    }
    
    /**
     * 添加时间
     * @param date
     * @param seconds
     * @return
     */
    public static Date addDay(Date date, int day){
    	if(date == null){
			date = new Date();
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.SECOND, day*24*60*60);
		return calendar.getTime();
	}
	
	/**
	 * 返回服务器对应的明天之前(含明天)的历史3个月数据，起始结束时间戳
	 * @return
	 */
	public static long[] getThreeMonthStartEndTime() {
	    return getStartEndTimeByStep(THREE_MONTH_TS);
	}
	/**
	 * 返回服务器对应的明天之前(含明天)的历史6个月数据，起始结束时间戳
	 * @return
	 */
	public static long[] getHalfYearStartEndTime() {
	    return getStartEndTimeByStep(SIX_MONTH_TS);
	}
   
   public static long[] getStartEndTimeByStep(long step)
   {
       long[] rst = new long[2];
       try {
           SimpleDateFormat dateF = new SimpleDateFormat("yyyyMMdd");
           Date curDate = new Date();
           String dateStr = dateF.format(curDate);
           Date bDate = dateF.parse(dateStr);
           rst[0] = bDate.getTime() - step;
           rst[1] = bDate.getTime() + TWO_DAY_TS;
       } catch (Exception e) {
           //ignore;
       }
       return rst;
   }
   
   /**
    * 返回指定周期的起止时间
    * 起始时间是：日期对应起始时间点，例如：2015-06-14 00:00:00
    * 结束时间是：日期对应的结束时间，例如：2015-06-14 23:59:59
    * @param step
    * @return
    */
   public static long[] getStartEndTimeByStep2(long step)
   {
       long[] rst = new long[2];
       try {
           SimpleDateFormat dateF = new SimpleDateFormat("yyyyMMdd");
           Date curDate = new Date();
           String dateStr = dateF.format(curDate);
           Date bDate = dateF.parse(dateStr);
           long bTS = bDate.getTime();
           long eTS = bDate.getTime()+ONE_DAY_TS;
           rst[0] = bTS;
           rst[1] = eTS + step;
       } catch (Exception e) {
           //ignore;
       }
       return rst;
   }
   
   public static long[] getOneWeek()
   {
       return getStartEndTimeByStep2(ONE_WEEK_TS);
   }
   
   public static long[] getTwoWeek()
   {
       return getStartEndTimeByStep2(TWO_WEEK_TS);
   }
   
   public static long[] getPermanent()
   {
	   long[] rst = new long[2];
       try {
           SimpleDateFormat dateF = new SimpleDateFormat("yyyyMMdd");
           Date curDate = new Date();
           String dateStr = dateF.format(curDate);
           Date bDate = dateF.parse(dateStr);
           long bTS = bDate.getTime();
           rst[0] = bTS;
           rst[1] = FUTURE_TIME;
       } catch (Exception e) {
           //ignore;
       }
       return rst;
   }
   
   /**
    * 返回服务器对应的当前天，起始结束时间戳
    * @return
    */
   public static long[] getTodayStartEndTime() {
       return getStartEndTime(System.currentTimeMillis());
   }
   
   /**
    * 根据指定的时间戳，返回对应天的起始时间
    * @param timsStamp
    * @return
    */
   public static long[] getStartEndTime(long timsStamp)
   {
       long[] rst = new long[2];
       try {
           SimpleDateFormat dateF = new SimpleDateFormat("yyyyMMdd");
           Date curDate = new Date(timsStamp);
           String dateStr = dateF.format(curDate);
           Date bDate = dateF.parse(dateStr);
           rst[0] = bDate.getTime();
           rst[1] = rst[0] + ONE_DAY_TS;
       } catch (Exception e) {
           //ignore;
       }
       return rst;
   }
   
   /**
    * 获取昨天的起止时间戳
    * @return
    */
   public static long[] getYesterdayStartEndTime() {
	   long[] rst = new long[2];
       try {
           SimpleDateFormat dateF = new SimpleDateFormat("yyyyMMdd");
           long oneDayAgo = getOneDayAgo();
           Date yesterdayDate = new Date(oneDayAgo);
           String dateStr = dateF.format(yesterdayDate);
           Date bDate = dateF.parse(dateStr);
           rst[0] = bDate.getTime();
           rst[1] = rst[0] + ONE_DAY_TS;
       } catch (Exception e) {
           //ignore;
       }
       return rst;
   }
   
   /**
    * 获取当前时间到明天天的起止时间戳
    * @return
    */
   public static long[] getYes2TomStartEndTime()
   {
       long[] rst = new long[2];
       long curTS = System.currentTimeMillis();
       rst[0] = curTS-ONE_DAY_TS;
       rst[1] = curTS + ONE_DAY_TS;
       return rst;
   }
   
   
   /**
    * 判断是否时，今天的时间戳
    * @param timeStamp
    * @return
    */
   public static boolean isTodayTime(long timeStamp)
   {
       try {
           SimpleDateFormat dateF = new SimpleDateFormat("yyyyMMdd");
           Date curDate = new Date();
           String dateStr = dateF.format(curDate);
           String tmpDateStr = dateF.format(new Date(timeStamp));
           return dateStr.equals(tmpDateStr);
       } catch (Exception e) {
           //ignore;
       }
       return false;
   }
   
   /**
    * 判断是否时，今天的时间戳
    * @param timeStamp
    * @return
    */
   public static boolean isYesterdayTime(long timeStamp)
   {
       try {
           SimpleDateFormat dateF = new SimpleDateFormat("yyyyMMdd");
           long oneDayAgo = getOneDayAgo();
           String dateStr = dateF.format(oneDayAgo);
           String tmpDateStr = dateF.format(new Date(timeStamp));
           return dateStr.equals(tmpDateStr);
       } catch (Exception e) {
           //ignore;
       }
       return false;
   }
   
   /**
    * 判断是否是昨天到今天的时间 
	* @param timeStamp
	* @return
	*/
   public static boolean isInYesterdayAndToday(long timeStamp)
   {
	   try {
           SimpleDateFormat dateF = new SimpleDateFormat("yyyyMMdd");
           long oneDayAgo = getOneDayAgo();
           String dateStr = dateF.format(new Date(oneDayAgo));
           Date bDate = dateF.parse(dateStr);
           return timeStamp >= bDate.getTime();
           
       } catch (Exception e) {
           //ignore;
       }
       return false;
   }
   /**
    * 根据制定格式转换指定时间
    * @param time 毫秒
    * @param format
    * @return
    */
   public static String parseDate2Str(long time,String format)
   {
	   return new SimpleDateFormat(format).format(new Date(time));
   }
   
   /**
    * 根据制定格式转换指定时间
    * @param date
    * @param format
    * @return
    */
   public static String parseDate2Str(Date date, String format) {
       if(date == null || StringUtils.isEmpty(format)) {
           return null;
       }
       return new SimpleDateFormat(format).format(date);
   }
   
   public static long getOneDayAgo()
   {
       return System.currentTimeMillis() - ONE_DAY_TS;
   }
   
   public static long getOneHourAgo()
   {
       return System.currentTimeMillis() - ONE_HOUR_TS;
   }
   
   public static long getOneWeekAgo()
   {
       return System.currentTimeMillis() - ONE_WEEK_TS;
   }
   
   public static long getDaysAgo(int days) 
   {
	   return System.currentTimeMillis() - ONE_DAY_TS * days;
   }
   
   /**
    * 是否是5分钟内的
    * @param ts
    * @return
    */
   public static boolean isInFiveMinutes(long ts)
   {
       long ct = Math.abs(System.currentTimeMillis() - ts);
       return ct < FIVE_MINUTES_TS;
   }
   /**
    * 将秒数转成mm:HH:ss的格式
    * @param totalSecond
    * @return
    */
   public static String second2time(Long totalSecond) {
       if(totalSecond == null) {
           return "00:00:00";
       }
       
       String time = "";
       long remainder = totalSecond % 60;
       long minute = (totalSecond / 60) % 60;
       long hour = (totalSecond / 60) / 60;

       if(hour < 10) {
           time += "0";
       }
       time += hour + ":";

       if(minute < 10) {
           time += "0";
       }
       time += minute + ":";

       if(remainder < 10) {
           time += "0";
       }
       time += remainder;
       return time;
   }
   
   /**
    * 获取现在具体月份: yyyyMM
    * @return
    */
   public static Integer getThisMonth() {
	   SimpleDateFormat sdf = new SimpleDateFormat(MONTH_FORMAT);
	   String month = sdf.format(new Date());
	   return Integer.parseInt(month);
   }
   private static final String WORK_START_TIME = "10:00";
   private static final String WORK_END_TIME = "23:00";
   private static final String DEF_WORK_TIME = WORK_START_TIME+"-"+WORK_END_TIME;
   private static Pattern workTimePtn = Pattern.compile("([\\d]{2}:[\\d]{2})\\-([\\d]{2}:[\\d]{2})");
   
   /**
    * 返回对应的小时+分钟时间戳
    * 注意：此时对应的年份是:1970/1/1
    * @param timeStr 时间格式: 12:00
    * @return 时间戳
    */
   public static long parseTime(String timeStr)
   {
       try {
           SimpleDateFormat sdf = new SimpleDateFormat(HOUR_MIN_FORMAT);
           Date d = sdf.parse(timeStr);
           return d.getTime();
       } catch (Exception e) {
           //ignore
       }
       return 0;
   }
   
   /**
    * 解析工作时间字符串
    * @param workTime
    * @return
    */
   public static long[] parseWorkHourM(String workTime)
   {
       //简单判断对应的工作时间是否是合法的
       if(StringUtils.isEmpty(workTime))
       {
           workTime = DEF_WORK_TIME;
       }
       String[] rstTimes = new String[2];
       //查看正则表达
       Matcher matcher = workTimePtn.matcher(workTime);
       if(!matcher.find())
       {
           //直接用默认值
           rstTimes[0] = WORK_START_TIME;
           rstTimes[1] = WORK_END_TIME;
       }else
       {
           rstTimes[0] = matcher.group(1);
           rstTimes[1] = matcher.group(2);
       }
       long time1 = parseTime(rstTimes[0]);
       if(time1 < 1)
       {
           time1 = parseTime(WORK_START_TIME);
       }
       long time2 = parseTime(rstTimes[1]);
       if(time2 < 1)
       {
           time2 = parseTime(WORK_END_TIME);
       }
       //返回对应的起止时间
       return new long[]{time1,time2};
   }
   
   /**
    * 根据工作时间，计算出当前支付、发货时间对应的
    * @param curTime
    * @return
    */
   public static long[] parseWorkTime(long curTime,long[] workTime)
   {
       long[] rstTS = new long[2];
       try {
           //获取当前时间对应的日期
           SimpleDateFormat sdf = new SimpleDateFormat(ONLY_DATE_FORMAT);
           SimpleDateFormat sdf2 = new SimpleDateFormat(NORMAL_DATE_FORMAT2);
           Date hmDate1 = new Date(workTime[0]);
           Date hmDate2 = new Date(workTime[1]);
           String curDay = sdf.format(new Date(curTime));
           Date curDayTS = sdf.parse(curDay);
           curDayTS.setHours(hmDate1.getHours());
           curDayTS.setMinutes(hmDate1.getMinutes());
           rstTS[0] = curDayTS.getTime();
           curDayTS.setHours(hmDate2.getHours());
           curDayTS.setMinutes(hmDate2.getMinutes());
           rstTS[1] = curDayTS.getTime();
        } catch (Exception e) {
            Logger.error(e, "");
        }
        return rstTS;
   }
   
   /**
    * 解析两个时间相差的天数
    * @param time1
    * @param time2
    * @return
    */
   public static int parseDateIntervalDay(long time1,long time2)
   {
       Calendar calendar = Calendar.getInstance(); 
       calendar.setTimeInMillis(time1);
       int day1 = calendar.get(Calendar.DAY_OF_YEAR);
       calendar.setTimeInMillis(time2);
       int day2 = calendar.get(Calendar.DAY_OF_YEAR);
       int interval = day2-day1;
       if(interval < 0)
       {
           return 0;
       }
       return interval;
   }
   
   /**
    * 解析两个时间差的秒数
    * @param time1
    * @param time2
    * @return
    */
   public static int parseDateIntervalSec(long time1,long time2)
   {
       long[] tmpTS = parseWorkTime(System.currentTimeMillis(), new long[]{time1,time2});
       return (int) (tmpTS[1]-tmpTS[0])/1000;
   }
   
   /**
    * 返回指定类型天数
    * @param ts
    * @return
    */
   public static String formatDate(long ts,String format)
   {
       SimpleDateFormat sdf = new SimpleDateFormat(format);
       return sdf.format(new Date(ts));
   }
   
   /**
	 * 获取当前时间
	 * @return
	 */
	public static Date getNow() {
		return new Date(System.currentTimeMillis());
	}
	
	/**
	* 字符串转换成日期
	* @param str
	* @return date
	*/
	public static Date StrToDate(String str) {
	  
	   SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   Date date = null;
	   try {
	    date = format.parse(str);
	   } catch (ParseException e) {
	    e.printStackTrace();
	   }
	   return date;
	}
	/**
	 * Timestamp转换成字符
	 * @param str
	 * @return date
	 */
	public static String timestampToStr(Timestamp time) {
		SimpleDateFormat format = new SimpleDateFormat(NORMAL_DATE_FORMAT);
		String str = "";
		str = format.format(time);
		return str;
	}
   
	public static String paseLongToString(long time) {
		SimpleDateFormat sdf=new SimpleDateFormat(NORMAL_DATE_FORMAT);
		Date date = new Date(time);
		String dateStr = "";
		dateStr = sdf.format(date);
		return dateStr;
	}
	/**
	 * 获取当前天的后一个月的时间，2017-06-13：修改注释为计算指定天数之后的时间，取当天最后一秒的时间，如：XXXX-XX-XX 23:59:59
	 * */
	public static Date getNextMonthTime(Integer day) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, day);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return calendar.getTime();
	}
}
