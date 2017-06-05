package cn.com.zlqf.airservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cn.com.zlqf.airservice.entity.User;

public interface UserDao extends JpaRepository<User, String>{
	
	@Query(value="select * from t_user where username=?1 and password=?2",nativeQuery=true)
	User findUserByUsernameAndPassword(String username,String password);

}
