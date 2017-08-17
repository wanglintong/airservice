package cn.com.zlqf.airservice.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.alibaba.fastjson.JSONObject;

import cn.com.zlqf.airservice.entity.Message;
import cn.com.zlqf.airservice.utils.SQLUtils;

public class MessageDaoImpl implements MessageDataDao {
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public void check(List<String> ids) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; ids != null && i < ids.size(); ++i) {
			if(i!=0) {
				sb.append(",");
			}
			sb.append("'"+ids.get(i)+"'");
		}
		String checkTime = new Date().toLocaleString();
		String sql = "update t_message set status=2,checkTime='"+checkTime+"' where id in ("+sb.toString()+")";
		entityManager.createNativeQuery(sql).executeUpdate();
	}

	@Override
	public List<Message> findMessagesByIds(List<String> ids) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; ids != null && i < ids.size(); ++i) {
			if(i!=0) {
				sb.append(",");
			}
			sb.append("'"+ids.get(i)+"'");
		}
		String sql = "select * from t_message where id in("+sb.toString()+")";
		Query query = entityManager.createNativeQuery(sql, Message.class);
		List<Message> list = query.getResultList();
		return list;
	}

	@Override
	public List<Message> getMessageList(String time) {
		String sql = "select * from t_message where time='" + time + "' order by status,addtime asc";
		Query query = entityManager.createNativeQuery(sql, Message.class);
		List<Message> list = query.getResultList();
		return list;
	}
}
