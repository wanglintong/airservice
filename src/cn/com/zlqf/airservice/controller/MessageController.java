package cn.com.zlqf.airservice.controller;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;

import cn.com.zlqf.airservice.entity.FlyInfo;
import cn.com.zlqf.airservice.entity.Message;
import cn.com.zlqf.airservice.entity.User;
import cn.com.zlqf.airservice.service.MessageService;
import cn.com.zlqf.airservice.service.UserService;
import cn.com.zlqf.airservice.utils.DateUtils;
import cn.com.zlqf.airservice.utils.MessageUtils;

@Controller
public class MessageController {
	
	@Autowired
	private MessageService messageService;
	
	@RequestMapping("/message/getMessageList")
	public @ResponseBody List<Message> getMessageList(String time) {
		if(time==null) {
			time = DateUtils.getStringToday("yyyy-MM-dd");
		}
		return messageService.getMessageList(time);
	}
	
	@RequestMapping("/message/checkFlyNo")
	public @ResponseBody Map<String,List<FlyInfo>> checkFlyNo(String ids) {
		Map<String, List<FlyInfo>> map = null;
		try {
			map = messageService.checkFlyNo(ids);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	@RequestMapping("/message/check")
	public @ResponseBody Map<String,String> check(String ids) {
		Map<String,String> map = new HashMap<>();
		try {
			Long begin = System.currentTimeMillis();
			Map<String, Long> timeMap = messageService.check(ids);
			map.put("state", "ok");
			map.put("checktime", (System.currentTimeMillis()-begin)+"");
			map.put("time1", timeMap.get("time1")+"");
			map.put("time2", timeMap.get("time2")+"");
			map.put("time3", timeMap.get("time3")+"");
			map.put("time4", timeMap.get("time4")+"");
			map.put("time5", timeMap.get("time5")+"");
		} catch (Exception e) {
			e.printStackTrace();
			map.put("state", "error");
		}
		return map;
	}
	@RequestMapping("/message/checkRepeat")
	public @ResponseBody Map<String,String> checkRepeat(String msg) {
		Map<String,String> map = new HashMap<>();
		try {
			Long begin = System.currentTimeMillis();
			List<Map<String,String>> list  = JSON.parseObject(msg,List.class);
			messageService.checkRepeat(list);
			map.put("state", "ok");
			map.put("checkRepeatTime", (System.currentTimeMillis()-begin)+"");
		} catch (Exception e) {
			e.printStackTrace();
			map.put("state", "error");
		}
		return map;
	}
	
	
	@RequestMapping("/message/ignore")
	public @ResponseBody Map<String,String> ignore(String ids) {
		Map<String,String> map = new HashMap<>();
		try {
			messageService.ignore(ids);
			map.put("state", "ok");
		} catch (Exception e) {
			e.printStackTrace();
			map.put("state", "error");
		}
		return map;
	}
	@RequestMapping("/message/parseMessage")
	public @ResponseBody Map<String,String> parseMessage(String message,String messageId) {
		Map<String,String> map = new HashMap<>();
		try {
			String jsonMessage = MessageUtils.parse(message);
			map.put("state", "ok");
			map.put("msg", jsonMessage);
			messageService.updateMessageById(message,jsonMessage,messageId);
		} catch (Exception e) {
			e.printStackTrace();
			map.put("state", "error");
		}
		return map;
	}
}
