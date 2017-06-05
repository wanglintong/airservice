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
	public void check(String ids) {
		long begin = System.currentTimeMillis();
		long latestPublishTime = flyInfoDao.getLatestPublishTime();
		if(ids!=null) {
			List<String> idList = Arrays.asList(ids.split(","));
			messageDao.check(idList);
			List<Message> messageList = messageDao.findMessagesByIds(idList);
			for(Message message : messageList) {
				JSONObject parseObject = JSON.parseObject(message.getJsonMessage());
				flyInfoDao.updateMessageField(parseObject,latestPublishTime);
			}
			//更新redis
			List<FlyInfo> latestFlyInfoList = flyInfoDao.getFlyInfoListByPublishTime(latestPublishTime);
			redisTemplate.opsForValue().set("flyInfoList", latestFlyInfoList);
		}
		System.out.println("check time:" + (System.currentTimeMillis()-begin));
	}
	
	@Override
	public Map<String,List<FlyInfo>> checkFlyNo(String ids) {
		Map<String,List<FlyInfo>> map = new HashMap<>();
		long begin = System.currentTimeMillis();
		if(ids!=null) {
			List<Message> messageList = messageDao.findMessagesByIds(Arrays.asList(ids.split(",")));
			List<FlyInfo> list = flyInfoService.getFlyInfoListFromRedis();
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
		System.out.println("checkFlyNo time:" + (System.currentTimeMillis()-begin));
		return map;
	}

	@Override
	public void ignore(String ids) {
		if(ids!=null) {
			String[] split = ids.split(",");
			for(int i=0 ; split!=null && i<split.length ; ++i) {
				messageDao.ignore(split[i]);
			}
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
	@Deprecated
	public void testBatch(EntityManager entityManager) {
		String ids = "dedeeb48-0d47-41e8-bee0-2877bf34493d,a5b2730a-f47e-4e7a-8e4d-ba83bfd5b756,cfc64760-1f66-4271-a7e0-959ed1c56655,d8324a3f-1a45-4972-ad9a-c1eed395acec,0901b4ba-6833-41aa-96c9-6caf2e74e2d3,fc27e667-be8c-4a1d-a122-09fbd4ae2548,6996f0df-e646-4748-ab5a-9153d0bff6f8,64973be6-82f6-42ef-bf31-a7a421555ee7,c580c549-3a46-4bb4-b487-205fd4e43c05,ab194cdb-5410-4b65-ad97-c0bd85f28b33,1fe8bd4f-4a6b-4e97-898f-155ec570c8e8,3c4f825f-92e6-4911-9184-2f9369a9110e,121b9dc8-e689-4fb8-9589-1adc801deb14,680b4702-77c9-4854-8e40-6ccaba17c436,6b05b104-e94f-4e7f-bf60-8554356ca568,05081267-36ab-4308-8ed8-9b8819a33527,9fcbc652-eafd-4081-b74f-d7d2e1586c2b,9cafa706-83d3-4025-b3a4-27342070b6a4,139dfa87-3b43-4f5c-b59a-35472a92641e,038359b7-ce1e-4869-ab53-dc2ef8328303,b91bb1f0-69c2-4678-a326-0a6b506812ed,8c5972de-3cab-49f9-9109-c6d5a4b01528,12cb4530-6084-4932-8b05-21dc7aa80430,4e219cc3-770f-4063-b3c5-9c80b7179849,595bef9d-d539-4305-98d8-13487b533fda,a359c377-f509-46f5-9a7d-c1e265d1b4e5,8240299d-9b2c-4bf7-908a-95bb31d1896f,798725ee-6c46-426b-b785-de5ed516d5ad,5636671e-75cc-4317-bcd8-5f1c346dbbe1,2d1e376e-c4d6-4aa4-af87-d2f4f8857311,2c7c5975-8672-4001-b52d-3200f2b78e24,f307edb0-682a-4a67-b9fe-10606c2d441e,08655973-d784-420f-bac4-03463076d439,ac84b523-4e8e-4640-96c2-c0d19f749357,d72c991e-49e3-4630-b776-b887e95be617,c572a3b8-2ff5-46f9-999f-cbcf36d5dd9b,562f0375-9580-4340-bde8-1391c7ed95e2,927c270b-257b-4112-9691-65e374044a13,00ebb127-c892-49ee-aab4-97b58f8319ed,6ff36108-197e-43d0-9aa5-eaab52ce7f33,9b105dda-dfee-432e-861b-9e8635d69620,38b0386f-c3fb-42ef-b3cf-fa1b9dfddbaf,ae248612-ad14-4f62-a757-c61b42274449,a752c5f8-916d-484e-9c3a-d41fa9eb5a33,bb11f704-87de-41ae-9bc2-ec370246e2f3,30364eb0-6e1d-4591-a7d2-a64423ea46a4,9d2b8881-c03b-4105-b1c9-aebf654016fc,2a4675fa-04a2-42a7-9014-a240435e9bc7,88f5009d-dae7-4477-9740-86b57da223fb,86d67157-7193-4f4a-a6f9-924eb6fd4472,c805650d-4e4f-4f9f-bb40-04c8d7146e4a,ac163d2e-c87a-4203-aa0c-d548c4cafaaa,5b32eb9b-ceea-4c39-8bb6-76c1af61bb8b,a3ac7791-6a93-4427-87fc-cebf5fd4ad7b,4da9336d-7e94-4670-ba91-5a16b2fdd80b,0581953c-08d3-4826-a805-d84679f1ee83,1a10d20f-dd82-4312-817e-f46434975969,d9078b06-273d-4ee9-882a-2632bb4201f5,2f867cd8-f634-4bb4-a2db-25bf736e50bf,e1cdf552-f8da-4ab7-865a-e91dc9ddbf1f,ace7fb4d-2174-4143-9058-a9839a1952c9,650eb07d-7e61-49e9-a23d-bcefbd55a3d7,c4bd63a3-a86f-40ea-8041-180c4ed6a51a,0efa18f1-628a-47bd-9677-28106c75fd8b,fbde4b4a-ccea-48b5-b59b-b38dbcdd0422,bb7e3892-94db-4542-8073-1253fc1e4a46,b90a2c73-a7e9-4fa2-9d2d-50d92ca09109,c678a957-58af-4258-94c5-e8de4e49ec68,94289045-8275-428e-bd2b-56e0d225a576,bde31762-ec7f-4733-82f4-86797ae759d3,91b6058c-42d7-42f7-9c6d-43e98f47ec46,b4ae473d-4b02-425a-a217-5bdc046ab514,9b138d21-d007-4c6c-b001-f70dd82626ef,01e6e9e1-4d11-4a2b-8f2d-7440b9bde613,9c9f8cd7-6b01-4ce8-9142-d41789484d27,a0a018b7-c88c-4f86-b539-63a99ef269ad,795ce09a-a5ba-41af-b08d-5fa27435731f,a8e0b986-e492-43ec-ab04-2c9d452dd04f,91930d3f-5269-4651-9381-6c11f00d697d,ea238db5-d923-43d6-8d38-497339028099,ebebb632-f52e-43f5-9047-b0d866dce71b,eb5d8fd5-8f4f-4a79-8210-89f9b6afd94c,49e4e120-0119-475f-a723-dd8d46584b64,44236bbc-2b85-4dca-a64b-6ae28c92499c,4299b493-e03d-4994-90a8-9f68db9b52d5,a7853422-2772-4fbc-ba5a-4c95307b04bf,bc2e85fd-1547-4250-9e3f-dc62b4d1d868,7af8514c-a65a-478f-b0a9-016c5e07a2e9,b0f77fea-2799-46b8-9c71-b7084965d8a4,b122ead6-5259-4e31-ad51-33755fb8dc49,a129236d-b162-4424-8bbe-8fe5f8d8714c,97fc2952-57b6-4cb6-9b17-5d12b4116ecb,a97d0809-334a-40d8-a377-68567f072146,d1827c66-255c-4ac8-8e80-2e62c1e22242,ee11bed3-fbda-43d6-ac70-ebd72635093c,6ff50515-44b3-44b7-ab0b-b5f2ed4fb7d9,33bbad37-8b2e-4258-8397-794809b9b341,fca5c0a7-7dd3-4804-98f3-515c5e261349,bb762cc8-78fb-4bd9-ad9a-b875098f16a1,55cdb4f8-3f74-4c49-a4b5-5a2471c5150b,";
		String[] idArray = ids.split(",");
		long beginTime = System.currentTimeMillis();
		System.out.println(beginTime);
		StringBuilder idStr = new StringBuilder();
		for(int i=0 ; i<idArray.length ; ++i) {
			//String sql = "update t_message set status=0 where id='" + idArray[i] + "'";
			if(i!=idArray.length-1) {
				idStr.append("'" + idArray[i] + "',");
			}else {
				idStr.append("'" + idArray[i] + "'");
			}
			
		}
		String sql = "update t_message set status=0 where id in (" + idStr.toString() + ")";
		//System.out.println(sql);
		Query query = entityManager.createNativeQuery(sql);
		query.executeUpdate();
		System.out.println(System.currentTimeMillis()-beginTime);
		System.out.println("OK");
	}

	@Override
	public void checkRepeat(List<Map<String, String>> list) {
		long latestPublishTime = flyInfoDao.getLatestPublishTime();
		
		List<String> ids = new ArrayList<>();
		for(Map<String,String> m : list) {
			for(String key : m.keySet()) {
				ids.add(key);
				//key是messageid m.get(key)是需要更新的flyinfo
				Message message = messageDao.findOne(key);
				String jsonMessage = message.getJsonMessage();
				
				JSONObject parseObject = JSON.parseObject(jsonMessage);
				flyInfoDao.updateMessageFieldByFlyInfoId(parseObject,m.get(key));
			}
		}
		
		//修改报文状态
		messageDao.check(ids);
		
		//更新redis
		List<FlyInfo> latestFlyInfoList = flyInfoDao.getFlyInfoListByPublishTime(latestPublishTime);
		redisTemplate.opsForValue().set("flyInfoList", latestFlyInfoList);
	}
	
}
