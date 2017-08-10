package cn.com.zlqf.airservice.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class LogUtil {
	public static void log(String message,String logFileName) {
		try {
			String stringToday = DateUtils.getStringToday("yyyyMMdd");
			File file = new File("/home/message/"+stringToday + "-" + logFileName + ".txt");
			if(!file.exists()) {
				file.createNewFile();
			}
			PrintWriter pw = new PrintWriter(new FileOutputStream(file,true));
			pw.println(message);
			pw.close();
		} catch (Exception e) {
		}
	}
}
