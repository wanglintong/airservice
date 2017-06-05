package cn.com.zlqf.airservice.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.UUID;

public class StringUtils {
	public static String stream2String(InputStream inStream) {
		ByteArrayOutputStream bos = null;
		try {
			int len = 0;
			byte[] buffer = new byte[1024];
			bos = new ByteArrayOutputStream();
			len=inStream.read(buffer);
			bos.write(buffer,0,len);
			bos.flush();
			return new String(bos.toByteArray(),"UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				bos.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		
	}
	public static String uuid() {
		return UUID.randomUUID().toString();
	}
	
}
