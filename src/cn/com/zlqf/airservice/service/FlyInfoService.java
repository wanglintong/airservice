package cn.com.zlqf.airservice.service;

import java.util.List;

import cn.com.zlqf.airservice.entity.FlyInfo;

public interface FlyInfoService {
	
	void updateBaseField(List<FlyInfo> hasPublishFlyInfoList);

	List<FlyInfo> getFlyInfoListFromRedis();

	void publishAll(List<FlyInfo> flyInfoList);
	
	void addFlyInfo(List<FlyInfo> flyInfoList);
}
