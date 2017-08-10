package cn.com.zlqf.airservice.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cn.com.zlqf.airservice.entity.FlyDynamic;
import cn.com.zlqf.airservice.entity.Message;
import cn.com.zlqf.airservice.entity.User;

public interface MessageDao extends JpaRepository<Message, String>{
	
	@Query(value="select * from t_message where time=?1 order by status,addtime asc",nativeQuery=true)
	List<Message> getMessageList(String time);
	
	@Modifying
	@Query(value="update t_message set status=2 where id in (?1)",nativeQuery=true)
	int check(List<String> ids);
	
	@Query(value="select * from t_message where id in (?1)",nativeQuery=true)
	List<Message> findMessagesByIds(List<String> ids);
	
	
	@Modifying
	@Query(value="update t_message set status=1 where id in (?1)",nativeQuery=true)
	int ignore(List<String> ids);
	
	@Query(value="select * from t_message where time=?1 and status=2 order by addtime",nativeQuery=true)
	List<Message> getCheckedMessageList(String time);

	@Modifying
	@Query(value="update t_message set message=?1,jsonmessage=?2 where id=?3",nativeQuery=true)
	void updateMessageById(String message,String jsonMessage, String messageId);
	
	@Query(value="select jsonmessage from t_message where id in(?1)",nativeQuery=true)
	List<String> findJsonMessageListByIds(List<String> ids);
}
