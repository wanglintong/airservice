package cn.com.zlqf.airservice.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.zlqf.airservice.dao.FlyDynamicDao;
import cn.com.zlqf.airservice.dao.UserDao;
import cn.com.zlqf.airservice.entity.FlyDynamic;
import cn.com.zlqf.airservice.entity.User;
import cn.com.zlqf.airservice.service.FlyDynamicService;
import cn.com.zlqf.airservice.service.UserService;

@Transactional(readOnly=false)
@Service
public class FlyDynamicServiceImpl implements FlyDynamicService{
	
	@Autowired
	private FlyDynamicDao flyDynamicDao;
	
	@Transactional(readOnly=true)
	@Override
	public List<FlyDynamic> getListByTime(String time) {
		return flyDynamicDao.getFlyDynamicListByTime(time);
	}

	@Override
	public void saveOrUpdate(FlyDynamic flyDynamic) {
		flyDynamicDao.save(flyDynamic);
	}
	@Override
	public void saveOrUpdate(List<FlyDynamic> flyDynamicList) {
		flyDynamicDao.save(flyDynamicList);
	}

	@Override
	public void deleteByIds(String[] ids) {
		for(int i=0 ; ids!=null&&i<ids.length ; ++i) {
			flyDynamicDao.delete(ids[i]);
		}
	}	
}
