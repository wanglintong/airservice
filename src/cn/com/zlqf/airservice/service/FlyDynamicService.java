package cn.com.zlqf.airservice.service;

import java.util.List;

import cn.com.zlqf.airservice.entity.FlyDynamic;

public interface FlyDynamicService {
	List<FlyDynamic> getListByTime(String time);
	void saveOrUpdate(FlyDynamic flyDynamic);
	void deleteByIds(String[] ids);
	void saveOrUpdate(List<FlyDynamic> flyDynamicList);
}
