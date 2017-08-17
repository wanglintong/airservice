package cn.com.zlqf.airservice.dao;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

import cn.com.zlqf.airservice.entity.Message;

public interface MessageDataDao {
	void check(List<String> ids);
	List<Message> findMessagesByIds(List<String> ids);
	List<Message> getMessageList(String time);
}
