package cn.com.zlqf.airservice.service;

import cn.com.zlqf.airservice.entity.User;

public interface UserService {
	User findUserById(String id);
	User findUserByUsernameAndPassword(String username,String password);
}
