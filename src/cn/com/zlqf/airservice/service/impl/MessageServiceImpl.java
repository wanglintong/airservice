package cn.com.zlqf.airservice.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.com.zlqf.airservice.dao.FlyInfoDao;
import cn.com.zlqf.airservice.dao.MessageDao;
import cn.com.zlqf.airservice.dao.UserDao;
import cn.com.zlqf.airservice.entity.FlyInfo;
import cn.com.zlqf.airservice.entity.Message;
import cn.com.zlqf.airservice.entity.User;
import cn.com.zlqf.airservice.service.FlyInfoService;
import cn.com.zlqf.airservice.service.MessageService;
import cn.com.zlqf.airservice.service.UserService;
import cn.com.zlqf.airservice.utils.DateUtils;
import cn.com.zlqf.airservice.utils.SQLUtils;


@Service
@Transactional(readOnly=false)
public class MessageServiceImpl implements MessageService{
	@Autowired
	private MessageDao messageDao;
	@Autowired
	private FlyInfoDao flyInfoDao;
	@Autowired
	private RedisTemplate<String,List<FlyInfo>> redisTemplate;
	@Autowired
	private FlyInfoService flyInfoService;
	
	@Override
	public void addMessage(Message message) {
		messageDao.save(message);
	}
	
	@Transactional(readOnly=true)
	@Override
	public List<Message> getMessageList(String time) {
		return messageDao.getMessageList(time);
	}
	
	
	@Override
	public Map<String,Long> check(String ids) {
		Map<String,Long> map = new HashMap<String,Long>();
		if(ids!=null) {
			List<String> idList = Arrays.asList(ids.split(","));
			
			Long beginTime = System.currentTimeMillis();
			//将message状态设置为审核
			messageDao.check(idList);
			map.put("time1", System.currentTimeMillis()-beginTime);
			
			//得到审核的message
			beginTime = System.currentTimeMillis();
			List<Message> messageList = messageDao.findMessagesByIds(idList);
			map.put("time2", System.currentTimeMillis()-beginTime);
			
			beginTime = System.currentTimeMillis();
			StringBuilder sb = new StringBuilder();
			for(Message message : messageList) {
				JSONObject jsonObject = JSON.parseObject(message.getJsonMessage());
				String sql = SQLUtils.createSQL(jsonObject);
				if(sql!=null) {
					sb.append(sql + ";");
				}
			}
			map.put("time3", System.currentTimeMillis()-beginTime);
			
			if(!sb.toString().equals("")) {
				beginTime = System.currentTimeMillis();
				//更新flyInfo
				flyInfoDao.updateMessageFieldBatch(sb.toString());
				map.put("time4", System.currentTimeMillis()-beginTime);
				
				beginTime = System.currentTimeMillis();
				//更新redis
				List<FlyInfo> latestFlyInfoList = flyInfoDao.findAll();
				redisTemplate.opsForValue().set("flyInfoList", latestFlyInfoList);
				map.put("time5", System.currentTimeMillis()-beginTime);
			}
		}
		return map;
	}
	
	@Override
	public Map<String,List<FlyInfo>> checkFlyNo(String ids) {
		Map<String,List<FlyInfo>> map = new HashMap<>();
		//long begin = System.currentTimeMillis();
		if(ids!=null) {
			List<Message> messageList = messageDao.findMessagesByIds(Arrays.asList(ids.split(",")));
			List<FlyInfo> list = flyInfoService.getFlyInfoListFromRedis();//得到最新的航班信息
			for(Message message : messageList) {
				ArrayList<FlyInfo> saveList = new ArrayList<>();
				JSONObject parseObject = JSON.parseObject(message.getJsonMessage());
				String incomingFlyNo = parseObject.getString("incomingFlyNo");
				String departureFlyNo = parseObject.getString("departureFlyNo");
				if(incomingFlyNo!=null) {
					for(FlyInfo f:list) {
						if(f.getIncomingFlyNo().equals(incomingFlyNo)) {
							saveList.add(f);
						}
					}
				}
				if(departureFlyNo!=null) {
					for(FlyInfo f:list) {
						if(f.getDepartureFlyNo().equals(departureFlyNo)) {
							saveList.add(f);
						}
					}
				}
				map.put(message.getId(), saveList);
			}
		}
		//System.out.println("checkFlyNo time:" + (System.currentTimeMillis()-begin));
		return map;
	}

	@Override
	public void ignore(String ids) {
		if(ids!=null) {
			messageDao.ignore(Arrays.asList(ids.split(",")));
		}
	}
	@Transactional(readOnly=true)
	@Override
	public List<Message> getCheckedMessageList() {
		return messageDao.getCheckedMessageList(DateUtils.getStringToday("yyyy-MM-dd"));
	}

	@Override
	public void updateMessageById(String message,String jsonMessage, String messageId) {
		messageDao.updateMessageById(message,jsonMessage,messageId);
	}
	
	@Override
	public void checkRepeat(List<Map<String, String>> list) {
		List<String> messageIds = new ArrayList<>();
		List<String> flyInfoIds = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		for(Map<String,String> m : list) {
			for(String key : m.keySet()) {
				messageIds.add(key);
				flyInfoIds.add(m.get(key));
			}
		}
		List<String> jsonMessageList = messageDao.findJsonMessageListByIds(messageIds);
		for(int i=0 ; jsonMessageList!=null && i<jsonMessageList.size() ; ++i) {
			JSONObject parseObject = JSON.parseObject(jsonMessageList.get(i));
			String sql = SQLUtils.createSQLByFlyInfoId(parseObject, flyInfoIds.get(i));
			if(sql!=null) {
				sb.append(sql + ";");
			}
		}
		//修改报文状态
		messageDao.check(messageIds);
		
		if(!sb.toString().equals("")) {
			flyInfoDao.updateMessageFieldByFlyInfoIdBatch(sb.toString());
			
			//更新redis
			List<FlyInfo> latestFlyInfoList = flyInfoDao.findAll();
			redisTemplate.opsForValue().set("flyInfoList", latestFlyInfoList);
		}
	}
	
}
