package cn.com.zlqf.airservice.service.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;

import cn.com.zlqf.airservice.dao.FlyInfoDao;
import cn.com.zlqf.airservice.entity.FlyInfo;
import cn.com.zlqf.airservice.entity.Message;
import cn.com.zlqf.airservice.service.FlyInfoService;
import cn.com.zlqf.airservice.service.MessageService;
import cn.com.zlqf.airservice.utils.DateUtils;


@Transactional(readOnly=false)
@Service
public class FlyInfoServiceImpl implements FlyInfoService{
	
	@Autowired
	private FlyInfoDao flyInfoDao;
	@Autowired
	private RedisTemplate<String,List<FlyInfo>> redisTemplate;
	
	@Override
	public void publishAll(List<FlyInfo> flyInfoList) {
		flyInfoDao.deleteAll();
		flyInfoDao.save(flyInfoList);
		
		//最新整表存入redis
		redisTemplate.opsForValue().set("flyInfoList", flyInfoList);  
	}
	
	@Override
	@Deprecated
	public void saveOrUpdate(List<FlyInfo> flyInfoList) {
		
	}
	
	@Override
	public void addFlyInfo(List<FlyInfo> flyInfoList) {
		flyInfoDao.save(flyInfoList);
		
		//更新redis整表
		List<FlyInfo> latestFlyInfoList = getFlyInfoListFromRedis();
		latestFlyInfoList.addAll(flyInfoList);
		redisTemplate.opsForValue().set("flyInfoList", latestFlyInfoList); 
	}
	
	@Override
	public void updateBaseField(List<FlyInfo> hasPublishFlyInfoList) {
		long latestPublishTime = this.getLatestPublishTime();
		for(int i=0 ; hasPublishFlyInfoList!=null&&i<hasPublishFlyInfoList.size() ; ++i) {
			FlyInfo flyInfo = hasPublishFlyInfoList.get(i);
			if(flyInfo.getEstimatedFly()==null) {
				flyInfo.setEstimatedFly(0l);
			}
			if(StringUtils.isNotBlank(flyInfo.getIncomingFlyNo())) {
				flyInfoDao.updateBaseFieldByIncomingFlyNo(flyInfo.getDepartureFlyNo(), flyInfo.getFlightLine(), flyInfo.getIncomingFlyNo(), flyInfo.getPlaneNo(), flyInfo.getPlaneType(),flyInfo.getEstimatedFly(),flyInfo.getTask(), flyInfo.getRemark(),latestPublishTime);
			}
			if(StringUtils.isNotBlank(flyInfo.getDepartureFlyNo())) {
				flyInfoDao.updateBaseFieldByDepartureFlyNo(flyInfo.getDepartureFlyNo(), flyInfo.getFlightLine(), flyInfo.getIncomingFlyNo(), flyInfo.getPlaneNo(), flyInfo.getPlaneType(),flyInfo.getEstimatedFly(),flyInfo.getTask(), flyInfo.getRemark(),latestPublishTime);
			}
		}
		//更新redis
		List<FlyInfo> flyInfoListByPublishTime = flyInfoDao.getFlyInfoListByPublishTime(latestPublishTime);
		redisTemplate.opsForValue().set("flyInfoList", flyInfoListByPublishTime); 
	}
	
	/**从redis中获取最新整表**/
	@Override
	public List<FlyInfo> getFlyInfoListFromRedis() {
		List<FlyInfo> flyInfoList = redisTemplate.opsForValue().get("flyInfoList");
		System.out.println("从redis中获取整表....");
		if(flyInfoList==null || flyInfoList.size()==0) {
			//redis中的整表失效，从数据库获取整表并更新redis
			long latestPublishTime = flyInfoDao.getLatestPublishTime();
			flyInfoList = flyInfoDao.getFlyInfoListByPublishTime(latestPublishTime);
			redisTemplate.opsForValue().set("flyInfoList", flyInfoList); 
			System.out.println("redis中获取整表失败,从数据库中获取整表....");
		}
		return flyInfoList;
	}

	@Override
	public long getLatestPublishTime() {
		return flyInfoDao.getLatestPublishTime();
	}

}
