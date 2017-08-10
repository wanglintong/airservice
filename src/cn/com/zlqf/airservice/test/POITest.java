package cn.com.zlqf.airservice.test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder.In;

import cn.com.zlqf.airservice.utils.BaseUtils;
import cn.com.zlqf.airservice.utils.DateUtils;

public class POITest {
	public static void main(String[] args) {
		Long estimatedArrival = 20170808235500l;
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
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		System.out.println(sdf.format(c.getTime()));
	}
}
