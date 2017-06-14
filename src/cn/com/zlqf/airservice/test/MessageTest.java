package cn.com.zlqf.airservice.test;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import cn.com.zlqf.airservice.utils.BaseUtils;

public class MessageTest {
	private String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}
	@Test
	public void test() {
		StringBuilder sb = new StringBuilder();
		String string = sb.toString();
		System.out.println("".equals(string));
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testPOI() {
		Map<String,String> map = new HashMap<String,String>();
		try {
			 File excelFile = new File("src/hbh.xlsx"); //创建文件对象  
		     FileInputStream is = new FileInputStream(excelFile); //文件流  
		     Workbook workbook = WorkbookFactory.create(is); //这种方式 Excel 2003/2007/2010 都是可以处理的  
		     Sheet sheet = workbook.getSheetAt(0);
		     //int rowCount = sheet.getPhysicalNumberOfRows(); //获取总行数  
		     for(int i=1 ; i<78 ; ++i) {
		    	 Row row = sheet.getRow(i);
		    	 String c1 = row.getCell(1).getStringCellValue();
		    	 String c2 = row.getCell(2).getStringCellValue();;
		    	 map.put(c2, c1);
		     }
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Test
	public void test1() {
		String oldDepartureFlyNo = "CHH7355";
		Map<String,String> flyNoMap = BaseUtils.getFlyNoMap();
		//String oldIncomingFlyNo = flyDynamic.getIncomingFlyNo();
		if(oldDepartureFlyNo!=null) {
			String szm = oldDepartureFlyNo.substring(0, 3);
			String ezm = flyNoMap.get(szm);
			if(ezm!=null) {
				oldDepartureFlyNo = oldDepartureFlyNo.replace(szm, ezm);
			}
		}
		System.out.println(oldDepartureFlyNo);
	}
}
