package cn.com.zlqf.airservice.rxtx;

import java.awt.SystemColor;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.com.zlqf.airservice.entity.Const;
import cn.com.zlqf.airservice.entity.Message;
import cn.com.zlqf.airservice.service.MessageService;
import cn.com.zlqf.airservice.utils.DateUtils;
import cn.com.zlqf.airservice.utils.MessageUtils;
import cn.com.zlqf.airservice.utils.StringUtils;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

@Component 
public class MessageReceiver implements SerialPortEventListener {
	
	@Autowired
	private MessageService messageService;
	
	private InputStream inputStream;
	private OutputStream outputStream;
	private StringBuilder sb = new StringBuilder();
	private PrintWriter pw = null;
	/*
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
	*/
	
	@PostConstruct
	public void start() {
		//String portName = "/dev/ttyUSB0";
		String portName = "COM2";
		try {
			CommPortIdentifier commPortIdentifier = CommPortIdentifier.getPortIdentifier(portName);
			SerialPort port = (SerialPort) commPortIdentifier.open("airservice", 1000);
			// 当端口有数据时通知
			port.notifyOnDataAvailable(true);
			port.addEventListener(this);
			port.setSerialPortParams(2400, SerialPort.DATABITS_8, // 设置串口读写参数
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			inputStream = port.getInputStream();
			outputStream = port.getOutputStream();
		} catch (NoSuchPortException e) {
			System.out.println("选择的端口不存在");
			return;
		} catch (PortInUseException e) {
			System.out.println("选择的端口已被使用");
			return;
		} catch (TooManyListenersException e) {
			System.out.println("端口监听过多");
			return;
		} catch (IOException e) {
			System.out.println("获得输入/输出流失败");
			return;
		} catch(UnsupportedCommOperationException e) {
			System.out.println("非法操作");
			return;
		}
		System.out.println(portName + "开启监听");
	}
	
	public void testSend(String message) {
		try {
			outputStream.write(message.getBytes());
			outputStream.flush();
		} catch (IOException e) {
			System.out.println("发送消息失败");
		}
	}
	
	@Override
	public void serialEvent(SerialPortEvent event) {
		switch (event.getEventType()) {
		case SerialPortEvent.BI:/* Break interrupt,通讯中断 */
		case SerialPortEvent.OE:/* Overrun error，溢位错误 */
		case SerialPortEvent.FE:/* Framing error，传帧错误 */
		case SerialPortEvent.PE:/* Parity error，校验错误 */
		case SerialPortEvent.CD:/* Carrier detect，载波检测 */
		case SerialPortEvent.CTS:/* Clear to send，清除发送 */
		case SerialPortEvent.DSR:/* Data set ready，数据设备就绪 */
		case SerialPortEvent.RI:/* Ring indicator，响铃指示 */
		/*
		 * Output buffer is empty，输出缓冲区清空
		 */
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			break;
		/*
		 * Data available at the serial port，端口有可用数据。读到缓冲数组，输出到终端
		 */
		case SerialPortEvent.DATA_AVAILABLE:
			byte[] readBuffer = new byte[1024];
			String message = "";
			int len = 0;
			try {
				while (inputStream.available() > 0) {
					len = inputStream.read(readBuffer);
				}
				message = new String(readBuffer, 0, len);
				message = message.replace("\r\n", "").replace("\n", "").replace("\r", "").replace("\n\r", "");
				//String flag = bytesToHexString(Arrays.copyOfRange(readBuffer, 0, len));
				if(message.contains("")) {//收到开头标志
					System.out.println("收到报文开始标志");
					sb.delete(0, sb.length());
				}else if(message.contains("")) {//收到结束标志
					System.out.println("收到报文结束标志，开始解析报文");
					String stringToday = DateUtils.getStringToday("yyyyMMdd");
					//File file = new File("/home/message/"+stringToday+".txt");
					File file = new File("d:/log_601.txt");
					if(!file.exists()) {
						file.createNewFile();
					}
					pw = new PrintWriter(new FileOutputStream(file,true));
					pw.println(sb.toString());
					pw.println("---------------------------------------------");
					pw.flush();
					pw.close();
					//处理报文
					String json = null;
					try {
						json = MessageUtils.parse(sb.toString());
						if(json==null) {//json为null 说明该条报文为无效报文
							System.out.println("无效报文，丢弃");
							sb.delete(0, sb.length());
							return;
						}
					} catch (Exception e) {
						System.out.println("报文解析失败");
						sb.delete(0, sb.length());
						return;
					}
					System.out.println("解析报文成功，存入数据库");
					//System.out.println(json);
					Message m = new Message();
					m.setId(StringUtils.uuid());
					m.setMessage(sb.toString());//设置原始报文
					m.setStatus(0);
					m.setTime(DateUtils.getStringToday("yyyy-MM-dd"));
					m.setAddTime(new Date());
					m.setJsonMessage(json);
					
					messageService.addMessage(m);
					Const.session.getBasicRemote().sendText("has new message");
					sb.delete(0, sb.length());
				}else {
					//System.out.println("收到报文内容:" + message);
					sb.append(message);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
