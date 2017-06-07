package cn.com.zlqf.airservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.com.zlqf.airservice.socket.MessageReminderHandler;

@Controller
@RequestMapping("/test")
public class TestController {
	@Autowired
	private MessageReminderHandler messageReminder;
	
	@RequestMapping("/ws")
	public void test() {
		try {
			messageReminder.sendMessage("hello!!!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
