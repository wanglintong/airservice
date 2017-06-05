package cn.com.zlqf.airservice.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class BaseUtils {
	
	public static Map<String,String> getFlyNoMap() {
		Map<String,String> map = new HashMap<String,String>();
		try {
			// File excelFile = new File("src/hbh.xlsx"); //创建文件对象  
		     //FileInputStream is = new FileInputStream(excelFile); //文件流  
		     InputStream is = BaseUtils.class.getClassLoader().getResourceAsStream("hbh.xlsx");
		     Workbook workbook = WorkbookFactory.create(is); //这种方式 Excel 2003/2007/2010 都是可以处理的  
		     Sheet sheet = workbook.getSheetAt(0);
		     //int rowCount = sheet.getPhysicalNumberOfRows(); //获取总行数  
		     for(int i=1 ; i<78 ; ++i) {
		    	 Row row = sheet.getRow(i);
		    	 String c1 = row.getCell(1).getStringCellValue();
		    	 String c2 = row.getCell(2).getStringCellValue();;
		    	 map.put(c2, c1);
		     }
		     return map;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Map<String,String> getAddressMap(){
		try {
			InputStream inputStream = BaseUtils.class.getClassLoader().getResourceAsStream("fly.xml");
			SAXReader sax = new SAXReader();
			Document document = sax.read(inputStream);
			
			List<Element> elements = document.getRootElement().elements();
			
			HashMap<String,String> map = new HashMap<String,String>();
			for(Element e : elements) {
				map.put(e.element("FRCD").getText(), e.element("CNNM").getText());
			}
			return map;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Map<String,String> getTaskMap() {
		try {
			Map<String,String> map = new HashMap<>();
			map.put("B", "专机");
			map.put("E", "急救");
			map.put("G", "通用");
			map.put("J", "加班飞行");
			map.put("M", "军用");
			map.put("N", "非定期运输");
			map.put("Q", "补班");
			map.put("S", "定期");
			map.put("X", "其它");
			return map;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String getRandomId() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
}
