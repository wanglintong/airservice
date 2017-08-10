package cn.com.zlqf.airservice.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;

public class DateUtils {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	/*
	 * 支持格式为 yyyy.MM.dd G 'at' hh:mm:ss z 如 '2002-1-1 AD at 22:10:59 PSD'<br>
	 * yy/MM/dd HH:mm:ss 如 '2002/1/1 17:55:00'<br> yy/MM/dd HH:mm:ss pm 如
	 * '2002/1/1 17:55:00 pm'<br> yy-MM-dd HH:mm:ss 如 '2002-1-1 17:55:00' <br>
	 * yy-MM-dd HH:mm:ss am 如 '2002-1-1 17:55:00 am' <br>
	 * 
	 */

	public static final String FORMAT1 = "yyyy-MM-dd HH:mm:ss";
	public static final String FORMAT2 = "yyyy.MM.dd G 'at' hh:mm:ss z";
	public static final String FORMAT3 = "yy/MM/dd HH:mm:ss";
	public static final String FORMAT4 = "yy-MM-dd HH:mm:ss am";
	public static final String FORMAT5 = "yyyy年MM月dd日 HH时mm分ss秒";
	public static final String FORMAT6 = "yyyy-MM-dd";
	public static final String FORMAT7 = "HH:mm:ss";
	public static final String FORMAT8 = "HH:mm";
	
	public static Long estimatedArrivalFormat(Long estimatedArrival) {
		String str = estimatedArrival+"";
		int year = Integer.parseInt(str.substring(0,4));
		int month = Integer.parseInt(str.substring(4,6));
		int day = Integer.parseInt(str.substring(6,8));
		int hour = Integer.parseInt(str.substring(8,10));
		int minute = Integer.parseInt(str.substring(10,12));
		int second = Integer.parseInt(str.substring(12,14));
		
		String sm = minute+"";
		if(sm.length()==1) {
			//说明是分钟数在10分之前
			if(minute>=0 && minute<=2) {
				minute = 0;
			}else if(minute>=3 && minute<=7) {
				minute = 5;
			}else {
				minute = 10;
			}
		}else {
			//说明分钟数在10分-59分
			String s1 = sm.substring(0, 1);
			String s2 = sm.substring(1,2);
			int s2i = Integer.parseInt(s2);
			int s1i=  Integer.parseInt(s1);
			if(s2i>=0 && s2i<=2) {
				s2i = 0;
			}else if(s2i>=3 && s2i<=7) {
				s2i = 5;
			}else if(s2i>=8) {
				s2i = 0;
				s1i += 1;
			}
			String ss = s1i + "" + s2i;
			minute = Integer.parseInt(ss);
		}
		
		Calendar c = Calendar.getInstance();
		c.set(year, month-1, day, hour, minute, second);
		
		return Long.parseLong(sdf.format(c.getTime()));
	}
	
	public static Long longTolong(Long planedArrival) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date d = new Date(planedArrival);
		String format = sdf.format(d);
		return Long.parseLong(format);
	}

	public static Long string2LongByDOF(String dof, String departureAirportTime) {
		int year = Integer.parseInt(dof.substring(0, 2)) + 2000;
		int month = Integer.parseInt(dof.substring(2, 4)) - 1;
		int day = Integer.parseInt(dof.substring(4, 6));
		int hh = Integer.parseInt(departureAirportTime.substring(0, 2));
		int mm = Integer.parseInt(departureAirportTime.substring(2, 4));
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);
		c.set(Calendar.HOUR_OF_DAY, hh + 8);
		c.set(Calendar.MINUTE, mm);
		c.set(Calendar.SECOND, 0);
		return c.getTimeInMillis();
	}

	public static Long string2FormattingLongByDOF(String dof, String departureAirportTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		int year = Integer.parseInt(dof.substring(0, 2)) + 2000;
		int month = Integer.parseInt(dof.substring(2, 4)) - 1;
		int day = Integer.parseInt(dof.substring(4, 6));
		int hh = Integer.parseInt(departureAirportTime.substring(0, 2));
		int mm = Integer.parseInt(departureAirportTime.substring(2, 4));
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);
		c.set(Calendar.HOUR_OF_DAY, hh + 8);
		c.set(Calendar.MINUTE, mm);
		c.set(Calendar.SECOND, 0);
		Date d = c.getTime();
		String format = sdf.format(d);
		return Long.parseLong(format);
	}

	public static Long getFlyTime(String flyTime) {
		// flyTime 是0202表示2小时2分钟 需要转成毫秒的形式
		int hh = Integer.parseInt(flyTime.substring(0, 2));
		int mm = Integer.parseInt(flyTime.substring(2, 4));
		Long retVal = (hh * 3600 + mm * 60) * 1000l;
		return retVal;
	}
	
	public static Long string2FormattingLong(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		int hh = Integer.parseInt(time.substring(0, 2));
		int mm = Integer.parseInt(time.substring(2, 4));
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, hh + 8);
		c.set(Calendar.MINUTE, mm);
		c.set(Calendar.SECOND, 0);
		Date d = c.getTime();
		String format = sdf.format(d);
		return Long.parseLong(format);
	}

	public static Long string2Long(String time) {
		int hh = Integer.parseInt(time.substring(0, 2));
		int mm = Integer.parseInt(time.substring(2, 4));
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, hh + 8);
		c.set(Calendar.MINUTE, mm);
		c.set(Calendar.SECOND, 0);
		return c.getTimeInMillis();
	}

	public static String getStringToday(String fmt) {
		SimpleDateFormat sdf = new SimpleDateFormat(fmt);
		return sdf.format(new Date());
	}

	// date类型转换为String类型
	// formatType格式为yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
	// data Date类型的时间
	public static String dateToString(Date data, String formatType) {
		return new SimpleDateFormat(formatType).format(data);
	}

	// long类型转换为String类型
	// currentTime要转换的long类型的时间
	// formatType要转换的string类型的时间格式
	public static String longToString(long currentTime, String formatType) throws ParseException {
		Date date = longToDate(currentTime, formatType); // long类型转成Date类型
		String strTime = dateToString(date, formatType); // date类型转成String
		return strTime;
	}

	// string类型转换为date类型
	// strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
	// HH时mm分ss秒，
	// strTime的时间格式必须要与formatType的时间格式相同
	public static Date stringToDate(String strTime, String formatType) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(formatType);
		Date date = null;
		date = formatter.parse(strTime);
		return date;
	}

	// long转换为Date类型
	// currentTime要转换的long类型的时间
	// formatType要转换的时间格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
	public static Date longToDate(long currentTime, String formatType) throws ParseException {
		Date dateOld = new Date(currentTime); // 根据long类型的毫秒数生命一个date类型的时间
		String sDateTime = dateToString(dateOld, formatType); // 把date类型的时间转换为string
		Date date = stringToDate(sDateTime, formatType); // 把String类型转换为Date类型
		return date;
	}

	// string类型转换为long类型
	// strTime要转换的String类型的时间
	// formatType时间格式
	// strTime的时间格式和formatType的时间格式必须相同
	public static long stringToLong(String strTime, String formatType) throws ParseException {
		Date date = stringToDate(strTime, formatType); // String类型转成date类型
		if (date == null) {
			return 0;
		} else {
			long currentTime = dateToLong(date); // date类型转成long类型
			return currentTime;
		}
	}

	// date类型转换为long类型
	// date要转换的date类型的时间
	public static long dateToLong(Date date) {
		return date.getTime();
	}

	/**
	 * 
	 * @param dataString
	 *            日期格式的字符串 格式如 2015-01-14 12:00:00
	 * @return
	 */
	public static String getNumString(String dataString) {
		if (!StringUtils.isBlank(dataString)) {
			StringBuilder sb = new StringBuilder();
			String[] total = dataString.split(" ");
			String[] date = total[0].split("-");
			String[] time = total[1].split(":");
			sb.append(date[0]).append(date[1]).append(date[2]).append(time[0]).append(time[1]).append(time[2]);
			return sb.toString();
		}
		return null;
	}

	/*
	 * public static void main(String[] args) throws ParseException {
	 * System.out.println(stringToLong("09:00", FORMAT8));
	 * System.out.println(longToString(3600000, FORMAT8)); }
	 */

	/**
	 * 获取当天 的 时间值
	 * 
	 * @return
	 * @throws ParseException
	 */
	public static Long getTodayTime() throws ParseException {
		Date data = longToDate(System.currentTimeMillis(), "yyyy-MM-dd");
		return data.getTime();
	}

	/**
	 * 获取明天 的 时间值
	 * 
	 * @return
	 * @throws ParseException
	 */
	public static Long getTomorrowTime() throws ParseException {
		Date data = longToDate(System.currentTimeMillis(), "yyyy-MM-dd");
		data.setDate(data.getDate() + 1);
		return data.getTime();
	}

	/**
	 * 
	 * @param numLong
	 *            20160810
	 * @return string 2016-08-10
	 * @throws ParseException
	 */
	public static String numberDateToStrDate(Long numLong) throws ParseException {

		if (numLong != null && (numLong + "").length() == 8) {
			StringBuilder sb = new StringBuilder();
			// 能查到当前执行日期
			sb.append((numLong + "").substring(0, 4)).append("-").append((numLong + "").substring(4, 6)).append("-")
					.append((numLong + "").substring(6, 8));
			return sb.toString();
		} else {
			// 查不到时,默认加载本天的
			return longToString(System.currentTimeMillis(), "yyyy-MM-dd");
		}
	}

	/*
	 * 将Long类型的时间转为HH:mm格式的时间
	 */
	public static String formatTime(Long time) {
		try {
			if (time != null) {
				String str = time.toString();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				Date date = sdf.parse(str);
				SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
				String format = sdf2.format(date);
				return format;
			}
			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// 判断两个日期相差的天数
	public static int differentDaysByMillisecond(Date date1, Date date2) {
		int days = (int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24));
		return days;
	}

}
