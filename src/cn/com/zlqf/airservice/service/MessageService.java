package cn.com.zlqf.airservice.service;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import cn.com.zlqf.airservice.entity.FlyInfo;
import cn.com.zlqf.airservice.entity.Message;

public interface MessageService {
	void addMessage(Message message);
	List<Message> getMessageList(String time);
	void check(String ids);
	void ignore(String ids);
	List<Message> getCheckedMessageList();
	void updateMessageById(String message,String jsonMessage, String messageId);
	Map<String,List<FlyInfo>> checkFlyNo(String ids);
	void checkRepeat(List<Map<String, String>> list);
}
