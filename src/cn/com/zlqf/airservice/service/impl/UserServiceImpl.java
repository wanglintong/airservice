package cn.com.zlqf.airservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.zlqf.airservice.dao.UserDao;
import cn.com.zlqf.airservice.entity.User;
import cn.com.zlqf.airservice.service.UserService;

@Transactional(readOnly=true)
@Service
public class UserServiceImpl implements UserService{
	
	@Autowired
	private UserDao userDao;
	
	@Override
	public User findUserById(String id) {
		return userDao.findOne(id);
	}

	@Override
	public User findUserByUsernameAndPassword(String username, String password) {
		return userDao.findUserByUsernameAndPassword(username, password);
	}
}
